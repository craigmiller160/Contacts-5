package io.craigmiller160.contacts5.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import org.apache.commons.lang3.StringUtils;

import io.craigmiller160.contacts5.ContactsApp;
import io.craigmiller160.contacts5.R;
import io.craigmiller160.contacts5.controller.OnClickController;
import io.craigmiller160.contacts5.controller.SearchController;
import io.craigmiller160.contacts5.fragment.FragmentChanger;
import io.craigmiller160.contacts5.fragment.RefreshableView;
import io.craigmiller160.contacts5.log.Logger;
import io.craigmiller160.contacts5.model.AndroidModel;
import io.craigmiller160.contacts5.service.ContactsService;
import io.craigmiller160.contacts5.util.AndroidSystemUtil;
import io.craigmiller160.contacts5.util.CodeParser;
import io.craigmiller160.contacts5.util.MD5;

import static io.craigmiller160.contacts5.util.ContactsConstants.SETTINGS_ACTIVITY_REQUEST;

/**
 * Created by craig on 5/4/16.
 */
public class ContactsActivity extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener {

    private static final String TAG = "ContactsActivity";

    private static final Logger logger = Logger.newLogger(TAG);
    private AndroidSystemUtil androidSystemUtil;
    private FragmentChanger fragmentChanger;
    private ContactsActivityViewChanger viewChanger;
    private AndroidModel contactsModel;
    private boolean reloadContacts = true;

    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        logger.i(TAG, "Creating ContactsActivity");
        setContentView(R.layout.activity_contacts);

        this.androidSystemUtil = new AndroidSystemUtil(this);
        this.fragmentChanger = new FragmentChanger(this);
        this.viewChanger = ContactsActivityViewChanger.getInstance();
        this.viewChanger.setActivity(this);
        this.contactsModel = ContactsApp.getApp().modelFactory().getModel(R.string.model_contacts);

        if(savedInstance != null){
            logger.v(TAG, "Restoring ContactsModel state on Activity creation");
            contactsModel.restoreState(savedInstance);
            reloadContacts = false;
        }
        else{
            setTitle(getString(R.string.activity_contacts_name_label));
            MobileAds.initialize(getApplicationContext(), getString(R.string.admob_app_id));
        }

        getSupportFragmentManager().addOnBackStackChangedListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.contacts_activity_toolbar);
        setSupportActionBar(toolbar);

        AdView mAdView = (AdView) findViewById(R.id.ad_activity_banner);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice(getString(R.string.device_id_md5))
                .build();
        mAdView.loadAd(adRequest);

        OnClickController onClickController = new OnClickController(this);
        onClickController.addArg(R.string.on_click_controller_type, OnClickController.ADD_BUTTON);
        findViewById(R.id.add_contact_fab).setOnClickListener(onClickController);

        //Check permissions
        if(!androidSystemUtil.permissions().hasReadContactsPermission()){
            androidSystemUtil.permissions().requestReadContactsPermission(this);
        }

        if(savedInstance == null){
            contactsModel.setProperty(R.string.prop_displayed_fragment, getString(R.string.tag_tabs_fragment));
            fragmentChanger.addTabsFragment(getSupportFragmentManager());
        }
        else{
            String displayedFragment = contactsModel.getProperty(R.string.prop_displayed_fragment, String.class);
            if(!StringUtils.isEmpty(displayedFragment)){
                if(displayedFragment.equals(getString(R.string.tag_tabs_fragment))){
                    viewChanger.showTabsFragment();
                }
                else{
                    viewChanger.showNoTabsFragment();
                }
            }
        }
    }

    @Override
    public void setTitle(CharSequence title){
        super.setTitle(title);
    }

    private String getAndroidIdMd5(){
        String androidId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        return MD5.encode(androidId);
    }

    @Override
    public void onBackStackChanged() {
        String displayedFragment = contactsModel.getProperty(R.string.prop_displayed_fragment, String.class);
        if(!StringUtils.isEmpty(displayedFragment)){
            if(displayedFragment.equals(getString(R.string.tag_tabs_fragment))){
                viewChanger.showTabsFragment();
            }
            else if(displayedFragment.equals(getString(R.string.tag_no_tabs_fragment))){
                viewChanger.showNoTabsFragment();
            }
        }
    }

    @Override
    protected void onRestart() {
        logger.i(TAG, "Restarting ContactsActivity");
        super.onRestart();
        reloadContacts = true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(reloadContacts){
            reloadContacts = false;
            reloadContacts();
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstance){
        if(contactsModel != null && contactsModel.getPropertyCount() == 0){
            logger.v(TAG, "Restoring ContactsModel state on Activity restore");
            contactsModel.restoreState(savedInstance);
        }
        super.onRestoreInstanceState(savedInstance);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        //Set up the search component
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search_id).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        SearchController searchController = new SearchController(this);
        searchView.setOnQueryTextListener(searchController);

        MenuItem item = menu.findItem(R.id.menu_search_id);
        MenuItemCompat.setOnActionExpandListener(item, searchController);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.menu_grant_permissions_id);
        if(item != null){
            item.setVisible(!androidSystemUtil.permissions().hasReadContactsPermission());
        }

        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle savedState){
        logger.v(TAG, "Storing ContactsModel state");
        contactsModel.storeState(savedState);
        super.onSaveInstanceState(savedState);
    }

    @Override
    public void onActivityResult(final int requestCode, int resultCode, Intent data) {
        logger.d(TAG, "Activity result received. Request: " + CodeParser.parseRequestCode(requestCode) + " Response: " + CodeParser.parseResultCode(resultCode));
        reloadContacts();
    }

    private void reloadContacts(){
        if(androidSystemUtil.permissions().hasReadContactsPermission()){
            Intent intent = new Intent(this, ContactsService.class);
            intent.putExtra(ContactsService.LOAD_CONTACTS, true);
            intent.putExtra(ContactsService.LOAD_GROUPS, true);

            String displayedFragment = contactsModel.getProperty(R.string.prop_displayed_fragment, String.class);
            Long groupId = contactsModel.getProperty(R.string.prop_selected_group_id, Long.class);
            String groupName = contactsModel.getProperty(R.string.prop_selected_group_name, String.class);
            if(displayedFragment != null && displayedFragment.equals(getString(R.string.tag_no_tabs_fragment)) && groupId != null && groupId >= 0){
                intent.putExtra(ContactsService.LOAD_CONTACTS_IN_GROUP, true);
                intent.putExtra(getString(R.string.prop_selected_group_id), groupId);
                intent.putExtra(getString(R.string.prop_selected_group_name), groupName);
            }
            startService(intent);
        }
    }

    @Override
    public void onBackPressed(){
        String displayedFragment = contactsModel.getProperty(R.string.prop_displayed_fragment, String.class);
        if(StringUtils.isEmpty(displayedFragment) || displayedFragment.equals(getString(R.string.tag_tabs_fragment))){
            logger.i(TAG, "Closing ContactsActivity");
            finish();
        }
        else{
            backAction();
        }
    }

    private void backAction(){
        String groupName = contactsModel.getProperty(R.string.prop_selected_group_name, String.class);
        logger.d(TAG, "Leaving Group: " + groupName);
        contactsModel.setProperty(R.string.prop_displayed_fragment, getString(R.string.tag_tabs_fragment));
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case AndroidSystemUtil.CONTACTS_PERMISSION_REQUEST:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    logger.d(TAG, "Necessary permissions were granted");
                    String displayedFragment = contactsModel.getProperty(R.string.prop_displayed_fragment, String.class);
                    RefreshableView refreshableView = (RefreshableView) getSupportFragmentManager().findFragmentByTag(displayedFragment);
                    refreshableView.refreshView();
                    reloadContacts();
                }
                else{
                    logger.e(TAG, "Necessary permissions were denied!");
                    View view = findViewById(R.id.activity_contacts_layout);
                    Snackbar snackbar = Snackbar.make(view, getString(R.string.permission_denied_snackbar_text), Snackbar.LENGTH_LONG)
                            .setAction(getString(R.string.grant_permission_snackbar_button), new View.OnClickListener(){
                                @Override
                                public void onClick(View v) {
                                    androidSystemUtil.permissions().requestReadContactsPermission(ContactsActivity.this);
                                }
                            });
                    snackbar.setActionTextColor(Color.YELLOW);
                    snackbar.show();
                }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_display_settings_id) {
            Intent intent = new Intent(this, DisplaySettingsActivity.class);
            startActivityForResult(intent, SETTINGS_ACTIVITY_REQUEST);
            return true;
        }
        else if(item.getItemId() == R.id.menu_grant_permissions_id){
            if(!androidSystemUtil.permissions().hasReadContactsPermission()){
                androidSystemUtil.permissions().requestReadContactsPermission(this);
            }
            return true;
        }
        else if(item.getItemId() == android.R.id.home){
            backAction();
            return true;
        }

        return false;
    }
}
