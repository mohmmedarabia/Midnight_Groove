package com.example.midnightgroove;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        ImageView btnBack = view.findViewById(R.id.btnBack);
        TextView tvUserName = view.findViewById(R.id.tvUserName);
        TextView tvUserEmail = view.findViewById(R.id.tvUserEmail);
        Button btnLogout = view.findViewById(R.id.btnLogout);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            tvUserEmail.setText(user.getEmail());
            // If you have a name stored, you can set it here. 
            // For now, using the part of email before @ as name.
            String email = user.getEmail();
            if (email != null && email.contains("@")) {
                tvUserName.setText(email.split("@")[0]);
            }
        }

        btnBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        view.findViewById(R.id.llProfileLikedSongs).setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new LikedSongsFragment())
                    .addToBackStack(null)
                    .commit();
        });

        view.findViewById(R.id.llProfileRecents).setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new RecentlyPlayedFragment())
                    .addToBackStack(null)
                    .commit();
        });

        view.findViewById(R.id.llProfileTopArtists).setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new ArtistsFragment())
                    .addToBackStack(null)
                    .commit();
        });

        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        return view;
    }
}
