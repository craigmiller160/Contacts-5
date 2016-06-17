package io.craigmiller160.contacts5.activity;

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
            configurePreference(findPreference(getString(R.string.setting_sort_order)));
            configurePreference(findPreference(getString(R.string.setting_phones_only)));
            configurePreference(findPreference(getString(R.string.setting_empty_group)));
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
            if(key.equals(getString(R.string.setting_accounts_to_display))){
                if(pref instanceof MultiSelectListPreference){
                    MultiSelectListPreference mPref = (MultiSelectListPreference) pref;

                    //Set the initial list of all account names
                    String[] accountNames = accountService.getAllContactAccountNames();
                    mPref.setEntries(accountNames);
                    mPref.setEntryValues(accountNames);

                    //Get the values to be selected and set them
                    Set<String> accountsToDisplay = PreferenceManager.getDefaultSharedPreferences(getActivity())
                            .getStringSet(getString(R.string.setting_accounts_to_display), accountService.getAllContactAccountNamesSet());
                    if(accountsToDisplay != null){
                        mPref.setValues(accountsToDisplay);
                    }
                }
            }
            else if(key.equals(getString(R.string.setting_sort_order))){
                String sortOrder = PreferenceManager.getDefaultSharedPreferences(getActivity())
                        .getString(getString(R.string.setting_sort_order), getResources().getStringArray(R.array.sort_order_values)[0]);
                if(sortOrder != null){
                    ((ListPreference) pref).setValue(sortOrder);
                }
            }
            else if(key.equals(getString(R.string.setting_phones_only))){
                boolean phonesOnly = PreferenceManager.getDefaultSharedPreferences(getActivity())
                        .getBoolean(getString(R.string.setting_phones_only), true);
                pref.setDefaultValue(phonesOnly);
            }
            else if(key.equals(getString(R.string.setting_empty_group))){
                boolean emptyGroups = PreferenceManager.getDefaultSharedPreferences(getActivity())
                        .getBoolean(getString(R.string.setting_empty_group), false);
                pref.setDefaultValue(emptyGroups);
            }

            pref.setOnPreferenceChangeListener(ContactsApp.getApp().controllerFactory()
                    .getController(DISPLAY_SETTINGS_CONTROLLER, Preference.OnPreferenceChangeListener.class));
        }
    }
}
