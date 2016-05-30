package io.craigmiller160.contacts5.util;

import android.util.Log;

import java.util.concurrent.ThreadFactory;

/**
 * Created by craig on 5/8/16.
 */
public class ContactsThreadFactory implements ThreadFactory {

    @Override
    public Thread newThread(Runnable runnable) {
        Thread thread = new Thread(runnable);
        thread.setUncaughtExceptionHandler(new ContactsUncaughtExceptionHandler());

        return thread;
    }

    public static class ContactsUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler{

        private static final String TAG = "UncaughtException";

        @Override
        public void uncaughtException(Thread thread, Throwable throwable) {
            Log.e(TAG, String.format("Uncaught exception in thread %d", thread.getId()), throwable);
            System.exit(1); //TODO consider removing this
        }
    }

}
