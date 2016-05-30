package io.craigmiller160.contacts5.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import java.util.List;

import io.craigmiller160.contacts5.R;
import io.craigmiller160.contacts5.adapter.ContactsTabsPagerAdapter;
import io.craigmiller160.contacts5.controller.ControllerFactory;
import io.craigmiller160.contacts5.fragment.AllContactsPage;
import io.craigmiller160.contacts5.fragment.AllGroupsPage;
import io.craigmiller160.contacts5.model.Contact;
import io.craigmiller160.contacts5.model.ContactGroup;
import io.craigmiller160.contacts5.model.ContactsDataCallback;
import io.craigmiller160.contacts5.service.ContactsPrefsService;
import io.craigmiller160.contacts5.service.ContactsRetrievalService;
import io.craigmiller160.contacts5.service.PermissionsService;
import io.craigmiller160.contacts5.service.ResourceService;
import io.craigmiller160.contacts5.service.ServiceFactory;

import static io.craigmiller160.contacts5.util.ContactsConstants.*;

/**
 * Created by craig on 5/4/16.
 */
public class ContactsActivity extends AppCompatActivity {

    //TODO ensure that the accounts to display preference is properly updated after permission is provided

    private static final String TAG = "ContactsActivity";

    private PermissionsService permissionsService;
    private ResourceService resources;
    private ContactsRetrievalService contactsService;
    private ContactsPrefsService contactsPrefsService;
    private ContactsTabsPagerAdapter tabsAdapter;

    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        Log.v(TAG, "Creating ContactsActivity");
        setContentView(R.layout.activity_contacts);
        this.permissionsService = ServiceFactory.getInstance().getPermissionsService();
        this.resources = ServiceFactory.getInstance().getResourceService();
        this.contactsService = ServiceFactory.getInstance().getContactsRetrievalService();
        this.contactsPrefsService = ServiceFactory.getInstance().getContactsPrefsService();

        //Check permissions and load contacts
        if(!permissionsService.hasReadContactsPermission()){
            permissionsService.requestReadContactsPermission(this);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.contactsActivityToolbar);
        setSupportActionBar(toolbar);

        findViewById(R.id.addContact).setOnClickListener(
                ControllerFactory.getInstance().getController(ADD_CONTACT_CONTROLLER, View.OnClickListener.class));

        configureTabs();
    }

    private ContactsTabsPagerAdapter configureTabs(){
        ViewPager viewPager = (ViewPager) findViewById(R.id.contactsTabsViewPager);
        tabsAdapter = new ContactsTabsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabsAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.contactsActivityTabs);
        tabLayout.setupWithViewPager(viewPager);

        return tabsAdapter;
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
    public void onActivityResult(final int requestCode, int resultCode, Intent data) {
        //TODO review and restore this code
        if(requestCode == SETTINGS_ACTIVITY_ID){
            //Using a handler here so that recreate will be called after main thread has finished current task
            Handler h = new Handler(Looper.getMainLooper());
            h.post(new Runnable() {
                @Override
                public void run() {
                    tabsAdapter.loadContacts();
                }
            });
        }
//        else if(requestCode == CONTACT_ACTION_VIEW_ID){
//            if(data != null){
//                System.out.println("View Contact: Data Not Null");
//            }
//            else{
//                System.out.println("View Contact: Data Null");
//            }
//
//            contactModified();
//        }
//        else if(requestCode == NEW_CONTACT_VIEW_ID){
//            if(data != null){
//                System.out.println("New Contact: Data Not Null");
//                System.out.println("URI: " + data.getData());
//            }
//            else{
//                System.out.println("New Contact: Data Null");
//            }
//
//            contactAdded();
//        }
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
                    View view = findViewById(R.id.contactsActivityLayout);
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
}
