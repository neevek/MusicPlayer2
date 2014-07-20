package com.example.musicplayer.pojo;

/**
 * Created by neevek on 7/20/14.
 */
public class Song {
    public String title;
    public String artist;
    public String album;
    public int duration;
    public String path;

    public Song(String title, String artist, String album, int duration, String path) {
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.duration = duration;
        this.path = path;
    }
}
