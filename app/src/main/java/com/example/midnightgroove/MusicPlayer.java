package com.example.midnightgroove;

import android.content.Context;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;

public class MusicPlayer {
    private static MusicPlayer instance;
    private ExoPlayer exoPlayer;

    private MusicPlayer(Context context) {
        exoPlayer = new ExoPlayer.Builder(context).build();
    }

    public static synchronized MusicPlayer getInstance(Context context) {
        if (instance == null) {
            instance = new MusicPlayer(context.getApplicationContext());
        }
        return instance;
    }

    public void playSong(String url) {
        MediaItem mediaItem = MediaItem.fromUri(url);
        exoPlayer.setMediaItem(mediaItem);
        exoPlayer.prepare();
        exoPlayer.play();
    }

    public void pause() {
        exoPlayer.pause();
    }

    public void resume() {
        exoPlayer.play();
    }

    public boolean isPlaying() {
        return exoPlayer.isPlaying();
    }

    public ExoPlayer getExoPlayer() {
        return exoPlayer;
    }
}
