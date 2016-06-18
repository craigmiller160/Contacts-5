package io.craigmiller160.contacts5.controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import io.craigmiller160.contacts5.R;

import static io.craigmiller160.contacts5.util.ContactsConstants.*;

/**
 * Created by craig on 5/7/16.
 */
public class DisplaySettingsController extends AbstractAndroidController implements Preference.OnPreferenceChangeListener {

    private static final String TAG = "DisplaySettingsContrllr";

    public DisplaySettingsController(Context context) {
        super(context);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        SharedPreferences.Editor settingsEditor = getContext().getSharedPreferences(CONTACTS_PREFS, Context.MODE_PRIVATE).edit();
        String key = preference.getKey();
        Log.d(TAG, "Preference changed: Key: " + key + " New Value: " + newValue);
        if(key.equals(getContext().getString(R.string.setting_accounts_to_display))){
            if(newValue instanceof Set){
                settingsEditor.putStringSet(getContext().getString(R.string.setting_accounts_to_display), (Set<String>) newValue).apply();
                return true;
            }
            else if(newValue instanceof String[]){
                settingsEditor.putStringSet(getContext().getString(R.string.setting_accounts_to_display), new HashSet<>(Arrays.asList((String[]) newValue))).apply();
                return true;
            }
            return false;
        }
        else if(key.equals(getContext().getString(R.string.setting_contact_sort_order)) && newValue != null){
            settingsEditor.putString(getContext().getString(R.string.setting_contact_sort_order), newValue.toString()).apply();
            return true;
        }
        else if(key.equals(getContext().getString(R.string.setting_phones_only)) && newValue != null){
            settingsEditor.putBoolean(getContext().getString(R.string.setting_phones_only), (Boolean) newValue).apply();
            return true;
        }
        else if(key.equals(getContext().getString(R.string.setting_empty_group)) && newValue != null){
            settingsEditor.putBoolean(getContext().getString(R.string.setting_empty_group), (Boolean) newValue).apply();
            return true;
        }
        else if(key.equals(getContext().getString(R.string.setting_group_sort_order)) && newValue != null){
            settingsEditor.putString(getContext().getString(R.string.setting_group_sort_order), newValue.toString()).apply();
            return true;
        }
        else if(key.equals(getContext().getString(R.string.setting_group_sort_by)) && newValue != null){
            settingsEditor.putString(getContext().getString(R.string.setting_group_sort_by), newValue.toString()).apply();
            return true;
        }

        return false;
    }
}
