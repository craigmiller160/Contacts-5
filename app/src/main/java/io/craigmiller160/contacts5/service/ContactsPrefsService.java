package io.craigmiller160.contacts5.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import io.craigmiller160.contacts5.R;
import io.craigmiller160.locus.Locus;

import static io.craigmiller160.contacts5.util.ContactsConstants.*;

/**
 * Created by craig on 5/7/16.
 */
public class ContactsPrefsService {

    private static final String TAG = "ContactsPrefsService";

    private final Context context;
    private final AccountService accountService;
    private final ResourceService resources;

    ContactsPrefsService(Context context, AccountService accountService, ResourceService resources){
        this.context = context;
        this.accountService = accountService;
        this.resources = resources;
    }


    public void loadAllPreferences(){
        Log.d(TAG, "Loading preferences from SharedPreferences");
        SharedPreferences displaySettings = PreferenceManager.getDefaultSharedPreferences(context);

        //Test how many accounts can be loaded. If 0, app doesn't have contacts permission
        //Don't set this field without permission, because it will set a 0 value that will persist
        //until manually changed
        String[] accountNames = accountService.getAllContactAccountNames();
        if(accountNames.length > 0){
            Set<String> accountsToDisplay = displaySettings.getStringSet(resources.getString(R.string.accounts_to_display_prop),
                    new HashSet<>(Arrays.asList(accountNames)));
            Locus.model.setValue(resources.getString(R.string.accounts_to_display_prop), accountsToDisplay);
        }

        String sortOrder = displaySettings.getString(context.getString(R.string.sort_order_prop),
                resources.getStringArray(R.array.sort_order_values)[0]);
        Locus.model.setValue(resources.getString(R.string.sort_order_prop), sortOrder);

        String sortBy = displaySettings.getString(context.getString(R.string.sort_by_prop),
                resources.getStringArray(R.array.sort_by_values)[0]);
        Locus.model.setValue(resources.getString(R.string.sort_by_prop), sortBy);

        boolean phonesOnly = displaySettings.getBoolean(
                resources.getString(R.string.phones_only_prop), true);
        Locus.model.setValue(resources.getString(R.string.phones_only_prop), phonesOnly);

    }

    public void saveAllPreferences(){
        Log.d(TAG, "Storing preferences in SharedPreferences");
        SharedPreferences.Editor settingsEditor = PreferenceManager.getDefaultSharedPreferences(context).edit();

        Set<String> accountsToDisplay = Locus.model.getValue(
                resources.getString(R.string.accounts_to_display_prop), Set.class);
        if(accountsToDisplay != null){
            settingsEditor.putStringSet(resources.getString(R.string.accounts_to_display_prop), accountsToDisplay);
        }

        String sortOrder = Locus.model.getValue(
                resources.getString(R.string.sort_order_prop), String.class);
        if(sortOrder != null){
            settingsEditor.putString(resources.getString(R.string.sort_order_prop), sortOrder);
        }

        String sortBy = Locus.model.getValue(
                resources.getString(R.string.sort_by_prop), String.class);
        if(sortBy != null){
            settingsEditor.putString(resources.getString(R.string.sort_by_prop), sortBy);
        }

        boolean phonesOnly = Locus.model.getValue(
                resources.getString(R.string.phones_only_prop), Boolean.class);
        settingsEditor.putBoolean(resources.getString(R.string.phones_only_prop), phonesOnly);

    }

}
