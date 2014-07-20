package com.example.musicplayer.pojo;

import android.text.TextUtils;

import java.util.*;

/**
 * Created by neevek on 7/20/14.
 */
public class SongCollection {
    private List<Song> mAllSongs;

    public enum CollectionType {
        ARTIST("歌手"), ALBUM("专辑");

        private String title;
        private Map<String, List<Song>> collectionMap;

        private CollectionType(String title) {
            this.title = title;
        }

        public void setCollectionMap(Map<String, List<Song>> collectionMap) {
            this.collectionMap = collectionMap;
        }

        public Map<String, List<Song>> getCollectionMap() {
            return collectionMap;
        }

        public String getTitle() {
            return title;
        }
    }

    public SongCollection(List<Song> allSongs) {
        mAllSongs = allSongs;
    }

    public List<Song> getAllSongs() {
        return mAllSongs;
    }

    public Map<String, List<Song>> getSongCollectionByType(CollectionType type) {
        if (type.collectionMap != null) {
            return type.collectionMap;
        }

        Map<String, List<Song>> map = new TreeMap<String, List<Song>>();
        for (int i = 0; i < mAllSongs.size(); ++i) {
            Song song = mAllSongs.get(i);

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
