package com.example.musicplayer.pojo;

import java.io.Serializable;

/**
 * Created by neevek on 7/20/14.
 */
public class Song implements Serializable {
    private static final long serialVersionUID = 8449538328972712286L;

    public long id;
    public String title;
    public String artist;
    public String album;
    public int duration;
    public String filePath;

    public Song(long id, String title, String artist, String album, int duration, String filePath) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.duration = duration;
        this.filePath = filePath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;

        Song song = (Song) o;

        if (id != song.id) return false;
        if (!filePath.equals(song.filePath)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + filePath.hashCode();
        return result;
    }
}
