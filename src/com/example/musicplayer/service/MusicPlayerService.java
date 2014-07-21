package com.example.musicplayer.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;
import com.example.musicplayer.MainActivity;
import com.example.musicplayer.R;
import com.example.musicplayer.lib.log.L;
import com.example.musicplayer.lib.task.TaskQueue;
import com.example.musicplayer.lib.util.Util;
import com.example.musicplayer.pojo.Song;

/**
 * Created by neevek on 7/20/14.
 */
public class MusicPlayerService extends Service {
    public final static String ACTION_MUSIC_PLAYING_STATE_CHANGED = "com.example.musicplayer.state.changed";
    public final static String EXTRA_STATE = "state";
    public final static String EXTRA_SONG_ID = "song_id";
    public final static String EXTRA_PLAYING_PROGRESS = "progress";

    public final static int EXTRA_PLAYING_STATE_START = 1;
    public final static int EXTRA_PLAYING_STATE_STOP = 2;

    private MediaPlayer mMediaPlayer;
    private Song mCurrentSong;

    private TaskQueue mActionQueue;

    private final static int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    private PendingIntent mOpenMainAppPendingIntent;

    private StringBuilder mProgressBuffer = new StringBuilder();

    @Override
    public void onCreate() {
        super.onCreate();

        mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        Intent openMainAppIntent = new Intent(this, MainActivity.class);
        openMainAppIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mOpenMainAppPendingIntent = PendingIntent.getActivity(this, 0, openMainAppIntent, 0);

        initMediaPlayer();
        initActionQueue();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        L.d("destroying service...");
        stopPlaying();

        mActionQueue.scheduleTask(new Runnable() {
            @Override
            public void run() {
                L.d("releasing MediaPlayer...");
                mMediaPlayer.release();
            }
        });
        mActionQueue.stopTaskQueue();
    }

    private void initActionQueue() {
        mActionQueue = new TaskQueue("PlayerActionQueue");
        mActionQueue.start();
    }

    private void initMediaPlayer() {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
            }
        });
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        if (intent != null) {
            final Song song = (Song)intent.getSerializableExtra("song");
            if (song != null) {
                mActionQueue.scheduleTask(new Runnable() {
                    @Override
                    public void run() {
                        playSong(song, intent.getIntExtra("progress", 0));
                    }
                });
            }
        }
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MusicPlayerBinder();
    }

    private void playSong(Song song, int progress) {
        if (song == null) {
            L.w(">>>> song is null");
            return;
        }

        L.d(">>>> start playing: %s", song.title);
        try {
            if (isPlaying()) {
                if (mCurrentSong.equals(song)) {
                    L.w(">>>> try the play a song that is alrealy playing, silently ignore it.");
                    return;
                }

                mMediaPlayer.stop();
            }

            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(song.filePath);
            mMediaPlayer.prepare();
            mMediaPlayer.seekTo(progress);
            mMediaPlayer.start();

            Intent intent = new Intent(ACTION_MUSIC_PLAYING_STATE_CHANGED);
            intent.putExtra(EXTRA_STATE, EXTRA_PLAYING_STATE_START);
            intent.putExtra(EXTRA_SONG_ID, song.id);
            sendOrderedBroadcast(intent, null);

            mProgressBuffer.delete(0, mProgressBuffer.length());
            Notification notification = new NotificationCompat.Builder(this)
                    .setTicker("正在播放 " + song.title)
                    .setContentTitle(song.title + "(" + song.artist+ ")")
                    .setContentText(Util.formatMilliseconds(progress, mProgressBuffer))
                    .setSmallIcon(R.drawable.notification_icon)
                    .setLargeIcon(((BitmapDrawable) getResources().getDrawable(R.drawable.ic_launcher)).getBitmap())
                    .setContentIntent(mOpenMainAppPendingIntent)
                    .setAutoCancel(true)
                    .setOngoing(true)
                    .build();

            startForeground(NOTIFICATION_ID, notification);

            mCurrentSong = song;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "播放音乐失败：" + song.filePath, Toast.LENGTH_LONG).show();

            mCurrentSong = null;
        }
    }

    private void stopPlaying() {
        if (!isPlaying()) {
            return;
        }

        mActionQueue.scheduleTask(new Runnable() {
            @Override
            public void run() {
                if (isPlaying()) {
                    mMediaPlayer.stop();
                    stopForeground(true);
                }
            }
        });
    }

    public boolean isPlaying() {
        try {
            return mMediaPlayer.isPlaying();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        return false;
    }

    public class MusicPlayerBinder extends Binder {
        public MusicPlayerService getMusicPlayerService() {
            return MusicPlayerService.this;
        }
    }
}
