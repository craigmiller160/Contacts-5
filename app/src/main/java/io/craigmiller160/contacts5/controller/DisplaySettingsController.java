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

/**
 * Created by craig on 5/7/16.
 */
public class DisplaySettingsController extends AbstractAndroidController implements Preference.OnPreferenceChangeListener {

    private static final String TAG = "DisplaySettingsContrllr";

    public DisplaySettingsController(Context context) {
        super(context);
    }

    //TODO put validation here for the values

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        SharedPreferences.Editor settingsEditor = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
        String key = preference.getKey();
        Log.d(TAG, "Preference changed: Key: " + key + " New Value: " + newValue);
        if(key.equals(getContext().getString(R.string.accounts_to_display_prop))){
            if(newValue instanceof Set){
                settingsEditor.putStringSet(getContext().getString(R.string.accounts_to_display_prop), (Set<String>) newValue);
                return true;
            }
            else if(newValue instanceof String[]){
                settingsEditor.putStringSet(getContext().getString(R.string.accounts_to_display_prop), new HashSet<>(Arrays.asList((String[]) newValue)));
                return true;
            }
            return false;
        }
        else if(key.equals(getContext().getString(R.string.sort_order_prop)) && newValue != null){
            settingsEditor.putString(getContext().getString(R.string.sort_order_prop), newValue.toString());
            return true;
        }
        else if(key.equals(getContext().getString(R.string.sort_by_prop)) && newValue != null){
            settingsEditor.putString(getContext().getString(R.string.sort_by_prop), newValue.toString());
            return true;
        }
        else if(key.equals(getContext().getString(R.string.name_format_prop)) && newValue != null){
            settingsEditor.putString(getContext().getString(R.string.name_format_prop), newValue.toString());
            return true;
        }
        else if(key.equals(getContext().getString(R.string.phones_only_prop)) && newValue != null){
            settingsEditor.putBoolean(getContext().getString(R.string.phones_only_prop), (Boolean) newValue);
            return true;
        }

        return false;
    }
}
