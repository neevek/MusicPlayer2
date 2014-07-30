package com.example.musicplayer.fragment;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.example.musicplayer.R;
import com.example.musicplayer.biz.SongCollectionManager;
import com.example.musicplayer.db.MusicDAO;
import com.example.musicplayer.lib.task.TaskExecutor;
import com.example.musicplayer.pojo.Song;

import java.util.List;

/**
 * Created by neevek on 7/20/14.
 */
public class SongListFragment extends BaseMusicListFragment implements AdapterView.OnItemClickListener {
    private ListType mListType;
    private List<Song> mSongList;

    public static enum ListType {
        SONG_LIST,          // show context menu for "Add to favorite"
        FAVORITE_LIST,      // show context menu for "Remove from favorite"
    }

    public SongListFragment(String title, MusicListItemHandler fetcher, ListType listType, List<Song> songList) {
        super(title, fetcher);
        mListType = listType;
        mSongList = songList;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ListView listView = (ListView)view.findViewById(R.id.lv_music_list);
        registerForContextMenu(listView);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        switch (mListType) {
            case SONG_LIST: {
                menu.addSubMenu("添加收藏");
                break;
            }
            case FAVORITE_LIST: {
                menu.addSubMenu("删除收藏");
                break;
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch (mListType) {
            case SONG_LIST: {
                final Song song = mSongList.get(info.position);
                if (song != null) {
                    SongCollectionManager.getInstance().getSongCollection(false).getFavoriteSongs().add(0, song);
                    TaskExecutor.executeTask(new Runnable() {
                        @Override
                        public void run() {
                            MusicDAO.getInstance().addFavorite(song);
                        }
                    });

                    Toast.makeText(getActivity(), "添加收藏成功", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case FAVORITE_LIST: {
                final Song song = mSongList.get(info.position);
                if (song != null) {
                    SongCollectionManager.getInstance().getSongCollection(false).getFavoriteSongs().remove(song);
                    TaskExecutor.executeTask(new Runnable() {
                        @Override
                        public void run() {
                            MusicDAO.getInstance().deleteFavorite(song.id);
                        }
                    });

                    Toast.makeText(getActivity(), "删除收藏成功", Toast.LENGTH_SHORT).show();

                    getMusicListAdapter().notifyDataSetChanged();
                }
                break;
            }
        }
        return false;
    }
}
