package io.craigmiller160.contacts5.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.io.Serializable;

import io.craigmiller160.contacts5.fragment.AllContactsPage;
import io.craigmiller160.contacts5.fragment.AllGroupsPage;

/**
 * Created by Craig on 1/24/2016.
 */
public class ContactsTabsPagerAdapter extends FragmentPagerAdapter implements Serializable {

    public static final String FRAGMENT_TAG_PREFIX = "android:switcher:";

    private static final String CONTACTS_TAB_NAME = "All Contacts";
    private static final String GROUPS_TAB_NAME = "Groups";

    private AllContactsPage contactsPage;
    private AllGroupsPage groupsPage;


    public ContactsTabsPagerAdapter(FragmentManager fm, AllContactsPage allContactsPage, AllGroupsPage allGroupsPage) {
        super(fm);
        this.contactsPage = allContactsPage;
        this.groupsPage = allGroupsPage;
    }

    @Override
    public Fragment getItem(int position) {
        System.out.println("POSITION: " + position); //TODO delete this
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
