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

import java.util.List;

import io.craigmiller160.contacts5.R;
import io.craigmiller160.contacts5.controller.ControllerFactory;
import io.craigmiller160.contacts5.fragment.TabsFragment;
import io.craigmiller160.contacts5.model.Contact;
import io.craigmiller160.contacts5.model.ContactGroup;
import io.craigmiller160.contacts5.model.ContactsDataCallback;
import io.craigmiller160.contacts5.model.ModelFactory;
import io.craigmiller160.contacts5.service.ContactsRetrievalService;
import io.craigmiller160.contacts5.service.PermissionsService;
import io.craigmiller160.contacts5.service.ServiceFactory;

import static io.craigmiller160.contacts5.util.ContactsConstants.ADD_CONTACT_CONTROLLER;
import static io.craigmiller160.contacts5.util.ContactsConstants.CONTACTS_MODEL;
import static io.craigmiller160.contacts5.util.ContactsConstants.FRAGMENT_MODEL;
import static io.craigmiller160.contacts5.util.ContactsConstants.SELECT_GROUP_REQUEST;
import static io.craigmiller160.contacts5.util.ContactsConstants.SETTINGS_ACTIVITY_REQUEST;
import static io.craigmiller160.contacts5.util.ContactsConstants.TABS_FRAGMENT_TAG;

/**
 * Created by craig on 5/4/16.
 */
public class ContactsActivity extends AppCompatActivity implements ContactsDataCallback {

    //TODO ensure that the accounts to display preference is properly updated after permission is provided

    private static final String TAG = "ContactsActivity";

    private PermissionsService permissionsService;
    private ContactsRetrievalService contactsService;

    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        Log.v(TAG, "Creating ContactsActivity");
        setContentView(R.layout.activity_contacts);

        this.permissionsService = ServiceFactory.getInstance().getPermissionsService();
        this.contactsService = ServiceFactory.getInstance().getContactsRetrievalService();

        Toolbar toolbar = (Toolbar) findViewById(R.id.contacts_activity_toolbar);
        setSupportActionBar(toolbar);

        //noinspection ConstantConditions
        findViewById(R.id.add_contact_fab).setOnClickListener(
                ControllerFactory.getInstance().getController(ADD_CONTACT_CONTROLLER, View.OnClickListener.class));

        //Check permissions and load contacts
        if(!permissionsService.hasReadContactsPermission()){
            permissionsService.requestReadContactsPermission(this);
        }

        if(savedInstance != null){
            ModelFactory.getInstance().getModel(CONTACTS_MODEL).restoreState(savedInstance);
            ModelFactory.getInstance().getModel(FRAGMENT_MODEL).restoreState(savedInstance);
        }
        else{
            getSupportFragmentManager().beginTransaction().add(new TabsFragment(), TABS_FRAGMENT_TAG).commit();
        }
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
        ModelFactory.getInstance().getModel(CONTACTS_MODEL).storeState(savedState);
        ModelFactory.getInstance().getModel(FRAGMENT_MODEL).storeState(savedState);
        super.onSaveInstanceState(savedState);
    }

    @Override
    public void onActivityResult(final int requestCode, int resultCode, Intent data) {
        //TODO revamp the activity result behavior
        if(!(requestCode == SELECT_GROUP_REQUEST)){
            contactsService.loadAllContacts(ContactsActivity.this);
            contactsService.loadAllGroups(ContactsActivity.this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PermissionsService.CONTACTS_PERMISSION_REQUEST:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Log.d(TAG, "Contacts Permission Granted");
                    Intent intent = new Intent(this, ContactsActivity.class);
                    startActivity(intent);
                    finish(); //TODO do I really want to do this here anymore?
                }
                else{
                    Log.e(TAG, "Contacts permission denied");
                    View view = findViewById(R.id.activity_contacts_layout);
                    //TODO move text to Strings
                    Snackbar snackbar = Snackbar.make(view, getString(R.string.permission_denied_snackbar_text), Snackbar.LENGTH_LONG)
                            .setAction("GRANT", new View.OnClickListener(){
                                @Override
                                public void onClick(View v) {
                                    Log.d(TAG, "Requesting Contacts permissions from snackbar");
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
        //TODO refactor this for changes to application
        if (item.getItemId() == R.id.displaySettings) {
            Log.i(TAG, "Opening display settings");
            Intent intent = new Intent(this, DisplaySettingsActivity.class);
            startActivityForResult(intent, SETTINGS_ACTIVITY_REQUEST);
            return true;
        }
        else if(item.getItemId() == R.id.grantPermissions){
            if(!permissionsService.hasReadContactsPermission()){
                Log.i(TAG, "Requesting Permissions from menu item");
                permissionsService.requestReadContactsPermission(this);
            }
        }

        return false;
    }

    @Override
    public void setContactsList(List<Contact> contacts) {
//        allContactsPage.setContactsList(contacts);
    }

    @Override
    public void setGroupsList(List<ContactGroup> groups) {
//        allGroupsPage.setGroupsList(groups);
    }
}
