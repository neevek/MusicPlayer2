package com.example.musicplayer.biz;

import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.provider.MediaStore;
import com.example.musicplayer.MusicPlayerApplication;
import com.example.musicplayer.db.MusicDAO;
import com.example.musicplayer.lib.log.L;
import com.example.musicplayer.lib.util.Util;
import com.example.musicplayer.pojo.Song;
import com.example.musicplayer.pojo.SongCollection;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by neevek on 7/29/14.
 */
public class SongCollectionManager {
    private static SongCollectionManager sInstance;

    private SongCollection mSongCollection;

    public static SongCollectionManager getInstance() {
        if (sInstance == null) {
            synchronized (SongCollectionManager.class) {
                if (sInstance == null) {
                    sInstance = new SongCollectionManager();
                }
            }
        }

        return sInstance;
    }

    public synchronized SongCollection getSongCollection(boolean force) {
        if (force || mSongCollection == null) {
            loadSongs();
        }

        return mSongCollection;
    }

    private void loadSongs() {
        List<Song> songList = new ArrayList<Song>();
        Map<Long, Song> mSongMap = new HashMap<Long, Song>();

        try {
            // make sure the following code is exception free.

            Cursor cursor = MusicPlayerApplication.getInstance().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    , new String[]{ MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DATA }
                    , MediaStore.Audio.Media.IS_MUSIC + "!=0"
                    , null
                    , null
            );

            while (cursor.moveToNext()) {
                long id = cursor.getLong(0);
                String filePath = cursor.getString(1);

                File file = new File(filePath);
                if (!file.exists()) {
                    continue;
                }

                try {
                    MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                    mmr.setDataSource(filePath);

                    String duration = Util.ensureNotNull(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION), "");

                    int intDuration = Util.safeParseInt(duration, 0);

                    if (intDuration == 0) {
                        continue;
                    }

                    String title = Util.ensureNotNull(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE), file.getName());
                    String artist = Util.ensureNotNull(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST), "");
                    String album = Util.ensureNotNull(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM), "");

                    Song song = new Song(id, title, artist, album, intDuration, filePath);
                    songList.add(song);
                    mSongMap.put(id, song);

                    L.d("loaded song: %d, %s, %s, %s, %s", id, title, artist, album, duration);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mSongCollection = new SongCollection(songList, mSongMap, MusicDAO.getInstance().getFavorites());
    }
}
