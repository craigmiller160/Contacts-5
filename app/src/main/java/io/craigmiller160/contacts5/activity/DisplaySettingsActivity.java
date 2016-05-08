package io.craigmiller160.contacts5.activity;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Set;

import io.craigmiller160.contacts5.R;
import io.craigmiller160.contacts5.fragment.AndroidPrefFragment;
import io.craigmiller160.contacts5.service.AccountService;
import io.craigmiller160.contacts5.service.ContactsPrefsService;
import io.craigmiller160.contacts5.service.ResourceService;
import io.craigmiller160.contacts5.service.ServiceFactory;
import io.craigmiller160.contacts5.view.AndroidFragmentView;
import io.craigmiller160.locus.Locus;

import static io.craigmiller160.contacts5.util.ContactsConstants.*;

/**
 * Created by craig on 5/7/16.
 */
public class DisplaySettingsActivity extends AppCompatPreferenceActivity {

    private static final String TAG = "DisplaySettingsActivity";

    private ContactsPrefsService contactsPrefsService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.contactsPrefsService = ServiceFactory.getInstance().getContactsPrefsService();

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
            Log.d(TAG, "Leaving Display Settings");
            contactsPrefsService.saveAllPreferences();
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        return DisplaySettingsFragment.class.getName().equals(fragmentName);
    }

    public static class DisplaySettingsFragment extends AndroidPrefFragment {

        @Override
        public void onCreate(Bundle savedInstance){
            super.onCreate(savedInstance);
            Log.d(TAG, "Creating DisplaySettingsFragment"); //TODO delete this
        }

        @Override
        protected AndroidFragmentView getAndroidView() {
            return new DisplaySettingsFragmentView(this);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                System.out.println("Activity: " + getActivity().getClass().getName()); //TODO delete this
                startActivity(new Intent(getActivity(), ContactsActivity.class));
                return true;
            }

            return false;
        }
    }

    private static class DisplaySettingsFragmentView extends AndroidFragmentView{

        private AccountService accountService;
        private ResourceService resources;

        public DisplaySettingsFragmentView(Fragment fragment) {
            super(fragment);
        }

        @Override
        public void onCreate(){
            super.onCreate();
            getFragment().setHasOptionsMenu(true);
            this.accountService = ServiceFactory.getInstance().getAccountService();
            this.resources = ServiceFactory.getInstance().getResourceService();

            configurePreference(findPreference(R.string.accounts_to_display_prop));
            configurePreference(findPreference(R.string.sort_order_prop));
            configurePreference(findPreference(R.string.phones_only_prop));
        }

        private Preference findPreference(int prefId){
            return getPreferenceFragment().findPreference(getPreferenceFragment().getResources().getString(prefId));
        }

        //TODO move this to the superclass with a type check before returning something
        private PreferenceFragment getPreferenceFragment(){
            return (PreferenceFragment) getFragment();
        }

        private void configurePreference(Preference pref){
            String key = pref.getKey();
            if(key.equals(resources.getString(R.string.accounts_to_display_prop))){
                //TODO consider illegal argument exception
                if(pref instanceof MultiSelectListPreference){
                    MultiSelectListPreference mPref = (MultiSelectListPreference) pref;

                    //Set the initial list of all account names
                    String[] accountNames = accountService.getAllContactAccountNames();
                    mPref.setEntries(accountNames);
                    mPref.setEntryValues(accountNames);

                    //Get the values to be selected and set them
                    Set<String> accountsToDisplay = Locus.model.getValue(
                            resources.getString(R.string.accounts_to_display_prop), Set.class);
                    if(accountsToDisplay != null){
                        mPref.setValues(accountsToDisplay);
                    }
                }
            }
            else if(key.equals(resources.getString(R.string.sort_order_prop))){
                String sortOrder = Locus.model.getValue(resources.getString(R.string.sort_order_prop), String.class);
                if(sortOrder != null){
                    pref.setDefaultValue(sortOrder);
                }
            }
            else if(key.equals(resources.getString(R.string.sort_by_prop))){
                String sortBy = Locus.model.getValue(resources.getString(R.string.sort_by_prop), String.class);
                if(sortBy != null){
                    pref.setDefaultValue(sortBy);
                }
            }

            pref.setOnPreferenceChangeListener(
                    Locus.controller.getController(DISPLAY_SETTINGS_CONTROLLER,
                            Preference.OnPreferenceChangeListener.class,
                            getFragment().getActivity()));
        }

        @Override
        protected int getViewResourceId() {
            return R.xml.display_settings;
        }

        @Override
        public void onCreateOptionsMenu(Menu menu) {
            //Do nothing
        }

        @Override
        public void onPrepareOptionsMenu(Menu menu) {
            //Do nothing
        }
    }
}
