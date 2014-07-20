package com.example.musicplayer.lib.task;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

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
public class TaskExecutor {
    private static ThreadPoolExecutorWrapper sThreadPoolExecutorWrapper;

    public static void init(int maxTaskThread, int maxIdleTaskThread, int maxScheTaskThread) {
        if (sThreadPoolExecutorWrapper == null) {
            sThreadPoolExecutorWrapper = new ThreadPoolExecutorWrapper(maxTaskThread, maxIdleTaskThread, maxScheTaskThread);
        }
    }

    public static void executeTask(Runnable task) {
        sThreadPoolExecutorWrapper.executeTask(task);
    }

    public static <T> Future<T> submitTask(Callable <T> task) {
        return sThreadPoolExecutorWrapper.submitTask(task);
    }

    public static void scheduleTask(long delay, Runnable task) {
        sThreadPoolExecutorWrapper.scheduleTask(delay, task);
    }

    public static void scheduleTaskAtFixedRateIgnoringTaskRunningTime(long initialDelay, long period, Runnable task) {
        sThreadPoolExecutorWrapper.scheduleTaskAtFixedRateIgnoringTaskRunningTime(initialDelay, period, task);
    }

    public static void scheduleTaskAtFixedRateIncludingTaskRunningTime(long initialDelay, long period, Runnable task) {
        sThreadPoolExecutorWrapper.scheduleTaskAtFixedRateIncludingTaskRunningTime(initialDelay, period, task);
    }

    public static boolean removeScheduledTask(Runnable task) {
        return sThreadPoolExecutorWrapper.removeScheduledTask(task);
    }

    public static void scheduleTaskOnUiThread(long delay, Runnable task) {
        sThreadPoolExecutorWrapper.scheduleTaskOnUiThread(delay, task);
    }

    public static void removeScheduledTaskOnUiThread(Runnable task) {
        sThreadPoolExecutorWrapper.removeScheduledTaskOnUiThread(task);
    }

    public static void runTaskOnUiThread(Runnable task) {
        sThreadPoolExecutorWrapper.runTaskOnUiThread(task);
    }

    public static void shutdown() {
        if (sThreadPoolExecutorWrapper != null) {
            sThreadPoolExecutorWrapper.shutdown();
            sThreadPoolExecutorWrapper = null;
        }
    }
}
