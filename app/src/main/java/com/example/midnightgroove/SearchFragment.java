package com.example.midnightgroove;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;
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

public class SearchFragment extends Fragment {

    private EditText etSearch;
    private RecyclerView rvSearchResults;
    private TextView tvBrowseAll;
    private GridLayout glGenres;
    private SongAdapter songAdapter;
    private List<Song> songList;
    private List<Song> filteredList;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        etSearch = view.findViewById(R.id.etSearch);
        rvSearchResults = view.findViewById(R.id.rvSearchResults);
        tvBrowseAll = view.findViewById(R.id.tvBrowseAll);
        glGenres = view.findViewById(R.id.glGenres);

        rvSearchResults.setLayoutManager(new LinearLayoutManager(getContext()));
        songList = new ArrayList<>();
        filteredList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

        songAdapter = new SongAdapter(filteredList, song -> {
            MusicPlayer.getInstance(getContext()).playSong(song.getSongUrl());
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).updateMiniPlayer(song);
            }
        });
        rvSearchResults.setAdapter(songAdapter);

        loadAllSongs();

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterSongs(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        View.OnClickListener albumClickListener = v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new AlbumsFragment())
                    .addToBackStack(null)
                    .commit();
        };

        view.findViewById(R.id.llGenrePop).setOnClickListener(albumClickListener);
        view.findViewById(R.id.llGenreHipHop).setOnClickListener(albumClickListener);
        view.findViewById(R.id.llGenrePodcasts).setOnClickListener(albumClickListener);
        view.findViewById(R.id.llGenreRock).setOnClickListener(albumClickListener);

        return view;
    }

    private void loadAllSongs() {
        db.collection("songs").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                songList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Song song = document.toObject(Song.class);
                    songList.add(song);
                }
            }
        });
    }

    private void filterSongs(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            rvSearchResults.setVisibility(View.GONE);
            tvBrowseAll.setVisibility(View.VISIBLE);
            glGenres.setVisibility(View.VISIBLE);
        } else {
            rvSearchResults.setVisibility(View.VISIBLE);
            tvBrowseAll.setVisibility(View.GONE);
            glGenres.setVisibility(View.GONE);
            for (Song song : songList) {
                if (song.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                    song.getArtist().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(song);
                }
            }
        }
        songAdapter.notifyDataSetChanged();
    }
}
