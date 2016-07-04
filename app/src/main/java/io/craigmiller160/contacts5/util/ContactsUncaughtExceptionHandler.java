package io.craigmiller160.contacts5.util;


import io.craigmiller160.contacts5.ContactsApp;
import io.craigmiller160.contacts5.log.Logger;

/**
 * Created by craig on 6/5/16.
 */
public class ContactsUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

    private static final String TAG = "UncaughtException";
    private static final Logger logger = Logger.newLogger(TAG);

    private final Thread.UncaughtExceptionHandler defaultUncaughtHandler;

    public ContactsUncaughtExceptionHandler(){
        this.defaultUncaughtHandler = ContactsApp.getApp().getDefaultUncaughtHandler();
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        logger.e(TAG, String.format("Uncaught exception in thread %d", thread.getId()), throwable);
        logger.flushCache();
        defaultUncaughtHandler.uncaughtException(thread, throwable);
    }

}
