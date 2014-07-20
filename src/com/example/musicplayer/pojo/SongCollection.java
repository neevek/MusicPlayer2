package com.example.musicplayer.pojo;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by neevek on 7/20/14.
 */
public class SongCollection {
    private List<Song> mSongCollection;

    public enum CollectionType {
        ARTIST, ALBUM;

        private Map<String, List<Song>> collectionMap;

        public void setCollectionMap(Map<String, List<Song>> collectionMap) {
            this.collectionMap = collectionMap;
        }

        public Map<String, List<Song>> getCollectionMap() {
            return collectionMap;
        }
    }

    public SongCollection(List<Song> songCollection) {
        mSongCollection = songCollection;
    }

    public List<Song> getSongCollection() {
        return mSongCollection;
    }

    public Map<String, List<Song>> getSongCollectionByType(CollectionType type) {
        if (type.collectionMap != null) {
            return type.collectionMap;
        }

        Map<String, List<Song>> map = new HashMap<String, List<Song>>();
        for (int i = 0; i < mSongCollection.size(); ++i) {
            Song song = mSongCollection.get(i);

            String strType = type == CollectionType.ALBUM ? song.album : song.artist;
            if (!TextUtils.isEmpty(strType)) {
                List<Song> tmpList = map.get(strType);
                if (tmpList == null) {
                    tmpList = new ArrayList<Song>();
                    map.put(strType, tmpList);
                }

                tmpList.add(song);
            }
        }

        type.setCollectionMap(map);

        return map;
    }
}
