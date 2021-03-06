/*
 * Copyright 2016 ShadowAngler <craigmiller160@gmail.com> - All Rights Reserved
 * Proprietary / Confidential
 * Unauthorized copying, use, or redistribution of this file is prohibited
 */

package io.craigmiller160.contacts5;

import android.app.Application;
import android.preference.PreferenceManager;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.utils.L;

import io.craigmiller160.contacts5.activity.ContactsActivityViewChanger;
import io.craigmiller160.contacts5.log.LogCache;
import io.craigmiller160.contacts5.model.ModelFactory;
import io.craigmiller160.contacts5.util.ContactsUncaughtExceptionHandler;

/**
 * The application class for Contacts 5+.
 *
 * Created by craig on 5/4/16.
 */
public class ContactsApp extends Application {

    private static ContactsApp instance;

    private static final Object factoryLock = new Object();

    private ModelFactory modelFactory;
    private Thread.UncaughtExceptionHandler defaultUncaughtHandler;

    public static ContactsApp getApp(){
        return instance;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        instance = this;

        LogCache.initialize(this);
        PreferenceManager.setDefaultValues(this, R.xml.display_settings, false);

        synchronized (factoryLock){
            this.defaultUncaughtHandler = Thread.getDefaultUncaughtExceptionHandler();
            modelFactory = new ModelFactory(this);
        }

        if(!ImageLoader.getInstance().isInited()){
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                    .threadPoolSize(5)
                    .build();
            ImageLoader.getInstance().init(config);
        }

        //Disable UniversalImageLoader logging
        L.writeLogs(false);

        Thread.setDefaultUncaughtExceptionHandler(new ContactsUncaughtExceptionHandler());

        ContactsActivityViewChanger.initialize(this);
    }

    public ModelFactory modelFactory(){
        synchronized (factoryLock){
            return modelFactory;
        }
    }

    public Thread.UncaughtExceptionHandler getDefaultUncaughtHandler(){
        synchronized (factoryLock){
            return defaultUncaughtHandler;
        }
    }

}
