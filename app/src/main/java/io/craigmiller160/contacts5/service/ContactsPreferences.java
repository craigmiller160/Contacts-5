package io.craigmiller160.contacts5.service;

import android.accounts.Account;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import io.craigmiller160.contacts5.ContactsApplication;
import io.craigmiller160.contacts5.R;

/**
 * Created by Craig on 1/25/2016.
 */
public class ContactsPreferences {

    //These are all in strings.xml now
//    public static final String SORT_ASC_ORDER = "Ascending";
//    public static final String SORT_DESC_ORDER = "Descending";
//    public static final String SORT_BY_FIRST_NAME = "First name";
//    public static final String SORT_BY_LAST_NAME = "Last name";
//    public static final String FIRST_NAME_FORMAT = "First name first";
//    public static final String LAST_NAME_FORMAT = "Last name first";
//
//    private static final String ACCOUNTS_TO_DISPLAY_KEY = "Accounts To Display";
//    private static final String SORT_BY_KEY = "Sort By";
//    private static final String NAME_FORMAT_KEY = "Name Format";
//    private static final String SORT_ORDER_KEY = "Sort Order";

    private static final String TAG = "ContactsPreferences";

    private static ContactsPreferences instance;
    private static final Object instanceLock = new Object();

    private Set<String> accountsToDisplay;
    private String sortOrder;
    private String sortBy;
    private String nameFormat;
    private boolean phonesOnly;

    private boolean preferencesLoaded;


    public static ContactsPreferences getInstance(){
        if(instance == null){
            //Do synchronized check too, inside outer if to minimize blocking
            synchronized(instanceLock){
                if(instance == null){
                    instance = new ContactsPreferences();
                }
            }
        }
        return instance;
    }

    public void loadSavedPreferences(Context context){
        Log.d(TAG, "Loading preferences from SharedPreferences");
        SharedPreferences displaySettings = PreferenceManager.getDefaultSharedPreferences(context);

        //Test how many accounts can be loaded. If 0, app doesn't have contacts permission
        //Don't set this field without permission, because it will set a 0 value that will persist
        //until manually changed
        String[] accountNames = AccountService.getAllContactAccountNames(context);
        if(accountNames.length > 0){
            setAccountsToDisplay(displaySettings.getStringSet(
                    context.getString(R.string.accounts_to_display_key),
                    new HashSet<String>(Arrays.asList(accountNames))));
        }

        setSortOrder(displaySettings.getString(
                context.getString(R.string.sort_order_key),
                context.getResources().getStringArray(R.array.sort_order_values)[0]), context);

        setSortBy(displaySettings.getString(
                context.getString(R.string.sort_by_key),
                context.getResources().getStringArray(R.array.sort_by_values)[0]), context);

        setNameFormat(displaySettings.getString(
                context.getString(R.string.name_format_key),
                context.getResources().getStringArray(R.array.name_format_values)[0]), context);

        setPhonesOnly(displaySettings.getBoolean(
                context.getString(R.string.phones_only_key), true));
    }

    public void storeSavedPreferences(Context context){
        Log.d(TAG, "Storing preferences in SharedPreferences");
        SharedPreferences displaySettings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor settingsEditor = displaySettings.edit();

        //Copy the values of the fields into local variables to avoid synchronizing the Editor
        Set<String> accountsToDisplay = null;
        String sortOrder = null;
        String sortBy = null;
        String nameFormat = null;
        boolean phonesOnly;
        synchronized(this){
            if(this.accountsToDisplay != null){
                accountsToDisplay = this.accountsToDisplay;
            }

            if(this.sortOrder != null){
                sortOrder = this.sortOrder;
            }

            if(this.sortBy != null){
                sortBy = this.sortBy;
            }

            if(this.nameFormat != null){
                nameFormat = this.nameFormat;
            }

            phonesOnly = this.phonesOnly;
        }

        if(accountsToDisplay != null){
            settingsEditor.putStringSet(
                    context.getString(R.string.accounts_to_display_key), accountsToDisplay);
        }

        if(sortOrder != null){
            settingsEditor.putString(
                    context.getString(R.string.sort_order_key), sortOrder);
        }

        if(sortBy != null){
            settingsEditor.putString(
                    context.getString(R.string.sort_by_key), sortBy);
        }

        if(nameFormat != null){
            settingsEditor.putString(
                    context.getString(R.string.name_format_key), nameFormat);
        }

        settingsEditor.putBoolean(
                context.getString(R.string.phones_only_key), phonesOnly);

        settingsEditor.apply();
    }

    public synchronized Set<String> getAccountsToDisplay(){
        return accountsToDisplay;
    }

    public synchronized String getSortOrder(){
        return sortOrder;
    }

    public synchronized String getSortBy(){
        return sortBy;
    }

    public synchronized String getNameFormat(){
        return nameFormat;
    }

    public synchronized boolean getPhonesOnly(){
        return phonesOnly;
    }

    //TODO this will need some work once i get the account list properly loaded
    private void setAccountsToDisplay(Object accountsToDisplay){
        if(accountsToDisplay instanceof Set){
            synchronized(this){
                this.accountsToDisplay = (Set<String>) accountsToDisplay;
            }
        }
        else if(accountsToDisplay instanceof String[]){
            synchronized(this){
                this.accountsToDisplay = new HashSet<String>(Arrays.asList((String[]) accountsToDisplay));
            }
        }
        else{
            throw new IllegalArgumentException("Invalid accountsToDisplay value: " + accountsToDisplay);
        }
    }

    public void setDisplaySettingProperty(String propertyName, Object value, Context context){
        if(propertyName.equals(context.getString(R.string.accounts_to_display_key))){
            setAccountsToDisplay(value);
        }
        else if(propertyName.equals(context.getString(R.string.sort_by_key))){
            setSortBy(value.toString(), context);
        }
        else if(propertyName.equals(context.getString(R.string.sort_order_key))){
            setSortOrder(value.toString(), context);
        }
        else if(propertyName.equals(context.getString(R.string.name_format_key))){
            setNameFormat(value.toString(), context);
        }
        else if(propertyName.equals(context.getString(R.string.phones_only_key))){
            if(value instanceof Boolean){
                setPhonesOnly((Boolean) value); //TODO consider IllegalArgumentException here
            }
        }
        else{
            throw new IllegalArgumentException("Invalid Property Name: " + propertyName);
        }
    }

    private void setNameFormat(String nameFormat, Context context){
        String[] nameFormatValues = context.getResources().getStringArray(R.array.name_format_values);
        if(!Arrays.asList(nameFormatValues).contains(nameFormat)){
            throw new IllegalArgumentException("Invalid nameFormat value: " + nameFormat);
        }

        synchronized(this){
            this.nameFormat = nameFormat;
        }
    }

    private synchronized void setPhonesOnly(boolean phonesOnly){
        this.phonesOnly = phonesOnly;
    }

    private void setSortBy(String sortBy, Context context){
        String[] sortByValues = context.getResources().getStringArray(R.array.sort_by_values);
        if(!Arrays.asList(sortByValues).contains(sortBy)){
            throw new IllegalArgumentException("Invalid sortBy value: " + sortBy);
        }

        synchronized(this){
            this.sortBy = sortBy;
        }
    }

    private void setSortOrder(String sortOrder, Context context){
        String[] sortOrderValues = context.getResources().getStringArray(R.array.sort_order_values);
        if(!Arrays.asList(sortOrderValues).contains(sortOrder)){
            throw new IllegalArgumentException("Invalid sortOrder value: " + sortOrder);
        }

        synchronized(this){
            this.sortOrder = sortOrder;
        }
    }

}
