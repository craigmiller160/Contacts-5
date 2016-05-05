package io.craigmiller160.contacts5.old.activity;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.view.MenuItem;

import io.craigmiller160.contacts5.R;
import io.craigmiller160.contacts5.old.service.AccountService;
import io.craigmiller160.contacts5.old.service.ContactsPreferences;

import java.util.List;
import java.util.Set;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class DisplaySettingsActivity extends AppCompatPreferenceActivity{

    //TODO clean up this mess of a class with all the default crap

    private static final ContactsPreferences settings = ContactsPreferences.getInstance();

    private static final String TAG = "DisplaySettingsActivity";

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     */
    private static void bindPreferenceSummaryToValue(Preference preference, Context context) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(new ContactsDisplayPreferenceListener(context));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();

        getFragmentManager().beginTransaction().replace(
                android.R.id.content, new DisplaySettingsFragment()).commit();


    }

    @Override
    public void onBackPressed(){
        endAndReturnToContacts();
    }

    private void endAndReturnToContacts(){
        //TODO don't need a separate method just to call finish() now that other stuff is gone
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == android.R.id.home){
            Log.d(TAG, "Leaving Display Settings");
            endAndReturnToContacts();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        //loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    @Override
    protected boolean isValidFragment(String fragmentName) {
        return DisplaySettingsFragment.class.getName().equals(fragmentName);
    }

    private static void loadAccountsToListPref(Preference pref, Context context){
        //TODO IllegalArgumentException if this test fails?
        if(pref instanceof MultiSelectListPreference){
            MultiSelectListPreference mPref = (MultiSelectListPreference) pref;
            String[] accountNames = AccountService.getAllContactAccountNames(context);
            mPref.setEntries(accountNames);
            mPref.setEntryValues(accountNames); //TODO consider having a different set of values... maybe...
        }
    }

    private static void setInitialValueAccountsPref(Preference pref, Context context){
        //TODO consider illegal argument exception here
        if(pref instanceof MultiSelectListPreference){
            MultiSelectListPreference mPref = (MultiSelectListPreference) pref;
            Set<String> accountsToDisplay = settings.getAccountsToDisplay();
            if(accountsToDisplay != null){
                mPref.setValues(accountsToDisplay);
            }
        }
    }

    private static void setInitialValue(Preference pref, Context context){
        //This requires the settings to have been configured at startup
        String key = pref.getKey();
        if(key.equals(context.getString(R.string.accounts_to_display_key))){
            setInitialValueAccountsPref(pref, context);
        }
        else if(key.equals(context.getString(R.string.sort_order_key))){
            String sortOrder = settings.getSortOrder();
            if(sortOrder != null){
                pref.setDefaultValue(sortOrder);
            }
        }
        else if(key.equals(context.getString(R.string.sort_by_key))){
            String sortBy = settings.getSortBy();
            if(sortBy != null){
                pref.setDefaultValue(sortBy);
            }
        }
        else if(key.equals(context.getString(R.string.name_format_key))){
            String nameFormat = settings.getNameFormat();
            if(nameFormat != null){
                pref.setDefaultValue(nameFormat);
            }
        }
    }

    private static void configurePreference(Preference pref, Context context){
        if(pref.getKey().equals(context.getString(R.string.accounts_to_display_key))){
            loadAccountsToListPref(pref, context);
        }

        setInitialValue(pref, context);
        bindPreferenceSummaryToValue(pref, context);
    }

    //TODO document this
    public static class DisplaySettingsFragment extends PreferenceFragment{

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.display_settings);
            setHasOptionsMenu(true);



            configurePreference(findPreference(getResources().getString(R.string.accounts_to_display_key)), getActivity());
            configurePreference(findPreference(getResources().getString(R.string.sort_order_key)), getActivity());
            //configurePreference(findPreference(getResources().getString(R.string.sort_by_key)), getActivity());
            //configurePreference(findPreference(getResources().getString(R.string.name_format_key)), getActivity());
            configurePreference(findPreference(getResources().getString(R.string.phones_only_key)), getActivity());
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                System.out.println("Activity: " + getActivity().getClass().getName());
                startActivity(new Intent(getActivity(), ContactsActivity.class));
                return true;
            }

            return false;
        }

    }

    private static class ContactsDisplayPreferenceListener implements Preference.OnPreferenceChangeListener{

        private Context context;

        public ContactsDisplayPreferenceListener(Context context){
            this.context = context;
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String key = preference.getKey();
            Log.d(TAG, "Preference changed: Key: " + key + " New Value: " + newValue);
            if(key.equals(context.getString(R.string.accounts_to_display_key))){
                settings.setDisplaySettingProperty(key, newValue, context);
                return true;
            }
            else if(key.equals(context.getString(R.string.sort_order_key))){
                settings.setDisplaySettingProperty(key, newValue, context);
                return true;
            }
            else if(key.equals(context.getString(R.string.sort_by_key))){
                settings.setDisplaySettingProperty(key, newValue, context);
                return true;
            }
            else if(key.equals(context.getString(R.string.name_format_key))){
                settings.setDisplaySettingProperty(key, newValue, context);
                return true;
            }
            else if(key.equals(context.getString(R.string.phones_only_key))){
                settings.setDisplaySettingProperty(key, newValue, context);
                return true;
            }
            return false;
        }
    }
}
