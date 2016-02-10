package io.craigmiller160.contacts5.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import io.craigmiller160.contacts5.ContactsApplication;
import io.craigmiller160.contacts5.R;
import io.craigmiller160.contacts5.adapter.ContactsArrayAdapter;
import io.craigmiller160.contacts5.controller.ContactUpdateController;
import io.craigmiller160.contacts5.fragment.AllContactsPage;
import io.craigmiller160.contacts5.fragment.ContactsGroupsPage;
import io.craigmiller160.contacts5.adapter.TabsPagerAdapter;
import io.craigmiller160.contacts5.model.Contact;
import io.craigmiller160.contacts5.model.LookupContact;
import io.craigmiller160.contacts5.service.ContactsPreferences;
import io.craigmiller160.contacts5.service.ContactsRetrievalService;
import io.craigmiller160.contacts5.service.PermissionsManager;

public class ContactsActivity extends AppCompatActivity {

    private static final String TAG = "ContactsActivity";

    private static final int SETTINGS_ACTIVITY_ID = 301;
    public static final int CONTACT_ACTION_VIEW_ID = 302;
    public static final int NEW_CONTACT_VIEW_ID = 303;

    public static final String INTENT_KEY_FINISH_ACTIVITY_ON_SAVE_COMPLETED = "finishActivityOnSaveCompleted";

    private TabsPagerAdapter tabsAdapter;

    private ContactsRetrievalService service = new ContactsRetrievalService();
    private ContactUpdateController contactUpdateController = new ContactUpdateController(this);

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PermissionsManager.CONTACTS_PERMISSION_REQUEST:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Log.d(TAG, "Contacts Permission Granted");
                    Intent intent = new Intent(this, ContactsActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    Log.e(TAG, "Contacts permission denied");
                    View view = findViewById(R.id.contactsActivityLayout);
                    //TODO move text to Strings
                    Snackbar snackbar = Snackbar.make(view, "Permission Denied!", Snackbar.LENGTH_LONG)
                            .setAction("GRANT", new View.OnClickListener(){
                                @Override
                                public void onClick(View v) {
                                    Log.d(TAG, "Requesting Contacts permissions from snackbar");
                                    PermissionsManager.requestReadContactsPermission(ContactsActivity.this);
                                }
                            });
                    snackbar.setActionTextColor(Color.YELLOW);
                    snackbar.show();
                }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        //Check permissions
        if(!PermissionsManager.hasReadContactsPermission(this)){
            Log.d(TAG, "Requesting Contacts permissions");
            PermissionsManager.requestReadContactsPermission(this);
        }

        ContactsPreferences.getInstance().loadSavedPreferences(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.contactsActivityToolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton addContactButton = (FloatingActionButton) findViewById(R.id.addContact);
        addContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_INSERT);
                intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
                intent.putExtra(INTENT_KEY_FINISH_ACTIVITY_ON_SAVE_COMPLETED, true);
                startActivityForResult(intent, NEW_CONTACT_VIEW_ID);
            }
        });

        Context appContext = getApplicationContext();
        if(appContext == null){
            System.out.println("AppContext Null");
        }
        else{
            System.out.println(appContext.getClass().getName());
        }

        configureTabs();
    }

    private void configureTabs(){
        ViewPager viewPager = (ViewPager) findViewById(R.id.contactsTabsViewPager);
        tabsAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        tabsAdapter.addFragmentPage(new AllContactsPage(), AllContactsPage.TITLE);
        tabsAdapter.addFragmentPage(new ContactsGroupsPage(), ContactsGroupsPage.TITLE);
        viewPager.setAdapter(tabsAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.contactsActivityTabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void configureTabsAdapter(){

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        MenuItem item = menu.findItem(R.id.grantPermissions);
        if(item != null){
            item.setVisible(!PermissionsManager.hasReadContactsPermission(this));
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.displaySettings) {
            Log.i(TAG, "Opening display settings");
            Intent intent = new Intent(this, DisplaySettingsActivity.class);
            startActivityForResult(intent, SETTINGS_ACTIVITY_ID);
            return true;
        }
        else if(id == R.id.grantPermissions){
            if(!PermissionsManager.hasReadContactsPermission(this)){
                Log.i(TAG, "Requesting Permissions from menu item");
                PermissionsManager.requestReadContactsPermission(this);
            }
        }

        return false;
    }

    @Override
    protected void onStop() {
        //Ensure all preferences are stored before closing the application

        super.onStop();
    }

    @Override
    protected void onDestroy(){
        //TODO still being called a bit too much because of the recreate()
        ContactsPreferences.getInstance().storeSavedPreferences(this);
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == SETTINGS_ACTIVITY_ID){
            //Using a handler here so that recreate will be called after main thread has finished current task
            Handler h = new Handler();
            h.post(new Runnable() {
                @Override
                public void run() {
                    ContactsActivity.this.recreate();
                }
            });
        }
        else if(requestCode == CONTACT_ACTION_VIEW_ID){
            if(data != null){
                System.out.println("View Contact: Data Not Null");
            }
            else{
                System.out.println("View Contact: Data Null");
            }

            contactUpdateController.contactModified();
        }
        else if(requestCode == NEW_CONTACT_VIEW_ID){
            if(data != null){
                System.out.println("New Contact: Data Not Null");
                System.out.println("URI: " + data.getData());
            }
            else{
                System.out.println("New Contact: Data Null");
            }

            contactUpdateController.contactAdded();
        }
    }
}
