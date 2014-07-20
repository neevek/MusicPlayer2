package com.example.musicplayer.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.*;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import com.example.musicplayer.R;
import com.example.musicplayer.lib.log.L;
import com.example.musicplayer.lib.task.TaskExecutor;
import com.example.musicplayer.lib.util.Util;
import com.example.musicplayer.pojo.Song;
import com.example.musicplayer.pojo.SongCollection;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by neevek on 7/20/14.
 */
public class MainFragment extends Fragment implements AdapterView.OnItemClickListener {
    private Menu mMenu;

    private GridView mGridView;
    private MainGridViewItemAdapter mAdapter;

    private final static String[] MENU_ITEM_TEXT = new String[]{"全部音乐", "歌手", "专辑"};
    private final static int[] MENU_ITEM_ICON = new int[] { R.drawable.icon_music, R.drawable.icon_artist, R.drawable.icon_album };


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

    private void scanSongs() {
        mMenu.getItem(0).setEnabled(false);
        getActivity().setProgressBarIndeterminateVisibility(true);

        TaskExecutor.executeTask(new Runnable() {
            @Override
            public void run() {
                final SongCollection songCollection = loadSongs();

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
                        tvMenuItem.setText(MENU_ITEM_TEXT[position] + "(" + mSongCollection.getSongCollection().size() + ")");
                        break;
                    case 1:
                        tvMenuItem.setText(MENU_ITEM_TEXT[position] + "(" + mSongCollection.getSongCollectionByType(SongCollection.CollectionType.ARTIST).size() + ")");
                        break;
                    case 2:
                        tvMenuItem.setText(MENU_ITEM_TEXT[position] + "(" + mSongCollection.getSongCollectionByType(SongCollection.CollectionType.ALBUM).size() + ")");
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
            case 0:
                break;
            case 1:
                break;
            case 2:
                break;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        mMenu = menu;

        scanSongs();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_scan:
                scanSongs();
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
//                Intent intent = new Intent(this, AboutActivity.class);
//                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private SongCollection loadSongs() {
        List<Song> songList = new ArrayList<Song>();

        try {
            // make sure the following code is exception free.

            Cursor cursor = getActivity().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    , new String[]{MediaStore.Audio.Media.DATA}
                    , MediaStore.Audio.Media.IS_MUSIC + "!=0"
                    , null
                    , null
            );

            while (cursor.moveToNext()) {
                String filePath = cursor.getString(0);

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

                    Song song = new Song(title, artist, album, intDuration, filePath);
                    songList.add(song);

                    L.d("loaded song: %s, %s, %s, %s", title, artist, album, duration);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new SongCollection(songList);
    }

}
