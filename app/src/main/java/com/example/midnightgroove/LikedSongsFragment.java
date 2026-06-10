package com.example.midnightgroove;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class LikedSongsFragment extends Fragment {

    private RecyclerView rvLikedSongs;
    private SongAdapter songAdapter;
    private List<Song> songList;
    private FirebaseFirestore db;
    private ImageView btnBack;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_liked_songs, container, false);

        rvLikedSongs = view.findViewById(R.id.rvLikedSongs);
        btnBack = view.findViewById(R.id.btnBack);

        rvLikedSongs.setLayoutManager(new LinearLayoutManager(getContext()));
        songList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

        songAdapter = new SongAdapter(songList, song -> {
            MusicPlayer.getInstance(getContext()).playSong(song.getSongUrl());
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).updateMiniPlayer(song);
            }
        });
        rvLikedSongs.setAdapter(songAdapter);

        btnBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        loadLikedSongs();

        return view;
    }

    private void loadLikedSongs() {
        // For now, loading all songs as a placeholder. 
        // In a real app, you'd filter by a 'liked' field or a user-specific collection.
        db.collection("songs").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                songList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Song song = document.toObject(Song.class);
                    songList.add(song);
                }
                songAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getContext(), "Error getting liked songs", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
