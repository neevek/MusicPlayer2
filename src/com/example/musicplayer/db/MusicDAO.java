package com.example.musicplayer.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.musicplayer.MusicPlayerApplication;
import com.example.musicplayer.pojo.Song;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by neevek on 7/30/14.
 */
public class MusicDAO {
    private static MusicDAO sInstance;
    private MusicDBHelper mMusicDBHelper;

    public MusicDAO(Context context) {
        mMusicDBHelper = new MusicDBHelper(context);
    }

    public static MusicDAO getInstance() {
        if (sInstance == null) {
            synchronized (MusicDAO.class) {
                if (sInstance == null) {
                    sInstance = new MusicDAO(MusicPlayerApplication.getInstance());
                }
            }
        }

        return sInstance;
    }

    public boolean addFavorite(Song song) {
        try {
            SQLiteDatabase db = mMusicDBHelper.getWritableDatabase();

            String sql = "INSERT OR IGNORE INTO favorite_list (id, title, " +
                    "artist, album, duration, file_path, timestamp)" +
                    " VALUES (?, ?, ?, ?, ?, ?, ?)";

            db.execSQL(sql, new Object[]{
                    song.id,
                    song.title,
                    song.artist,
                    song.album,
                    song.duration,
                    song.filePath,
                    System.currentTimeMillis()
            });

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean deleteFavorite(long id) {
        try {
            SQLiteDatabase db = mMusicDBHelper.getWritableDatabase();

            String sql = "DELETE FROM favorite_list WHERE id=" + id;

            db.execSQL(sql);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<Song> getFavorites() {
        List<Song> songList = new ArrayList<Song>();

        Cursor cursor = null;
        try {
            SQLiteDatabase db = mMusicDBHelper.getReadableDatabase();

            String sql = "SELECT id, title, artist, album, duration, file_path FROM favorite_list ORDER BY timestamp DESC";
            cursor = db.rawQuery(sql, null);

            while (cursor.moveToNext()) {
                Song song = new Song();
                song.id = cursor.getLong(0);
                song.title = cursor.getString(1);
                song.artist = cursor.getString(2);
                song.album = cursor.getString(3);
                song.duration = cursor.getInt(4);
                song.filePath = cursor.getString(5);

                songList.add(song);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return songList;
    }
}
