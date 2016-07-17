/*
 * Copyright 2016 ShadowAngler <craigmiller160@gmail.com> - All Rights Reserved
 * Proprietary / Confidential
 * Unauthorized copying, use, or redistribution of this file is prohibited
 */

package io.craigmiller160.contacts5.util;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import io.craigmiller160.contacts5.log.Logger;

/**
 * Created by craig on 6/19/16.
 */
public class AndroidSystemUtil extends AbstractAndroidUtil{

    public static final int CONTACTS_PERMISSION_REQUEST = 101;

    private static final String TAG = "AndroidSystemUtil";
    private static final Logger logger = Logger.newLogger(TAG);

    private final Permissions permissions;
    private final Accounts accounts;

    public AndroidSystemUtil(Context context) {
        super(context);
        this.permissions = new Permissions(context);
        this.accounts = new Accounts(context);
    }

    public Permissions permissions(){
        return permissions;
    }

    public Accounts accounts(){
        return accounts;
    }

    public static class Permissions extends AbstractAndroidUtil{

        private Permissions(Context context) {
            super(context);
        }

        public boolean hasReadContactsPermission(){
            logger.d(TAG, "Checking if ReadContacts permission has been granted");
            return ContextCompat.checkSelfPermission(
                    getContext(), Manifest.permission.READ_CONTACTS) ==
                    PackageManager.PERMISSION_GRANTED;
        }

        public void requestReadContactsPermission(Activity activity){
            logger.d(TAG, "Requesting permission to read contacts");
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    CONTACTS_PERMISSION_REQUEST);
        }
    }

    public static class Accounts extends AbstractAndroidUtil{

        private static final String GOOGLE_ACCOUNT = "com.google";

        public Accounts(Context context) {
            super(context);
        }

        public Account[] getAllContactAccounts(){
            AccountManager accountManager = (AccountManager) getContext().getSystemService(Context.ACCOUNT_SERVICE);
            return accountManager.getAccountsByType(GOOGLE_ACCOUNT);
        }

        public String[] getAllContactAccountNames(){
            AccountManager accountManager = (AccountManager) getContext().getSystemService(Context.ACCOUNT_SERVICE);
            Account[] accounts = accountManager.getAccountsByType(GOOGLE_ACCOUNT);
            String[] accountNames = new String[accounts.length];
            logger.d(TAG, "All account names retrieved. Size: " + accountNames.length);
            for(int i = 0; i < accounts.length; i++){
                accountNames[i] = accounts[i].name;
            }
            return accountNames;
        }

        public Set<String> getAllContactAccountNamesSet(){
            return new HashSet<>(Arrays.asList(getAllContactAccountNames()));
        }

        public int getAccountCount(){
            AccountManager accountManager = (AccountManager) getContext().getSystemService(Context.ACCOUNT_SERVICE);
            Account[] accounts = accountManager.getAccountsByType(GOOGLE_ACCOUNT);

            return accounts.length;
        }
    }

}
