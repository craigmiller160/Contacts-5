/*
 * Copyright 2016 ShadowAngler <craigmiller160@gmail.com> - All Rights Reserved
 * Proprietary / Confidential
 * Unauthorized copying, use, or redistribution of this file is prohibited
 */

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
