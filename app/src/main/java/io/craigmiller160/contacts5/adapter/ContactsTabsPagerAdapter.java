package io.craigmiller160.contacts5.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.craigmiller160.contacts5.fragment.AbstractContactsPageFragment;

/**
 * Created by Craig on 1/24/2016.
 */
public class ContactsTabsPagerAdapter extends FragmentStatePagerAdapter implements Serializable {

    public static final String FRAGMENT_TAG_PREFIX = "android:switcher:";

    private static final String CONTACTS_TAB_NAME = "All Contacts";
    private static final String GROUPS_TAB_NAME = "Groups";

    private Fragment contactsFragment;
    private Fragment groupsFragment;

    private final List<Fragment> pages = new ArrayList<>();
    private final List<String> pageTitles = new ArrayList<>();

    public ContactsTabsPagerAdapter(FragmentManager fm){
//        this(fm, null, null);
        super(fm);
    }

//    public ContactsTabsPagerAdapter(FragmentManager fm, Fragment contactsFragment, Fragment groupsFragment) {
//        super(fm);
//        this.contactsFragment = contactsFragment;
//        this.groupsFragment = groupsFragment;
//    }

//    public void setContactsFragment(Fragment contactsFragment){
//        this.contactsFragment = contactsFragment;
//    }
//
//    public void setGroupsFragment(Fragment groupsFragment){
//        this.groupsFragment = groupsFragment;
//    }

    public void addPage(AbstractContactsPageFragment<?> page){
        pages.add(page);
        pageTitles.add(page.getPageTitle());
        notifyDataSetChanged();
    }

    public void replacePage(AbstractContactsPageFragment<?> page, int position){
        pages.set(position, page);
        pageTitles.set(position, page.getPageTitle());
        notifyDataSetChanged();
    }

    public void removePage(AbstractContactsPageFragment<?> page){
        pages.remove(page);
        pageTitles.remove(page.getPageTitle());
        notifyDataSetChanged();
    }

    public void removePage(int position){
        pages.remove(position);
        pageTitles.remove(position);
        notifyDataSetChanged();
    }

    public void removeAllPages(){
        pages.clear();
        pageTitles.clear();
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
//        return position == 0 ? contactsFragment : groupsFragment;
        return pages.get(position);
    }

    @Override
    public int getCount() {
//        return 2;
        return pages.size();
    }

    @Override
    public CharSequence getPageTitle(int position){
//        return position == 0 ? CONTACTS_TAB_NAME : GROUPS_TAB_NAME;
        return pageTitles.get(position);
    }

}
