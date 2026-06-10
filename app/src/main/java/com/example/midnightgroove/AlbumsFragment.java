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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AlbumsFragment extends Fragment {

    private RecyclerView rvAlbums;
    private AlbumAdapter albumAdapter;
    private List<Album> albumList;
    private FirebaseFirestore db;
    private ImageView btnBack;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_albums, container, false);

        rvAlbums = view.findViewById(R.id.rvAlbums);
        btnBack = view.findViewById(R.id.btnBack);

        rvAlbums.setLayoutManager(new GridLayoutManager(getContext(), 2));
        albumList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

        albumAdapter = new AlbumAdapter(albumList, album -> {
            Toast.makeText(getContext(), "Selected: " + album.getTitle(), Toast.LENGTH_SHORT).show();
            // Navigate to Album detail if needed
        });
        rvAlbums.setAdapter(albumAdapter);

        btnBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        loadAlbums();

        return view;
    }

    private void loadAlbums() {
        db.collection("albums").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                albumList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Album album = document.toObject(Album.class);
                    albumList.add(album);
                }
                albumAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getContext(), "Error getting albums", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
