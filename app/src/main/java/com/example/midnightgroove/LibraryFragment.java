package com.example.midnightgroove;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

public class LibraryFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_library, container, false);

        ImageView btnAddSong = view.findViewById(R.id.btnAddSong);
        btnAddSong.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddSongActivity.class);
            startActivity(intent);
        });

        ImageView ivProfile = view.findViewById(R.id.ivProfile);
        ivProfile.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new ProfileFragment())
                    .addToBackStack(null)
                    .commit();
        });

        view.findViewById(R.id.tvFilterPlaylists).setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new PlaylistsFragment())
                    .addToBackStack(null)
                    .commit();
        });

        view.findViewById(R.id.tvFilterArtists).setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new ArtistsFragment())
                    .addToBackStack(null)
                    .commit();
        });

        View llLikedSongs = view.findViewById(R.id.llLikedSongs);
        llLikedSongs.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new LikedSongsFragment())
                    .addToBackStack(null)
                    .commit();
        });

        view.findViewById(R.id.llRecentItem).setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new RecentlyPlayedFragment())
                    .addToBackStack(null)
                    .commit();
        });

        View tvFilterAlbums = view.findViewById(R.id.tvFilterAlbums);
        tvFilterAlbums.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new AlbumsFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }
}
