package com.example.musicplayer.lib.task;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created with IntelliJ IDEA.
 * User: xiejm
 *
 * Date: 3/19/13
 * Time: 8:58 PM
 * Modify time: Sep. 23, 2013
 *
 * This TaskQueue is used to queue up tasks that are supposed
 * to run sequentially, which helps avoid problems related to
 * thread-safety.
 */
public class TaskQueue extends Thread {
    private BlockingQueue<Object> mQueue;

    public TaskQueue() {
        mQueue = new LinkedBlockingQueue<Object>();
    }

    public TaskQueue(String name) {
        this();
        setName(name);
    }

    public void stopTaskQueue() {
        // use 'Poison Pill Shutdown' to stop the task queue
        // add a non-Runnable object, which will be recognized as the command
        // by the thread to break the infinite loop
        mQueue.add(new Object());
    }

    public void scheduleTask(Runnable task) {
        mQueue.add(task);
    }

    @Override
    public void run() {
        while (true) {
            try {
                Object obj = mQueue.take();

                if (obj instanceof Runnable)
                    ((Runnable) obj).run();

                else
                    break;

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
