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
public class ContactsPreferences {

    private static final String TAG = "ContactsPreferences";

    public static void loadAllPreferences(Context context){
        Log.d(TAG, "Loading preferences from SharedPreferences");
        SharedPreferences displaySettings = PreferenceManager.getDefaultSharedPreferences(context);

        //Test how many accounts can be loaded. If 0, app doesn't have contacts permission
        //Don't set this field without permission, because it will set a 0 value that will persist
        //until manually changed
        String[] accountNames = AccountService.getAllContactAccountNames(context);
        if(accountNames.length > 0){
            Set<String> accountsToDisplay = displaySettings.getStringSet(context.getString(R.string.accounts_to_display_key),
                    new HashSet<>(Arrays.asList(accountNames)));
            Locus.model.setValue(ACCOUNTS_TO_DISPLAY_PROP, accountsToDisplay);
        }

        String sortOrder = displaySettings.getString(context.getString(R.string.sort_order_key),
                context.getResources().getStringArray(R.array.sort_order_values)[0]);
        Locus.model.setValue(SORT_ORDER_PROP, sortOrder);

        String sortBy = displaySettings.getString(context.getString(R.string.sort_by_key),
                context.getResources().getStringArray(R.array.sort_by_values)[0]);
        Locus.model.setValue(SORT_BY_PROP, sortBy);

        boolean phonesOnly = displaySettings.getBoolean(
                context.getString(R.string.phones_only_key), true);
        Locus.model.setValue(PHONES_ONLY_PROP, phonesOnly);

    }

    public static void saveAllPreferences(Context context){
        Log.d(TAG, "Storing preferences in SharedPreferences");
        SharedPreferences.Editor settingsEditor = PreferenceManager.getDefaultSharedPreferences(context).edit();

        Set<String> accountsToDisplay = Locus.model.getValue(ACCOUNTS_TO_DISPLAY_PROP, Set.class);
        if(accountsToDisplay != null){
            settingsEditor.putStringSet(context.getString(R.string.accounts_to_display_key), accountsToDisplay);
        }

        String sortOrder = Locus.model.getValue(SORT_ORDER_PROP, String.class);
        if(sortOrder != null){
            settingsEditor.putString(context.getString(R.string.sort_order_key), sortOrder);
        }

        String sortBy = Locus.model.getValue(SORT_BY_PROP, String.class);
        if(sortBy != null){
            settingsEditor.putString(context.getString(R.string.sort_by_key), sortBy);
        }

        boolean phonesOnly = Locus.model.getValue(PHONES_ONLY_PROP, Boolean.class);
        settingsEditor.putBoolean(context.getString(R.string.phones_only_key), phonesOnly);

    }

}
