package com.example.midnightgroove;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        ivPlayPause = findViewById(R.id.ivPlayPause);
        tvMiniSongTitle = findViewById(R.id.tvMiniSongTitle);
        tvMiniArtistName = findViewById(R.id.tvMiniArtistName);

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
        tvMiniSongTitle.setText(song.getTitle());
        tvMiniArtistName.setText(song.getArtist());
        ivPlayPause.setImageResource(android.R.drawable.ic_media_pause);
    }
}
