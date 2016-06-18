package io.craigmiller160.contacts5.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import io.craigmiller160.contacts5.ContactsApp;
import io.craigmiller160.contacts5.R;
import io.craigmiller160.contacts5.service.AccountService;

/**
 * Created by craig on 5/7/16.
 */
public class DisplaySettingsActivity extends AppCompatPreferenceActivity {

    private static final String TAG = "DisplaySettingsActivity";

    private static final BindSummaryToValueListener bindSummaryListener = new BindSummaryToValueListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.activity_settings_name_label));

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        getFragmentManager().beginTransaction().replace(
                android.R.id.content, new DisplaySettingsFragment()).commit();
    }

    @Override
    public void onBackPressed(){
        closeAction();
    }

    private void closeAction(){
        Log.i(TAG, "Closing DisplaySettingsActivity");
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == android.R.id.home){
            closeAction();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        return DisplaySettingsFragment.class.getName().equals(fragmentName);
    }

    public static class DisplaySettingsFragment extends PreferenceFragment {

        private AccountService accountService;

        @Override
        public void onCreate(Bundle savedInstance){
            super.onCreate(savedInstance);
            Log.d(TAG, "Creating DisplaySettingsFragment");
            addPreferencesFromResource(R.xml.display_settings);
            setHasOptionsMenu(true);
            this.accountService = ContactsApp.getApp().serviceFactory().getAccountService();

            configurePreference(findPreference(getString(R.string.setting_accounts_to_display_key)));
            configurePreference(findPreference(getString(R.string.setting_contact_sort_order_key)));
            configurePreference(findPreference(getString(R.string.setting_phones_only_key)));
            configurePreference(findPreference(getString(R.string.setting_group_empty_key)));
            configurePreference(findPreference(getString(R.string.setting_group_sort_order_key)));
            configurePreference(findPreference(getString(R.string.setting_group_sort_by_key)));
            configurePreference(findPreference(getString(R.string.setting_contact_name_format_key)));
            configurePreference(findPreference(getString(R.string.setting_contact_sort_by_key)));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                ((DisplaySettingsActivity) getActivity()).closeAction();
                return true;
            }

            return false;
        }

        private void configurePreference(Preference pref){
            String key = pref.getKey();
            Log.v(TAG, "Configuring preference: " + key);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            pref.setOnPreferenceChangeListener(bindSummaryListener);

            if(key.equals(getString(R.string.setting_accounts_to_display_key))){
                MultiSelectListPreference mPref = (MultiSelectListPreference) pref;

                //Set the initial list of all account names
                String[] accountNames = accountService.getAllContactAccountNames();
                mPref.setEntries(accountNames);
                mPref.setEntryValues(accountNames);

                //Get the values to be selected and set them
                Set<String> accountsToDisplay = prefs.getStringSet(getString(R.string.setting_accounts_to_display_key), accountService.getAllContactAccountNamesSet());
                if(accountsToDisplay != null){
                    mPref.setValues(accountsToDisplay);
                }
                bindSummaryListener.onPreferenceChange(mPref, accountsToDisplay);
            }
            else if(!(pref instanceof SwitchPreference)){
                String value = prefs.getString(key, "");
                bindSummaryListener.onPreferenceChange(pref, value);
            }
        }
    }

    private static class BindSummaryToValueListener implements Preference.OnPreferenceChangeListener{

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String stringValue = newValue != null ? newValue.toString() : "";
            if(preference instanceof ListPreference){
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);
                preference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);
            }
            else if(preference instanceof MultiSelectListPreference){
                MultiSelectListPreference mListPref = (MultiSelectListPreference) preference;
                List<String> values = new ArrayList<>((Set<String>) newValue);
                Collections.sort(values);
                mListPref.setSummary(values.toString());
            }
            else if(!(preference instanceof SwitchPreference)){
                preference.setSummary(stringValue);
            }

            return true;
        }
    }
}
