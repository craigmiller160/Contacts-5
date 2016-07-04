package io.craigmiller160.contacts5.util;

import android.util.Log;

import io.craigmiller160.contacts5.ContactsApp;

/**
 * Created by craig on 6/5/16.
 */
public class ContactsUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

    private static final String TAG = "UncaughtException";

    private final Thread.UncaughtExceptionHandler defaultUncaughtHandler;

    public ContactsUncaughtExceptionHandler(){
        this.defaultUncaughtHandler = ContactsApp.getApp().getDefaultUncaughtHandler();
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        Log.e(TAG, String.format("Uncaught exception in thread %d", thread.getId()), throwable);
        defaultUncaughtHandler.uncaughtException(thread, throwable);
    }

}
