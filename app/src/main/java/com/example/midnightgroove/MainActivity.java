package com.example.midnightgroove;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.midnightgroove.HomeFragment;
import com.example.midnightgroove.LibraryFragment;
import com.example.midnightgroove.R;
import com.example.midnightgroove.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    private ImageView ivPlayPause;
    private TextView tvMiniSongTitle, tvMiniArtistName;
    private Song currentSong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        ivPlayPause = findViewById(R.id.ivPlayPause);
        tvMiniSongTitle = findViewById(R.id.tvMiniSongTitle);
        tvMiniArtistName = findViewById(R.id.tvMiniArtistName);
        View miniPlayerBar = findViewById(R.id.mini_player_bar);

        // تعيين شاشة الـ Home Fragment كشاشة افتراضية أول ما يفتح الـ MainActivity
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }

        ivPlayPause.setOnClickListener(v -> {
            MusicPlayer musicPlayer = MusicPlayer.getInstance(this);
            if (musicPlayer.isPlaying()) {
                musicPlayer.pause();
                ivPlayPause.setImageResource(android.R.drawable.ic_media_play);
            } else {
                musicPlayer.resume();
                ivPlayPause.setImageResource(android.R.drawable.ic_media_pause);
            }
        });

        miniPlayerBar.setOnClickListener(v -> {
            if (currentSong != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, PlayerFragment.newInstance(currentSong))
                        .addToBackStack(null)
                        .commit();
            }
        });

        // برمجة التنقل بين الـ Fragments عند الضغط على أزرار القائمة السفلية
        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                if (item.getItemId() == R.id.nav_home) {
                    selectedFragment = new HomeFragment();
                } else if (item.getItemId() == R.id.nav_search) {
                    selectedFragment = new SearchFragment();
                } else if (item.getItemId() == R.id.nav_library) {
                    selectedFragment = new LibraryFragment();
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, selectedFragment)
                            .commit();
                }
                return true;
            }
        });
    }

    public void updateMiniPlayer(Song song) {
        this.currentSong = song;
        tvMiniSongTitle.setText(song.getTitle());
        tvMiniArtistName.setText(song.getArtist());
        ivPlayPause.setImageResource(android.R.drawable.ic_media_pause);
        
        ImageView ivMiniAlbumArt = findViewById(R.id.ivMiniAlbumArt);
        if (song.getCoverUrl() != null && !song.getCoverUrl().isEmpty()) {
            Glide.with(this).load(song.getCoverUrl()).into(ivMiniAlbumArt);
        }
    }

    public void syncMiniPlayerState() {
        if (MusicPlayer.getInstance(this).isPlaying()) {
            ivPlayPause.setImageResource(android.R.drawable.ic_media_pause);
        } else {
            ivPlayPause.setImageResource(android.R.drawable.ic_media_play);
        }
    }
}
