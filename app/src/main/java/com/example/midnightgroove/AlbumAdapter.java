package com.example.midnightgroove;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder> {

    private List<Album> albumList;
    private OnAlbumClickListener listener;

    public interface OnAlbumClickListener {
        void onAlbumClick(Album album);
    }

    public AlbumAdapter(List<Album> albumList, OnAlbumClickListener listener) {
        this.albumList = albumList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album, parent, false);
        return new AlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumViewHolder holder, int position) {
        Album album = albumList.get(position);
        holder.tvTitle.setText(album.getTitle());
        holder.tvArtist.setText(album.getArtist());
        holder.itemView.setOnClickListener(v -> listener.onAlbumClick(album));
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    static class AlbumViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvArtist;

        public AlbumViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvAlbumTitle);
            tvArtist = itemView.findViewById(R.id.tvAlbumArtist);
        }
    }
}
