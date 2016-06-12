package io.craigmiller160.contacts5.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.craigmiller160.contacts5.fragment.AbstractContactsPageFragment;

/**
 * Created by Craig on 1/24/2016.
 */
public class ContactsTabsPagerAdapter extends FragmentPagerAdapter implements Serializable {

    public static final String FRAGMENT_TAG_PREFIX = "android:switcher:";

    private final List<Fragment> pages = new ArrayList<>();
    private final List<String> pageTitles = new ArrayList<>();

    public ContactsTabsPagerAdapter(FragmentManager fm){
        super(fm);
    }

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
        return pages.get(position);
    }

    @Override
    public int getCount() {
        return pages.size();
    }

    @Override
    public CharSequence getPageTitle(int position){
        return pageTitles.get(position);
    }

}
