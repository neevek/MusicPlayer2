package com.example.musicplayer.lib.task;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.*;

/**
 * Created with IntelliJ IDEA.
 * User: xiejm
 * Date: 8/16/12
 * Time: 11:46 AM
 *
 * This class encapsulates 3 kinds of methods to "execute" code in background
 * thread, "execute" code in UI thread and "schedule" code to be run in the future.
 */

// an asynchronous task executor(thread pool)
public class ThreadPoolExecutorWrapper {
    private ExecutorService mThreadPoolExecutor;
    private ScheduledThreadPoolExecutor mScheduledThreadPoolExecutor;
    private Handler mMainHandler;

    public ThreadPoolExecutorWrapper(int activeThreadCount, int maxThreadCount, int maxScheTaskThread) {
        mThreadPoolExecutor = new ThreadPoolExecutor(activeThreadCount, maxThreadCount,
                60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(),
                Executors.defaultThreadFactory());

        if (maxScheTaskThread > 0) {
            mScheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(maxScheTaskThread);
        }

        mMainHandler = new Handler(Looper.getMainLooper());
    }

    public void executeTask(Runnable task) {
        mThreadPoolExecutor.execute(task);
    }

    public <T> Future<T> submitTask(Callable <T> task) {
        return mThreadPoolExecutor.submit(task);
    }

    public void scheduleTask(long delay, Runnable task) {
        mScheduledThreadPoolExecutor.schedule(task, delay, TimeUnit.MILLISECONDS);
    }

    public void scheduleTaskAtFixedRateIgnoringTaskRunningTime(long initialDelay, long period, Runnable task) {
        mScheduledThreadPoolExecutor.scheduleAtFixedRate(task, initialDelay, period, TimeUnit.MILLISECONDS);
    }

    public void scheduleTaskAtFixedRateIncludingTaskRunningTime(long initialDelay, long period, Runnable task) {
        mScheduledThreadPoolExecutor.scheduleWithFixedDelay(task, initialDelay, period, TimeUnit.MILLISECONDS);
    }

    public boolean removeScheduledTask(Runnable task) {
        return mScheduledThreadPoolExecutor.remove(task);
    }

    public void scheduleTaskOnUiThread(long delay, Runnable task) {
        mMainHandler.postDelayed(task, delay);
    }

    public void removeScheduledTaskOnUiThread(Runnable task) {
        mMainHandler.removeCallbacks(task);
    }

    public void runTaskOnUiThread(Runnable task) {
        mMainHandler.post(task);
    }

    public void shutdown() {
        if (mThreadPoolExecutor != null) {
            mThreadPoolExecutor.shutdown();
            mThreadPoolExecutor = null;
        }

        if (mScheduledThreadPoolExecutor != null) {
            mScheduledThreadPoolExecutor.shutdown();
            mScheduledThreadPoolExecutor = null;
        }
    }
}
