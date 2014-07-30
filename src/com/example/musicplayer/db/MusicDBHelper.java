package com.example.musicplayer.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by neevek on 7/30/14.
 */
public class MusicDBHelper extends SQLiteOpenHelper {
    private final static String DB_NAME = "musicplayer.db";
    private final static int VERSION = 1;

    public MusicDBHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS favorite_list " +
                "(id integer primary key, title text, artist text, " +
                "album text, duration int, file_path text, timestamp DATETIME)";

        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
