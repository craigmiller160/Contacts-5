package io.craigmiller160.contacts5.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.io.Serializable;
/**
 * Created by Craig on 1/24/2016.
 */
public class ContactsTabsPagerAdapter extends FragmentStatePagerAdapter implements Serializable {

    public static final String FRAGMENT_TAG_PREFIX = "android:switcher:";

    private static final String CONTACTS_TAB_NAME = "All Contacts";
    private static final String GROUPS_TAB_NAME = "Groups";

    private Fragment contactsFragment;
    private Fragment groupsFragment;

    public ContactsTabsPagerAdapter(FragmentManager fm){
        this(fm, null, null);
    }

    public ContactsTabsPagerAdapter(FragmentManager fm, Fragment contactsFragment, Fragment groupsFragment) {
        super(fm);
        this.contactsFragment = contactsFragment;
        this.groupsFragment = groupsFragment;
    }

    public void setContactsFragment(Fragment contactsFragment){
        this.contactsFragment = contactsFragment;
    }

    public void setGroupsFragment(Fragment groupsFragment){
        this.groupsFragment = groupsFragment;
    }

    @Override
    public Fragment getItem(int position) {
        return position == 0 ? contactsFragment : groupsFragment;
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
