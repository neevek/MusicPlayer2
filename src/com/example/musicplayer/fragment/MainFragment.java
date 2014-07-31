package com.example.musicplayer.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.*;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import com.example.musicplayer.AboutActivity;
import com.example.musicplayer.R;
import com.example.musicplayer.biz.SongCollectionManager;
import com.example.musicplayer.lib.task.TaskExecutor;
import com.example.musicplayer.lib.util.Util;
import com.example.musicplayer.pojo.Song;
import com.example.musicplayer.pojo.SongCollection;
import com.example.musicplayer.service.MusicPlayerService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by neevek on 7/20/14.
 */
public class MainFragment extends Fragment implements AdapterView.OnItemClickListener {
    private Menu mMenu;

    private GridView mGridView;
    private MainGridViewItemAdapter mAdapter;

    private final static String[] MENU_ITEM_TEXT = new String[]{"全部歌曲", "歌手", "专辑", "收藏"};
    private final static int[] MENU_ITEM_ICON = new int[] { R.drawable.icon_music, R.drawable.icon_artist, R.drawable.icon_album, R.drawable.icon_favorite};

    private String mTitle;

    private SongCollection mSongCollection;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mTitle = getResources().getString(R.string.app_name);
        getActivity().setTitle(mTitle);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle("音乐播放器");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.gv_main, null, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mGridView = (GridView) view;
        mGridView.setOnItemClickListener(this);
        mAdapter = new MainGridViewItemAdapter();
        mGridView.setAdapter(mAdapter);
    }

    private void scanSongs(final boolean force) {
        if (!force && mSongCollection != null) {
            return;
        }

        mMenu.getItem(0).setEnabled(false);
        getActivity().setProgressBarIndeterminateVisibility(true);

        TaskExecutor.executeTask(new Runnable() {
            @Override
            public void run() {
                final SongCollection songCollection = SongCollectionManager.getInstance().getSongCollection(force);

                TaskExecutor.runTaskOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mMenu.getItem(0).setEnabled(true);
                        getActivity().setProgressBarIndeterminateVisibility(false);

                        mSongCollection = songCollection;
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    private class MainGridViewItemAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return MENU_ITEM_TEXT.length;
        }

        @Override
        public Object getItem(int position) {
            return MENU_ITEM_TEXT[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.gv_main_item, null);

                convertView.setTag(convertView);
            }

            TextView tvMenuItem = (TextView)convertView;
            BitmapDrawable bitmapDrawable = (BitmapDrawable)getResources().getDrawable(MENU_ITEM_ICON[position]);
            bitmapDrawable.setBounds(0, 0, bitmapDrawable.getIntrinsicWidth(), bitmapDrawable.getIntrinsicHeight());
            tvMenuItem.setCompoundDrawables(null, bitmapDrawable, null, null);

            if (mSongCollection != null) {
                switch (position) {
                    case 0:
                        tvMenuItem.setText(MENU_ITEM_TEXT[position] + "(" + mSongCollection.getAllSongs().size() + ")");
                        break;
                    case 1:
                        tvMenuItem.setText(MENU_ITEM_TEXT[position] + "(" + mSongCollection.getSongCollectionByType(SongCollection.CollectionType.ARTIST).size() + ")");
                        break;
                    case 2:
                        tvMenuItem.setText(MENU_ITEM_TEXT[position] + "(" + mSongCollection.getSongCollectionByType(SongCollection.CollectionType.ALBUM).size() + ")");
                        break;
                    case 3:
                        tvMenuItem.setText(MENU_ITEM_TEXT[position] + "(" + mSongCollection.getFavoriteSongs().size() + ")");
                        break;
                }
            } else {
                tvMenuItem.setText(MENU_ITEM_TEXT[position] + "(0)");
            }

            return convertView;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0: {
                pushMusicListFragmentWithSongList("全部歌曲", SongListFragment.ListType.SONG_LIST, mSongCollection.getAllSongs());
                break;
            }
            case 1: {
                pushCategorizedMusicListFragment(SongCollection.CollectionType.ARTIST);
                break;
            }
            case 2: {
                pushCategorizedMusicListFragment(SongCollection.CollectionType.ALBUM);
                break;
            }
            case 3: {
                pushMusicListFragmentWithSongList("收藏", SongListFragment.ListType.FAVORITE_LIST, mSongCollection.getFavoriteSongs());
                break;
            }
        }
    }

    private void pushMusicListFragmentWithSongList(String title, SongListFragment.ListType listType, final List<Song> songList) {
        pushFragment(new SongListFragment(title, new BaseMusicListFragment.MusicListItemHandler() {
            @Override
            public int getCount() {
                return songList.size();
            }

            @Override
            public String getItemTitle(int position) {
                return songList.get(position).title;
            }

            @Override
            public String getItemSubtitle(int position) {
                Song song = songList.get(position);
                return "[" + Util.formatMilliseconds(song.duration, null) + "] by " + song.artist;
            }

            @Override
            public void onItemClick(int position) {
//                Toast.makeText(getActivity(), "Start playing...", Toast.LENGTH_SHORT).show();
                Song song = songList.get(position);

                Intent intent = new Intent(getActivity(), MusicPlayerService.class);
                intent.putExtra(MusicPlayerService.EXTRA_SONG_ID, song.id);
                intent.putExtra(MusicPlayerService.EXTRA_PLAYING_PROGRESS, 0);
                getActivity().startService(intent);
            }
        }, listType, songList));
    }

    private void pushCategorizedMusicListFragment(SongCollection.CollectionType type) {
        final Map<String, List<Song>> map = mSongCollection.getSongCollectionByType(type);
        final List<String> keyList = new ArrayList<String>(map.keySet());
        pushFragment(new BaseMusicListFragment(type.getTitle(), new BaseMusicListFragment.MusicListItemHandler() {
            @Override
            public int getCount() {
                return keyList.size();
            }

            @Override
            public String getItemTitle(int position) {
                return keyList.get(position);
            }

            @Override
            public String getItemSubtitle(int position) {
                return map.get(keyList.get(position)).size() + "首歌";
            }

            @Override
            public void onItemClick(int position) {
                String key = keyList.get(position);
                List<Song> songList = map.get(key);
                pushMusicListFragmentWithSongList(key, SongListFragment.ListType.SONG_LIST, songList);
            }
        }));
    }

    private void pushFragment(Fragment fragment) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        ft.replace(R.id.container, fragment);

        ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);

        ft.commitAllowingStateLoss();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        mMenu = menu;

        scanSongs(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_scan:
                scanSongs(true);
                break;
            case R.id.action_quit:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("退出")
                        .setMessage("确定退出音乐播放器吗?")
                        .setCancelable(true)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getActivity().finish();
                            }
                        })
                        .setNegativeButton("取消", null);
                builder.create().show();
                return true;
            case R.id.action_about:
                Intent intent = new Intent(getActivity(), AboutActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}
