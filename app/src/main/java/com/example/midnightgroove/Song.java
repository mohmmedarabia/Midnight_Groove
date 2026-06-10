package com.example.midnightgroove;

public class Song {
    private String id;
    private String title;
    private String artist;
    private String songUrl;
    private String coverUrl;

    public Song() {
        // Required for Firebase
    }

    public Song(String id, String title, String artist, String songUrl, String coverUrl) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.songUrl = songUrl;
        this.coverUrl = coverUrl;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getArtist() { return artist; }
    public void setArtist(String artist) { this.artist = artist; }

    public String getSongUrl() { return songUrl; }
    public void setSongUrl(String songUrl) { this.songUrl = songUrl; }

    public String getCoverUrl() { return coverUrl; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }
}
