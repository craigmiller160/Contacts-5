package io.craigmiller160.contacts5.util;

import android.util.Log;

import java.util.concurrent.ThreadFactory;

/**
 * Created by craig on 5/8/16.
 */
public class ContactsThreadFactory implements ThreadFactory {

    @Override
    public Thread newThread(Runnable runnable) {
        System.out.println("CREATING A THREAD"); //TODO delete this

        Thread thread = new Thread(runnable);
        thread.setUncaughtExceptionHandler(new ContactsUncaughtExceptionHandler());

        return thread;
    }

    public static class ContactsUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler{

        private static final String TAG = "UncaughtException";

        @Override
        public void uncaughtException(Thread thread, Throwable throwable) {
            System.out.println("UNCAUGHT EXCEPTION"); //TODO delete this
            Log.e(TAG, String.format("Uncaught exception in thread %d", thread.getId()), throwable);
            throwable.printStackTrace();
            System.exit(1);
        }
    }

}
