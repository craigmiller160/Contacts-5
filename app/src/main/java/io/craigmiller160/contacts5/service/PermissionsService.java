package io.craigmiller160.contacts5.service;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v13.app.FragmentCompat;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;

/**
 * Created by Craig on 1/30/2016.
 */
public class PermissionsService {

    public static final int CONTACTS_PERMISSION_REQUEST = 101;

    private static final String TAG = "PermissionsService";

    private final Context context;

    PermissionsService(Context context){
        this.context = context;
    }

    public boolean hasReadContactsPermission(){
        return ContextCompat.checkSelfPermission(
                context, Manifest.permission.READ_CONTACTS) ==
                PackageManager.PERMISSION_GRANTED;
    }

    //TODO in the future, try and find a way to do this without needing an Activity reference

    public void requestReadContactsPermission(Activity activity){
        Log.d(TAG, "Requesting permission to read contacts");
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.READ_CONTACTS},
                CONTACTS_PERMISSION_REQUEST);
    }

    //TODO consider if you really need both request permissions methods

    public void requestReadContactsPermission(android.app.Fragment fragment){
        Log.d(TAG, "Requesting permission to read contacts");
        FragmentCompat.requestPermissions(fragment,
                new String[]{Manifest.permission.READ_CONTACTS},
                CONTACTS_PERMISSION_REQUEST);
    }

    //support.v4.Fragment
    public void requestReadContactsPermission(Fragment fragment){
        Log.d(TAG, "Requesting permission to read contacts");
        fragment.requestPermissions(
                new String[]{Manifest.permission.READ_CONTACTS},
                CONTACTS_PERMISSION_REQUEST);
    }

}
