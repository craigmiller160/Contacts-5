package io.craigmiller160.contacts5.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import io.craigmiller160.contacts5.fragment.AllContactsPage;
import io.craigmiller160.contacts5.fragment.AllGroupsPage;
import io.craigmiller160.contacts5.model.Contact;
import io.craigmiller160.contacts5.model.ContactGroup;
import io.craigmiller160.contacts5.model.ContactsDataCallback;
import io.craigmiller160.contacts5.service.ContactsRetrievalService;
import io.craigmiller160.contacts5.service.PermissionsService;
import io.craigmiller160.contacts5.service.ServiceFactory;

/**
 * Created by Craig on 1/24/2016.
 */
public class ContactsTabsPagerAdapter extends FragmentPagerAdapter implements ContactsDataCallback {

    private static final String CONTACTS_TAB_NAME = "All Contacts";
    private static final String GROUPS_TAB_NAME = "Groups";

    private PermissionsService permissionsService;
    private ContactsRetrievalService contactsService;

    private AllContactsPage contactsPage;
    private AllGroupsPage groupsPage;


    public ContactsTabsPagerAdapter(FragmentManager fm) {
        super(fm);
        permissionsService = ServiceFactory.getInstance().getPermissionsService();
        contactsService = ServiceFactory.getInstance().getContactsRetrievalService();
        contactsPage = new AllContactsPage();
        groupsPage = new AllGroupsPage();

        loadContacts();
    }

    public void loadContacts(){
        if(permissionsService.hasReadContactsPermission()){
            contactsService.loadAllContacts(this);
        }
    }

    @Override
    public void setContactsList(final List<Contact> contacts){
        if(contactsPage == null){
            throw new IllegalArgumentException("Cannot set the contacts list before setting the contacts page to display it");
        }

        contactsPage.setContactsList(contacts);
    }

    @Override
    public void setGroupsList(List<ContactGroup> groups){
        if(contactsPage == null){
            throw new IllegalArgumentException("Cannot set the groups list before setting the groups page to display it");
        }

        groupsPage.setGroupsList(groups);
    }

    @Override
    public Fragment getItem(int position) {
        return position == 0 ? contactsPage : groupsPage;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position){
        return position == 0 ? CONTACTS_TAB_NAME : GROUPS_TAB_NAME;
    }
}
