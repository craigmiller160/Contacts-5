package io.craigmiller160.contacts5.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.MenuItem;

import java.util.Set;

import io.craigmiller160.contacts5.ContactsApp;
import io.craigmiller160.contacts5.R;
import io.craigmiller160.contacts5.service.AccountService;

import static io.craigmiller160.contacts5.util.ContactsConstants.CONTACTS_PREFS;
import static io.craigmiller160.contacts5.util.ContactsConstants.DISPLAY_SETTINGS_CONTROLLER;

/**
 * Created by craig on 5/7/16.
 */
public class DisplaySettingsActivity extends AppCompatPreferenceActivity {

    private static final String TAG = "DisplaySettingsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

            configurePreference(findPreference(getString(R.string.setting_accounts_to_display)));
            configurePreference(findPreference(getString(R.string.setting_contact_sort_order)));
            configurePreference(findPreference(getString(R.string.setting_phones_only)));
            configurePreference(findPreference(getString(R.string.setting_empty_group)));
            configurePreference(findPreference(getString(R.string.setting_group_sort_order)));
            configurePreference(findPreference(getString(R.string.setting_group_sort_by)));
            configurePreference(findPreference(getString(R.string.setting_contact_name_format)));
            configurePreference(findPreference(getString(R.string.setting_contact_sort_by)));
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
            SharedPreferences prefs = getActivity().getSharedPreferences(CONTACTS_PREFS, Context.MODE_PRIVATE);

            if(key.equals(getString(R.string.setting_accounts_to_display))){
                if(pref instanceof MultiSelectListPreference){
                    MultiSelectListPreference mPref = (MultiSelectListPreference) pref;

                    //Set the initial list of all account names
                    String[] accountNames = accountService.getAllContactAccountNames();
                    mPref.setEntries(accountNames);
                    mPref.setEntryValues(accountNames);

                    //Get the values to be selected and set them
                    Set<String> accountsToDisplay = prefs.getStringSet(getString(R.string.setting_accounts_to_display), accountService.getAllContactAccountNamesSet());
                    if(accountsToDisplay != null){
                        mPref.setValues(accountsToDisplay);
                    }
                }
            }
            else if(key.equals(getString(R.string.setting_contact_sort_order))){
                String sortOrder = prefs.getString(getString(R.string.setting_contact_sort_order), "0");
                int sortOrderIndex = Integer.parseInt(sortOrder);
                System.out.println("SORT ORDER: " + sortOrderIndex); //TODO delete this
                ((ListPreference) pref).setValueIndex(sortOrderIndex);
            }
            else if(key.equals(getString(R.string.setting_phones_only))){
                boolean phonesOnly = prefs.getBoolean(getString(R.string.setting_phones_only), true);
                pref.setDefaultValue(phonesOnly);
            }
            else if(key.equals(getString(R.string.setting_empty_group))){
                boolean emptyGroups = prefs.getBoolean(getString(R.string.setting_empty_group), false);
                pref.setDefaultValue(emptyGroups);
            }
            else if(key.equals(getString(R.string.setting_group_sort_order))){
                String sortOrder = prefs.getString(getString(R.string.setting_group_sort_order), getResources().getStringArray(R.array.sort_order_values)[0]);

                String s = getResources().getStringArray(R.array.sort_order_values)[0];
                System.out.println("S: " + s); //TODO delete this
                if(sortOrder != null){
                    ((ListPreference) pref).setValue(sortOrder);
                }
            }
            else if(key.equals(getString(R.string.setting_group_sort_by))){
                String sortBy = prefs.getString(getString(R.string.setting_group_sort_by), getResources().getStringArray(R.array.group_sort_by_values)[0]);
                if(sortBy != null){
                    ((ListPreference) pref).setValue(sortBy);
                }
            }
            else if(key.equals(getString(R.string.setting_contact_name_format))){
                String nameFormat = prefs.getString(getString(R.string.setting_contact_name_format), getResources().getStringArray(R.array.name_format_values)[0]); //TODO need new values here
                System.out.println("NAME FORMAT: " + nameFormat); //TODO delete this
                if(nameFormat != null){
                    ((ListPreference) pref).setValue(nameFormat);
                }
            }
            else if(key.equals(getString(R.string.setting_contact_sort_by))){
                String sortBy = prefs.getString(getString(R.string.setting_contact_sort_by), getResources().getStringArray(R.array.contact_sort_by_values)[0]); //TODO need new values here
                if(sortBy != null){
                    ((ListPreference) pref).setValue(sortBy);
                }
            }

            pref.setOnPreferenceChangeListener(ContactsApp.getApp().controllerFactory()
                    .getController(DISPLAY_SETTINGS_CONTROLLER, Preference.OnPreferenceChangeListener.class));
        }
    }
}
