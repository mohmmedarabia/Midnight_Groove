package com.example.midnightgroove; // تأكد أن هذا السطر يطابق اسم الباكج الحقيقي عندك فوق

import android.os.Bundle;
import android.view.MenuItem;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        // تعيين شاشة الـ Home Fragment كشاشة افتراضية أول ما يفتح الـ MainActivity
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }

        // برمجة التنقل بين الـ Fragments عند الضغط على أزرار القائمة السفلية
        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                // فحص الأيقونة التي تم الضغط عليها باستخدام الـ ID
                if (item.getItemId() == R.id.nav_home) {
                    selectedFragment = new HomeFragment();
                } else if (item.getItemId() == R.id.nav_search) {
                    selectedFragment = new SearchFragment();
                } else if (item.getItemId() == R.id.nav_library) {
                    selectedFragment = new LibraryFragment();
                }

                // تبديل الـ Fragment الحالي بالـ Fragment المختار
                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, selectedFragment)
                            .commit();
                }
                return true;
            }
        });
    }
}