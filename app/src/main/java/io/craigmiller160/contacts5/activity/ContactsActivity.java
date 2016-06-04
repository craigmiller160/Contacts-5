package io.craigmiller160.contacts5.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import java.util.ArrayList;
import java.util.List;

import io.craigmiller160.contacts5.R;
import io.craigmiller160.contacts5.adapter.ContactsTabsPagerAdapter;
import io.craigmiller160.contacts5.controller.ControllerFactory;
import io.craigmiller160.contacts5.fragment.AllContactsPage;
import io.craigmiller160.contacts5.fragment.AllGroupsPage;
import io.craigmiller160.contacts5.model.Contact;
import io.craigmiller160.contacts5.model.ContactGroup;
import io.craigmiller160.contacts5.model.ContactsDataCallback;
import io.craigmiller160.contacts5.service.ContactsRetrievalService;
import io.craigmiller160.contacts5.service.PermissionsService;
import io.craigmiller160.contacts5.service.ServiceFactory;

import static io.craigmiller160.contacts5.util.ContactsConstants.*;

/**
 * Created by craig on 5/4/16.
 */
public class ContactsActivity extends AppCompatActivity implements ContactsDataCallback {

    //TODO ensure that the accounts to display preference is properly updated after permission is provided

    private static final String TAG = "ContactsActivity";

    private PermissionsService permissionsService;
    private ContactsRetrievalService contactsService;
    private ContactsTabsPagerAdapter tabsAdapter;

    private AllContactsPage allContactsPage;
    private AllGroupsPage allGroupsPage;

    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        Log.v(TAG, "Creating ContactsActivity");
        setContentView(R.layout.activity_contacts);

        int state = RECREATE_CHANGE;
        if(savedInstance != null && savedInstance.getInt(STATE_CHANGE) > 0){
            state = savedInstance.getInt(STATE_CHANGE);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.contactsActivityToolbar);
        setSupportActionBar(toolbar);

        findViewById(R.id.addContact).setOnClickListener(
                ControllerFactory.getInstance().getController(ADD_CONTACT_CONTROLLER, View.OnClickListener.class));

        this.permissionsService = ServiceFactory.getInstance().getPermissionsService();
        this.contactsService = ServiceFactory.getInstance().getContactsRetrievalService();

        //Check permissions and load contacts
        if(!permissionsService.hasReadContactsPermission()){
            permissionsService.requestReadContactsPermission(this);
        }

        //Get the existing instances of the fragments, if they exist
        if(savedInstance != null){
            Fragment oldContactsPage = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.contactsTabsViewPager + ":" + 0);
            if(oldContactsPage != null && oldContactsPage instanceof AllContactsPage){
                allContactsPage = (AllContactsPage) oldContactsPage;
            }
            else{
                allContactsPage = new AllContactsPage();
            }

            Fragment oldGroupsPage = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.contactsTabsViewPager + ":" + 1);
            if(oldGroupsPage != null && oldGroupsPage instanceof AllGroupsPage){
                allGroupsPage = (AllGroupsPage) oldGroupsPage;
            }
            else{
                allGroupsPage = new AllGroupsPage();
            }
        }
        else{
            allContactsPage = new AllContactsPage();
            allGroupsPage = new AllGroupsPage();
        }

        ViewPager viewPager = (ViewPager) findViewById(R.id.contactsTabsViewPager);

        ContactsTabsPagerAdapter tabsAdapter = new ContactsTabsPagerAdapter(getSupportFragmentManager(), allContactsPage, allGroupsPage);
        viewPager.setAdapter(tabsAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.contactsActivityTabs);
        tabLayout.setupWithViewPager(viewPager);

        if(savedInstance != null){
            Object o = savedInstance.getSerializable(CONTACTS_LIST);
            if(o != null && o instanceof List){
                allContactsPage.setContactsList((List<Contact>) o);
            }
            else{
                contactsService.loadAllContacts(this);
            }

            o = savedInstance.getSerializable(GROUPS_LIST);
            if(o != null && o instanceof List){
                allGroupsPage.setGroupsList((List<ContactGroup>) o);
            }
            else{
                contactsService.loadAllGroups(this);
            }
        }
        else{
            contactsService.loadAllContacts(this);
            contactsService.loadAllGroups(this);
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
        if(allContactsPage != null){
            List<Contact> contacts = allContactsPage.getContactsList();
            if(contacts != null){
                savedState.putSerializable(CONTACTS_LIST, (ArrayList<Contact>) contacts);
            }
        }

        if(allGroupsPage != null){
            List<ContactGroup> groups = allGroupsPage.getGroupsList();
            if(groups != null){
                savedState.putSerializable(GROUPS_LIST, (ArrayList<ContactGroup>) groups);
            }
        }

        super.onSaveInstanceState(savedState);
    }

    @Override
    public void onActivityResult(final int requestCode, int resultCode, Intent data) {
        if(!(requestCode == SELECT_GROUP_ID)){
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
            startActivityForResult(intent, SETTINGS_ACTIVITY_ID);
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
        allContactsPage.setContactsList(contacts);
    }

    @Override
    public void setGroupsList(List<ContactGroup> groups) {
        allGroupsPage.setGroupsList(groups);
    }
}
