package com.example.midnightgroove;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import java.util.Locale;

public class PlayerFragment extends Fragment {

    private String songTitle, artistName, coverUrl;
    private ImageView btnPlayPause, btnBack, btnNext, btnPrevious, ivAlbumArt;
    private TextView tvTitle, tvArtist, tvCurrentTime, tvTotalTime;
    private SeekBar playerSeekBar;
    private MusicPlayer musicPlayer;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable updateSeekBar = new Runnable() {
        @Override
        public void run() {
            if (musicPlayer != null && musicPlayer.getExoPlayer() != null) {
                long currentPos = musicPlayer.getExoPlayer().getCurrentPosition();
                long duration = musicPlayer.getExoPlayer().getDuration();
                
                playerSeekBar.setProgress((int) currentPos);
                tvCurrentTime.setText(formatTime(currentPos));
                if (duration > 0) {
                    playerSeekBar.setMax((int) duration);
                    tvTotalTime.setText(formatTime(duration));
                }
            }
            handler.postDelayed(this, 1000);
        }
    };

    public static PlayerFragment newInstance(Song song) {
        PlayerFragment fragment = new PlayerFragment();
        Bundle args = new Bundle();
        args.putString("title", song.getTitle());
        args.putString("artist", song.getArtist());
        args.putString("cover", song.getCoverUrl());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            songTitle = getArguments().getString("title");
            artistName = getArguments().getString("artist");
            coverUrl = getArguments().getString("cover");
        }
        musicPlayer = MusicPlayer.getInstance(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player, container, false);

        btnBack = view.findViewById(R.id.btnBack);
        btnPlayPause = view.findViewById(R.id.btnPlayPauseLarge);
        btnNext = view.findViewById(R.id.btnNext);
        btnPrevious = view.findViewById(R.id.btnPrevious);
        ivAlbumArt = view.findViewById(R.id.ivAlbumArtLarge);
        tvTitle = view.findViewById(R.id.tvSongTitle);
        tvArtist = view.findViewById(R.id.tvArtistName);
        tvCurrentTime = view.findViewById(R.id.tvCurrentTime);
        tvTotalTime = view.findViewById(R.id.tvTotalTime);
        playerSeekBar = view.findViewById(R.id.playerSeekBar);

        tvTitle.setText(songTitle);
        tvArtist.setText(artistName);

        if (coverUrl != null && !coverUrl.isEmpty()) {
            Glide.with(this).load(coverUrl).placeholder(R.color.spotify_card_bg).into(ivAlbumArt);
        }

        updatePlayPauseButton();

        btnBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        btnPlayPause.setOnClickListener(v -> {
            if (musicPlayer.isPlaying()) {
                musicPlayer.pause();
            } else {
                musicPlayer.resume();
            }
            updatePlayPauseButton();
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).syncMiniPlayerState();
            }
        });

        btnNext.setOnClickListener(v -> Toast.makeText(getContext(), "Next song", Toast.LENGTH_SHORT).show());
        btnPrevious.setOnClickListener(v -> Toast.makeText(getContext(), "Previous song", Toast.LENGTH_SHORT).show());

        playerSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    musicPlayer.getExoPlayer().seekTo(progress);
                    tvCurrentTime.setText(formatTime(progress));
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                handler.removeCallbacks(updateSeekBar);
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                handler.post(updateSeekBar);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.post(updateSeekBar);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(updateSeekBar);
    }

    private void updatePlayPauseButton() {
        if (musicPlayer.isPlaying()) {
            btnPlayPause.setImageResource(android.R.drawable.ic_media_pause);
        } else {
            btnPlayPause.setImageResource(android.R.drawable.ic_media_play);
        }
    }

    private String formatTime(long millis) {
        int seconds = (int) (millis / 1000) % 60;
        int minutes = (int) (millis / (1000 * 60)) % 60;
        return String.format(Locale.getDefault(), "%d:%02d", minutes, seconds);
    }
}
