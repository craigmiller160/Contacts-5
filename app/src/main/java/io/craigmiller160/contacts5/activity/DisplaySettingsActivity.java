package io.craigmiller160.contacts5.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import io.craigmiller160.contacts5.R;
import io.craigmiller160.contacts5.view.AndroidActivityView;
import io.craigmiller160.contacts5.view.AndroidFragmentView;

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

        //noinspection SimplifiableIfStatement
        if(id == android.R.id.home){
            Log.d(TAG, "Leaving Display Settings");
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        return DisplaySettingsFragment.class.getName().equals(fragmentName);
    }

    public static class DisplaySettingsFragment extends AndroidPrefFragment{

        @Override
        public void onCreate(Bundle savedInstance){
            super.onCreate(savedInstance);
            setHasOptionsMenu(true);
        }

        @Override
        protected AndroidFragmentView getAndroidView() {
            return new DisplaySettingsFragmentView(this);
        }
    }

    private static class DisplaySettingsFragmentView extends AndroidFragmentView{

        public DisplaySettingsFragmentView(Fragment fragment) {
            super(fragment);
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
