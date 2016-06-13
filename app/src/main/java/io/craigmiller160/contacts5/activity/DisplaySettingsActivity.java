package io.craigmiller160.contacts5.activity;

import android.content.Intent;
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

        setupActionBar();

        getFragmentManager().beginTransaction().replace(
                android.R.id.content, new DisplaySettingsFragment()).commit();
    }

    //TODO consider if there is some way to move this to a separate view class... may not be worth the effort
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onBackPressed(){
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == android.R.id.home){
            Log.v(TAG, "Leaving Display Settings");
            finish();
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
            Log.d(TAG, "Creating DisplaySettingsFragment"); //TODO delete this
            addPreferencesFromResource(R.xml.display_settings);
            setHasOptionsMenu(true);
            this.accountService = ContactsApp.getApp().serviceFactory().getAccountService();

            configurePreference(findPreference(getString(R.string.accounts_to_display_prop)));
            configurePreference(findPreference(getString(R.string.sort_order_prop)));
            configurePreference(findPreference(getString(R.string.phones_only_prop)));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                getActivity().finish();
                return true;
            }

            return false;
        }

        private void configurePreference(Preference pref){
            String key = pref.getKey();
            if(key.equals(getString(R.string.accounts_to_display_prop))){
                //TODO consider illegal argument exception
                if(pref instanceof MultiSelectListPreference){
                    MultiSelectListPreference mPref = (MultiSelectListPreference) pref;

                    //Set the initial list of all account names
                    String[] accountNames = accountService.getAllContactAccountNames();
                    mPref.setEntries(accountNames);
                    mPref.setEntryValues(accountNames);

                    //Get the values to be selected and set them
                    Set<String> accountsToDisplay = PreferenceManager.getDefaultSharedPreferences(getActivity())
                            .getStringSet(getString(R.string.accounts_to_display_prop), accountService.getAllContactAccountNamesSet());
                    if(accountsToDisplay != null){
                        mPref.setValues(accountsToDisplay);
                    }
                }
            }
            else if(key.equals(getString(R.string.sort_order_prop))){
                String sortOrder = PreferenceManager.getDefaultSharedPreferences(getActivity())
                        .getString(getString(R.string.sort_order_prop), getResources().getStringArray(R.array.sort_order_values)[0]);
                if(sortOrder != null){
                    ((ListPreference) pref).setValue(sortOrder);
                }
            }
            else if(key.equals(getString(R.string.sort_by_prop))){
                String sortBy = PreferenceManager.getDefaultSharedPreferences(getActivity())
                        .getString(getString(R.string.sort_by_prop), getResources().getStringArray(R.array.sort_by_values)[0]);
                if(sortBy != null){
                    pref.setDefaultValue(sortBy);
                }
            }
            else if(key.equals(getString(R.string.phones_only_prop))){
                boolean phonesOnly = PreferenceManager.getDefaultSharedPreferences(getActivity())
                        .getBoolean(getString(R.string.phones_only_prop), true);
                pref.setDefaultValue(phonesOnly);
            }

            pref.setOnPreferenceChangeListener(ContactsApp.getApp().controllerFactory()
                    .getController(DISPLAY_SETTINGS_CONTROLLER, Preference.OnPreferenceChangeListener.class));
        }
    }
}
