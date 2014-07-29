package com.example.musicplayer;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.*;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;
import com.example.musicplayer.biz.SongCollectionManager;
import com.example.musicplayer.fragment.MainFragment;
import com.example.musicplayer.lib.task.TaskExecutor;
import com.example.musicplayer.lib.util.Util;
import com.example.musicplayer.pojo.Song;
import com.example.musicplayer.service.MusicPlayerService;

public class MainActivity extends Activity implements View.OnClickListener {
    private TextView mTvSongTitle;
    private TextView mTvArtist;
    private TextView mTvSongProgress;
    private TextView mTvSongDuration;
    private ImageButton mBtnPlayAndPause;

    private Song mCurrentSong;

    private MusicPlayerService mMusicPlayerService;
    private ServiceConnection mServiceConnection;
    private MusicPlayingStateChangedReceiver mMusicPlayingStateChangedReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // call requestWindowFeature before super.onCreate(); see https://groups.google.com/d/msg/actionbarsherlock/dHIJn1qbkFE/bEzSg2haGZMJ
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setProgressBarIndeterminateVisibility(false);

        initViews();
        bindMusicPlayerService();
        registerReceiver();
        showMainFragment();
        initCurrentSongInfo();
    }

    private void initCurrentSongInfo() {
        final SharedPreferences sharedPreferences = getSharedPreferences(MusicPlayerService.SHARED_PREF, MODE_PRIVATE);
        final long songId = sharedPreferences.getLong(MusicPlayerService.PREF_KEY_LAST_PLAYED_SONG_ID, 0);
        if (songId == 0) {
            return;
        }


        TaskExecutor.executeTask(new Runnable() {
            @Override
            public void run() {
                mCurrentSong = SongCollectionManager.getInstance().getSongCollection(false).getSongById(songId);
                if (mCurrentSong == null) {
                    return;
                }

                final int progress = sharedPreferences.getInt(MusicPlayerService.PREF_KEY_LAST_PLAYED_SONG_PROGRESS, 0);
                TaskExecutor.runTaskOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setCurrentSongInfo(progress);
                    }
                });
            }
        });
    }

    private void registerReceiver() {
        mMusicPlayingStateChangedReceiver = new MusicPlayingStateChangedReceiver();
        registerReceiver(mMusicPlayingStateChangedReceiver, new IntentFilter(MusicPlayerService.ACTION_MUSIC_PLAYING_STATE_CHANGED));
    }

    private void initViews() {
        (mBtnPlayAndPause = (ImageButton) findViewById(R.id.btn_play_and_pause)).setOnClickListener(this);
        findViewById(R.id.btn_play_prev_song).setOnClickListener(this);
        findViewById(R.id.btn_play_next_song).setOnClickListener(this);

        mTvSongTitle = (TextView) findViewById(R.id.tv_song_title);
        mTvSongTitle.getPaint().setFakeBoldText(true);
        mTvArtist = (TextView) findViewById(R.id.tv_artist);
        mTvSongProgress = (TextView) findViewById(R.id.tv_song_progress);
        mTvSongDuration = (TextView) findViewById(R.id.tv_song_duration);
    }

    private void bindMusicPlayerService() {
        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mMusicPlayerService = ((MusicPlayerService.MusicPlayerBinder)service).getMusicPlayerService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mMusicPlayerService = null;
            }
        };

        Intent intent = new Intent(this, MusicPlayerService.class);
        bindService(intent, mServiceConnection, BIND_AUTO_CREATE);
    }

    private void showMainFragment() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.container, new MainFragment());
        ft.commitAllowingStateLoss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        stopService(new Intent(this, MusicPlayerService.class));

        unregisterReceiver(mMusicPlayingStateChangedReceiver);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_play_prev_song: {
                mMusicPlayerService.playPrevOrNextSong(true);
                break;
            }
            case R.id.btn_play_and_pause: {
                if (mMusicPlayerService.isPlaying()) {
                    mMusicPlayerService.pausePlayback();
                } else {
                    mMusicPlayerService.resumePlayback();
                }
                break;
            }
            case R.id.btn_play_next_song: {
                mMusicPlayerService.playPrevOrNextSong(false);
                break;
            }
        }
    }

    private void setCurrentSongInfo(int progress) {
        if (mCurrentSong != null) {
            mTvSongTitle.setText(mCurrentSong.title);
            mTvArtist.setText(mCurrentSong.artist);
            mTvSongProgress.setText(Util.formatMilliseconds(progress, null));
            mTvSongDuration.setText("/" + Util.formatMilliseconds(mCurrentSong.duration, null));
        } else {
            mTvSongTitle.setText("未选歌曲");
            mTvArtist.setText("");
            mTvSongProgress.setText("00:00");
            mTvSongDuration.setText("/00:00");
        }
    }

    class MusicPlayingStateChangedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int state = intent.getIntExtra(MusicPlayerService.EXTRA_STATE, 0);
            switch (state) {
                case MusicPlayerService.EXTRA_PLAYING_STATE_START: {
                    long songId = intent.getLongExtra(MusicPlayerService.EXTRA_SONG_ID, 0L);
                    if (songId > 0) {
                        mCurrentSong = SongCollectionManager.getInstance().getSongCollection(false).getSongById(songId);
                        mBtnPlayAndPause.setImageResource(R.drawable.icon_pause_selector);
                    }

                    int progress = intent.getIntExtra(MusicPlayerService.EXTRA_PLAYING_PROGRESS, 0);
                    setCurrentSongInfo(progress);
                    break;
                }
                case MusicPlayerService.EXTRA_PLAYING_STATE_STOP: {
                    mBtnPlayAndPause.setImageResource(R.drawable.icon_play_selector);
                    break;
                }
                case MusicPlayerService.EXTRA_PLAYING_STATE_PLAYING: {
                    if (mCurrentSong != null) {
                        int progress = intent.getIntExtra(MusicPlayerService.EXTRA_PLAYING_PROGRESS, 0);
                        mTvSongProgress.setText(Util.formatMilliseconds(progress, null));
                    }
                    break;
                }
            }
        }
    }

}
