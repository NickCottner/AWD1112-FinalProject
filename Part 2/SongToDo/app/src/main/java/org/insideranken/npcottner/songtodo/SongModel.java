package org.insideranken.npcottner.songtodo;

public class SongModel {
    String title;
    String artist;
    String id;
    String date;

    public SongModel() {
    }

    public SongModel(String title, String artist, String id, String date) {
        this.title = title;
        this.artist = artist;
        this.id = id;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}