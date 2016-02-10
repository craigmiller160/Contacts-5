package io.craigmiller160.contacts5.service;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.util.Log;

/**
 * Created by Craig on 1/30/2016.
 */
public class AccountService {

    private static final String TAG = "AccountService";

    private static final String GOOGLE_ACCOUNT = "com.google";

    //TODO currently only retrieves Google accounts
    public static Account[] getAllContactAccounts(Context context){
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        return accountManager.getAccountsByType(GOOGLE_ACCOUNT);
    }

    //TODO currently only retrieves Google accounts
    public static String[] getAllContactAccountNames(Context context){
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        Account[] accounts = accountManager.getAccountsByType(GOOGLE_ACCOUNT);
        String[] accountNames = new String[accounts.length];
        Log.d(TAG, "All account names retrieved. Size: " + accountNames.length);
        for(int i = 0; i < accounts.length; i++){
            accountNames[i] = accounts[i].name;
        }
        return accountNames;
    }

    public static int getAccountCount(Context context){
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        Account[] accounts = accountManager.getAccountsByType(GOOGLE_ACCOUNT);

        return accounts.length;
    }

}
