package com.example.musicplayer;

import android.app.Application;
import com.example.musicplayer.lib.task.TaskExecutor;

/**
 * Created by neevek on 7/20/14.
 */
public class MusicPlayerApplication extends Application {
    private static MusicPlayerApplication sInstance;
    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        TaskExecutor.init(4, 10, 2);
    }

    public static MusicPlayerApplication getInstance() {
        return sInstance;
    }
}
