package io.craigmiller160.contacts5.util;

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

}
