package io.craigmiller160.contacts5.service;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.util.Log;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import io.craigmiller160.contacts5.util.AbstractAndroidUtil;

/**
 * Created by Craig on 1/30/2016.
 */
public class AccountService extends AbstractAndroidUtil {

    private static final String TAG = "AccountService";

    private static final String GOOGLE_ACCOUNT = "com.google";

    AccountService(Context context){
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
        Log.d(TAG, "All account names retrieved. Size: " + accountNames.length);
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
