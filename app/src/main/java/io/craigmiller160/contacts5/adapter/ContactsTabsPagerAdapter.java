package io.craigmiller160.contacts5.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.craigmiller160.contacts5.fragment.AbstractContactsPageFragment;
import io.craigmiller160.utils.collection.SortedList;

/**
 * Created by Craig on 1/24/2016.
 */
public class ContactsTabsPagerAdapter extends FragmentPagerAdapter implements Serializable {

    private final List<AbstractContactsPageFragment<?>> pages = new SortedList<>();

    public ContactsTabsPagerAdapter(FragmentManager fm){
        super(fm);
    }

    public void addPage(AbstractContactsPageFragment<?> page){
        pages.add(page);
        notifyDataSetChanged();
    }

    public void removePage(AbstractContactsPageFragment<?> page){
        pages.remove(page);
        notifyDataSetChanged();
    }

    public void removePage(int position){
        pages.remove(position);
        notifyDataSetChanged();
    }

    public void removeAllPages(){
        pages.clear();
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
        return pages.get(position).getPageTitle();
    }

}
