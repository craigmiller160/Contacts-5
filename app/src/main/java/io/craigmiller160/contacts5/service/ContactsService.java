package io.craigmiller160.contacts5.service;

import android.app.Service;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageItemInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import io.craigmiller160.contacts5.ContactsApp;
import io.craigmiller160.contacts5.model.AndroidModel;
import io.craigmiller160.contacts5.model.Contact;
import io.craigmiller160.contacts5.model.ContactGroup;
import io.craigmiller160.contacts5.util.AbstractAndroidUtil;
import io.craigmiller160.contacts5.util.AndroidSystemUtil;
import io.craigmiller160.contacts5.util.ContactsThreadFactory;
import io.craigmiller160.contacts5.util.PreferenceHelper;

import static io.craigmiller160.contacts5.service.ContactsQueryConstants.*;
import static io.craigmiller160.contacts5.util.ContactsConstants.*;

import static io.craigmiller160.contacts5.service.ContactQueryTasks.*;

/**
 * Created by craig on 6/19/16.
 */
public class ContactsService extends Service{

    //TODO this needs to support its own interruption.
    // TODO It also needs to cancel a running service if a new one is started
    //TODO the service needs to be stopped after each operation is finished

    //TODO reduce numbers of constants used, here and everywhere, and consolidate them all in strings.xml

    private static final String TAG = "ContactsService";

    private AndroidSystemUtil androidSystemUtil;
    private ExecutorService executor;

    @Override
    public void onCreate() {
        super.onCreate();
        androidSystemUtil = new AndroidSystemUtil(this);
        executor = Executors.newFixedThreadPool(5, new ContactsThreadFactory());
    }

    public void submit(Runnable task){
        executor.submit(task);
    }

    public <V> Future<V> submit(Callable<V> task){
        return executor.submit(task);
    }

    @Override
    public void onDestroy() {
        //TODO ensure that the service is interrupted
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(androidSystemUtil.permissions().hasReadContactsPermission()){
            Log.d(TAG, "ContactsService start command received. Contacts permissions are available");
            ExecuteQueries executeQueries = new ExecuteQueries(this, intent, startId, TAG);
            if(Looper.myLooper() == Looper.getMainLooper()){
                executor.submit(executeQueries);
            }
            else{
                executeQueries.run();
            }
        }
        else{
            Log.e(TAG, "ContactsService start command received. Contacts permissions NOT available");
        }

        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        //No binding being done here
        return null;
    }


}
