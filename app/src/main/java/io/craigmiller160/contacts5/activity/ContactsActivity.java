package io.craigmiller160.contacts5.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import io.craigmiller160.contacts5.ContactsApp;
import io.craigmiller160.contacts5.R;
import io.craigmiller160.contacts5.controller.OnClickController;
import io.craigmiller160.contacts5.fragment.FragmentChanger;
import io.craigmiller160.contacts5.model.AndroidModel;
import io.craigmiller160.contacts5.service.ContactsRetrievalService;
import io.craigmiller160.contacts5.service.PermissionsService;
import io.craigmiller160.contacts5.util.CodeParser;

import static io.craigmiller160.contacts5.util.ContactsConstants.ADD_CONTACT_CONTROLLER;
import static io.craigmiller160.contacts5.util.ContactsConstants.CONTACTS_MODEL;
import static io.craigmiller160.contacts5.util.ContactsConstants.DISPLAYED_FRAGMENT;
import static io.craigmiller160.contacts5.util.ContactsConstants.GROUPS_LIST;
import static io.craigmiller160.contacts5.util.ContactsConstants.NO_TABS_FRAGMENT_TAG;
import static io.craigmiller160.contacts5.util.ContactsConstants.SELECTED_GROUP_ID;
import static io.craigmiller160.contacts5.util.ContactsConstants.SELECTED_GROUP_NAME;
import static io.craigmiller160.contacts5.util.ContactsConstants.SETTINGS_ACTIVITY_REQUEST;
import static io.craigmiller160.contacts5.util.ContactsConstants.TABS_FRAGMENT_TAG;

/**
 * Created by craig on 5/4/16.
 */
public class ContactsActivity extends AppCompatActivity {

    private static final String TAG = "ContactsActivity";

    private static final String REQUEST_PERMISSION_ACTION = "Grant";

    private PermissionsService permissionsService;
    private ContactsRetrievalService contactsService;
    private AndroidModel contactsModel;
    private boolean reloadContacts = true;

    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        Log.i(TAG, "Creating ContactsActivity");
        setContentView(R.layout.activity_contacts);

        this.permissionsService = ContactsApp.getApp().serviceFactory().getPermissionsService();
        this.contactsService = ContactsApp.getApp().serviceFactory().getContactsRetrievalService();
        this.contactsModel = ContactsApp.getApp().modelFactory().getModel(CONTACTS_MODEL);

        if(savedInstance != null){
            Log.v(TAG, "Restoring ContactsModel state on Activity creation");
            contactsModel.restoreState(savedInstance);
            reloadContacts = false;
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.contacts_activity_toolbar);
        setSupportActionBar(toolbar);

        //noinspection ConstantConditions
        findViewById(R.id.add_contact_fab).setOnClickListener(new OnClickController(this, null, OnClickController.ADD_BUTTON));

        //Check permissions
        if(!permissionsService.hasReadContactsPermission()){
            permissionsService.requestReadContactsPermission(this);
        }

        if(contactsModel.getProperty(DISPLAYED_FRAGMENT, String.class) == null){
            FragmentChanger.displayTabsFragment(getSupportFragmentManager());
        }
    }

    @Override
    protected void onRestart() {
        Log.i(TAG, "Restarting ContactsActivity");
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
            Log.v(TAG, "Restoring ContactsModel state on Activity restore");
            contactsModel.restoreState(savedInstance);
        }
        super.onRestoreInstanceState(savedInstance);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.grantPermissions);
        if(item != null){
            item.setVisible(!permissionsService.hasReadContactsPermission());
        }

        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle savedState){
        Log.v(TAG, "Storing ContactsModel state");
        contactsModel.storeState(savedState);
        super.onSaveInstanceState(savedState);
    }

    @Override
    public void onActivityResult(final int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "Activity result received. Request: " + CodeParser.parseRequestCode(requestCode) + " Response: " + CodeParser.parseResultCode(resultCode));
        reloadContacts();
    }

    private void reloadContacts(){
        if(permissionsService.hasReadContactsPermission()){
            contactsService.loadAllContacts();
            contactsService.loadAllGroups();
            String displayedFragment = contactsModel.getProperty(DISPLAYED_FRAGMENT, String.class);
            Long groupId = contactsModel.getProperty(SELECTED_GROUP_ID, Long.class);
            if(displayedFragment != null && displayedFragment.equals(NO_TABS_FRAGMENT_TAG) && groupId != null && groupId >= 0){
                contactsService.loadAllContactsInGroup(groupId);
            }
        }
    }

    @Override
    public void onBackPressed(){
        String displayedFragment = contactsModel.getProperty(DISPLAYED_FRAGMENT, String.class);
        if(displayedFragment == null || displayedFragment.equals(TABS_FRAGMENT_TAG)){
            Log.i(TAG, "Closing ContactsActivity");
            finish();
        }
        else{
            backAction();
        }
    }

    private void backAction(){
        String groupName = contactsModel.getProperty(SELECTED_GROUP_NAME, String.class);
        Log.d(TAG, "Leaving Group: " + groupName);
        FragmentChanger.displayTabsFragment(getSupportFragmentManager());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PermissionsService.CONTACTS_PERMISSION_REQUEST:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Log.d(TAG, "Necessary permissions were granted");
                    contactsModel.clearAllProperties();
                    Intent intent = new Intent(this, ContactsActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    Log.e(TAG, "Necessary permissions were denied!");
                    View view = findViewById(R.id.activity_contacts_layout);
                    Snackbar snackbar = Snackbar.make(view, getString(R.string.permission_denied_snackbar_text), Snackbar.LENGTH_LONG)
                            .setAction(REQUEST_PERMISSION_ACTION, new View.OnClickListener(){
                                @Override
                                public void onClick(View v) {
                                    permissionsService.requestReadContactsPermission(ContactsActivity.this);
                                }
                            });
                    snackbar.setActionTextColor(Color.YELLOW);
                    snackbar.show();
                }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.displaySettings) {
            Intent intent = new Intent(this, DisplaySettingsActivity.class);
            startActivityForResult(intent, SETTINGS_ACTIVITY_REQUEST);
            return true;
        }
        else if(item.getItemId() == R.id.grantPermissions){
            if(!permissionsService.hasReadContactsPermission()){
                permissionsService.requestReadContactsPermission(this);
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
