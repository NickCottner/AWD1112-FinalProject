package org.insideranken.npcottner.songtodo;

public class SongModel {
    String title;
    String artist;
    String id;
    String year;

    public SongModel() {
    }

    public SongModel(String title, String artist, String id, String year) {
        this.title = title;
        this.artist = artist;
        this.id = id;
        this.year = year;
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

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }
}
