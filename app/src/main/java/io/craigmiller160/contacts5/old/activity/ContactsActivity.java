package io.craigmiller160.contacts5.old.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.beans.PropertyChangeEvent;

import io.craigmiller160.contacts5.R;
import io.craigmiller160.contacts5.old.adapter.TabsPagerAdapter;
import io.craigmiller160.contacts5.old.application.ContactsApplication;
import io.craigmiller160.contacts5.old.fragment.AllContactsPage;
import io.craigmiller160.contacts5.old.fragment.ContactsGroupsPage;
import io.craigmiller160.contacts5.old.service.ContactsPreferences;
import io.craigmiller160.contacts5.service.PermissionsService;

import static io.craigmiller160.contacts5.old.helper.ContactsHelper.ADD_CONTACT_CONTROLLER;
import static io.craigmiller160.contacts5.old.helper.ContactsHelper.CONTACTS_ACTIVITY_CONTROLLER;
import static io.craigmiller160.contacts5.old.helper.ContactsHelper.CONTACTS_STORAGE_PROP;
import static io.craigmiller160.contacts5.old.helper.ContactsHelper.CONTACT_COUNT_PROPERTY;

public class ContactsActivity extends AbstractMVPActivity {

    private static final String TAG = "ContactsActivity";

    public static final int SETTINGS_ACTIVITY_ID = 301;
    public static final int CONTACT_ACTION_VIEW_ID = 302;
    public static final int NEW_CONTACT_VIEW_ID = 303;

    public static final String INTENT_KEY_FINISH_ACTIVITY_ON_SAVE_COMPLETED = "finishActivityOnSaveCompleted";

    private TabsPagerAdapter tabsAdapter;

    private Fragment allContactsPage;
    private Fragment contactGroupsPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        //Check permissions
//        if(!PermissionsService.hasReadContactsPermission(this)){
//            Log.d(TAG, "Requesting Contacts permissions");
//            PermissionsService.requestReadContactsPermission(this);
//        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.contactsActivityToolbar);
        setSupportActionBar(toolbar);

        findViewById(R.id.addContact).setOnClickListener(getMVPApplication().getOnClickController(ADD_CONTACT_CONTROLLER, this));

        configureTabs();

        //TODO ensure this isn't called multiple times by screen rotation
        int count = (Integer) getMVPApplication().getModelProperty(CONTACT_COUNT_PROPERTY);
        if(count == 0){
            ContactsApplication.getInstance().loadAllContacts();
        }
    }

    private void configureTabs(){
        ViewPager viewPager = (ViewPager) findViewById(R.id.contactsTabsViewPager);
        tabsAdapter = new TabsPagerAdapter(getSupportFragmentManager());

        allContactsPage = new AllContactsPage();
        contactGroupsPage = new ContactsGroupsPage();

        tabsAdapter.addFragmentPage(allContactsPage, AllContactsPage.TITLE);
        tabsAdapter.addFragmentPage(contactGroupsPage, ContactsGroupsPage.TITLE);
        viewPager.setAdapter(tabsAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.contactsActivityTabs);
        tabLayout.setupWithViewPager(viewPager);
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
//            item.setVisible(!PermissionsService.hasReadContactsPermission(this));
        }

        return super.onPrepareOptionsMenu(menu);
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
    protected String getActivityControllerName() {
        return CONTACTS_ACTIVITY_CONTROLLER;
    }

    private void updateLists(){
        Log.d(TAG, "Updating contacts ListViews for changes");
        if(allContactsPage != null){
            ((AllContactsPage) allContactsPage).notifyStorageChanged();
        }
        if(contactGroupsPage != null){
            ((ContactsGroupsPage) contactGroupsPage).notifyStorageChanged();
        }
    }

    @Override
    public void updateView(PropertyChangeEvent event) {
        if(event.getPropertyName().equals(CONTACTS_STORAGE_PROP)){
            updateLists();
        }
    }
}
