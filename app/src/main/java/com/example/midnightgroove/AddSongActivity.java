package com.example.midnightgroove;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;

public class AddSongActivity extends AppCompatActivity {

    private EditText etSongTitle, etArtistName, etYoutubeUrl;
    private Button btnUpload;
    private TextView tvFileName;
    private ProgressBar progressBar;
    private Uri audioUri;

    private FirebaseStorage storage;
    private FirebaseFirestore db;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private final ActivityResultLauncher<String> pickAudioLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    audioUri = uri;
                    tvFileName.setText("File Selected: " + uri.getLastPathSegment());
                    etYoutubeUrl.setText("");
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_song);

        etSongTitle = findViewById(R.id.etSongTitle);
        etArtistName = findViewById(R.id.etArtistName);
        etYoutubeUrl = findViewById(R.id.etYoutubeUrl);
        btnUpload = findViewById(R.id.btnUpload);
        tvFileName = findViewById(R.id.tvFileName);
        progressBar = findViewById(R.id.progressBar);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();

        findViewById(R.id.btnSelectFile).setOnClickListener(v -> pickAudioLauncher.launch("audio/*"));

        btnUpload.setOnClickListener(v -> {
            String ytUrl = etYoutubeUrl.getText().toString().trim();
            if (!ytUrl.isEmpty()) {
                extractAndUploadFromYoutube(ytUrl);
            } else if (audioUri != null) {
                uploadLocalFile();
            } else {
                Toast.makeText(this, "Please select a file or paste a YouTube link", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private void extractAndUploadFromYoutube(String youtubeUrl) {
        android.util.Log.d("AddSong", "Starting extraction for: " + youtubeUrl);
        if (!youtubeUrl.startsWith("http")) {
            showError("Invalid YouTube URL. Make sure it starts with http:// or https://");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnUpload.setEnabled(false);

        new YouTubeExtractor(this) {
            @Override
            public void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta videoMeta) {
                if (ytFiles != null) {
                    android.util.Log.d("AddSong", "Extraction complete. Found " + ytFiles.size() + " files.");
                    // itag 140 is m4a audio (128kbps)
                    YtFile audioFile = ytFiles.get(140);
                    if (audioFile == null) {
                        android.util.Log.d("AddSong", "itag 140 not found, searching fallbacks...");
                        for (int i = 0; i < ytFiles.size(); i++) {
                            YtFile file = ytFiles.valueAt(i);
                            if (file != null && file.getFormat().getHeight() == -1) {
                                audioFile = file;
                                android.util.Log.d("AddSong", "Found fallback audio: " + file.getFormat().getItag());
                                break;
                            }
                        }
                    }

                    if (audioFile != null) {
                        android.util.Log.d("AddSong", "Downloading audio from: " + audioFile.getUrl());
                        String videoTitle = (videoMeta != null) ? videoMeta.getTitle() : "YouTube Song";
                        downloadAndUpload(audioFile.getUrl(), videoTitle);
                    } else {
                        showError("Could not find a suitable audio stream for this video");
                    }
                } else {
                    android.util.Log.e("AddSong", "Extraction failed: ytFiles is null");
                    showError("Extraction failed. Please check your internet connection and the YouTube link.");
                }
            }
        }.extract(youtubeUrl, true, true);
    }

    private void downloadAndUpload(String downloadUrl, String defaultTitle) {
        String title = etSongTitle.getText().toString().trim();
        final String finalTitle = title.isEmpty() ? defaultTitle : title;
        android.util.Log.d("AddSong", "Downloading: " + finalTitle);

        executorService.execute(() -> {
            try {
                URL url = new URL(downloadUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.connect();
                android.util.Log.d("AddSong", "Connected to download URL");

                File tempFile = File.createTempFile("yt_audio", ".m4a", getCacheDir());
                try (InputStream in = new BufferedInputStream(conn.getInputStream());
                     FileOutputStream out = new FileOutputStream(tempFile)) {
                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    while ((bytesRead = in.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                    }
                }
                android.util.Log.d("AddSong", "Download complete: " + tempFile.getAbsolutePath());
                
                runOnUiThread(() -> uploadToFirebase(Uri.fromFile(tempFile), finalTitle));

            } catch (Exception e) {
                android.util.Log.e("AddSong", "Download error: " + e.getMessage());
                runOnUiThread(() -> showError("Download failed: " + e.getMessage()));
            }
        });
    }

    private void uploadLocalFile() {
        String title = etSongTitle.getText().toString().trim();
        if (title.isEmpty()) {
            Toast.makeText(this, "Please enter a song title", Toast.LENGTH_SHORT).show();
            return;
        }
        uploadToFirebase(audioUri, title);
    }

    private void uploadToFirebase(Uri uri, String title) {
        progressBar.setVisibility(View.VISIBLE);
        btnUpload.setEnabled(false);

        String artist = etArtistName.getText().toString().trim();
        if (artist.isEmpty()) artist = "YouTube Artist";

        String songId = UUID.randomUUID().toString();
        StorageReference ref = storage.getReference().child("songs/" + songId);

        String finalArtist = artist;
        ref.putFile(uri)
                .addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                    saveSongToFirestore(songId, title, finalArtist, downloadUri.toString());
                }))
                .addOnFailureListener(e -> showError("Upload failed: " + e.getMessage()));
    }

    private void saveSongToFirestore(String id, String title, String artist, String url) {
        Song song = new Song(id, title, artist, url, "");
        db.collection("songs").document(id)
                .set(song)
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Song added successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> showError("Firestore error: " + e.getMessage()));
    }

    private void showError(String message) {
        progressBar.setVisibility(View.GONE);
        btnUpload.setEnabled(true);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
