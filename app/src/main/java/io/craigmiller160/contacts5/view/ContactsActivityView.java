package io.craigmiller160.contacts5.view;

import android.app.Activity;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import io.craigmiller160.contacts5.R;
import io.craigmiller160.contacts5.service.PermissionsManager;
import io.craigmiller160.locus.Locus;

import static io.craigmiller160.contacts5.util.ContactsConstants.ADD_CONTACT_CONTROLLER;

/**
 * Created by craig on 5/4/16.
 */
public class ContactsActivityView extends AndroidActivityView {

    public ContactsActivityView(Activity activity) {
        super(activity);
    }

    @Override
    protected void onCreate() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.contactsActivityToolbar);
        getAppCompatActivity().setSupportActionBar(toolbar);

        findViewById(R.id.addContact).setOnClickListener(
                Locus.controller.getController(
                        ADD_CONTACT_CONTROLLER, View.OnClickListener.class, getActivity())
        );
    }

    private void configureTabs(){
        ViewPager viewPager = (ViewPager) findViewById(R.id.contactsTabsViewPager);
        //TODO review and restore this code
//        tabsAdapter = new TabsPagerAdapter(getSupportFragmentManager());
//
//        allContactsPage = new AllContactsPage();
//        contactGroupsPage = new ContactsGroupsPage();
//
//        tabsAdapter.addFragmentPage(allContactsPage, AllContactsPage.TITLE);
//        tabsAdapter.addFragmentPage(contactGroupsPage, ContactsGroupsPage.TITLE);
//        viewPager.setAdapter(tabsAdapter);
//        TabLayout tabLayout = (TabLayout) findViewById(R.id.contactsActivityTabs);
//        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    protected int getViewResourceId() {
        return R.layout.activity_contacts;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.grantPermissions);
        if(item != null){
            item.setVisible(!PermissionsManager.hasReadContactsPermission(getActivity()));
        }

        return true;
    }
}
