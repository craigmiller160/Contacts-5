package io.craigmiller160.contacts5.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import io.craigmiller160.contacts5.fragment.AbstractContactsPageFragment;
import io.craigmiller160.utils.collection.SortedList;

/**
 * Created by Craig on 1/24/2016.
 */
public class ContactsTabsPagerAdapter extends FragmentPagerAdapter implements Serializable {

    private final List<AbstractContactsPageFragment<?>> pages;
    private final List<MutablePair<Integer,String>> pageTitles;
    private final Context context;

    public ContactsTabsPagerAdapter(FragmentManager fm, Context context){
        super(fm);
        this.pages = new SortedList<>();
        this.pageTitles = new SortedList<>(new FragmentPageTitleComparator());
        this.context = context;
    }

    public void addPage(AbstractContactsPageFragment<?> page){
        pages.add(page);
        String pageTitle = context.getString(page.getPageTitleResId());
        pageTitles.add(new MutablePair<>(page.getPageIndex(), pageTitle));
        notifyDataSetChanged();
    }

    public void removePage(AbstractContactsPageFragment<?> page){
        pages.remove(page);
        pageTitles.remove(page.getPageIndex());
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
        return pageTitles.get(position).getRight();
    }

    private static class FragmentPageTitleComparator implements Comparator<MutablePair<Integer,String>>{

        @Override
        public int compare(MutablePair<Integer, String> o1, MutablePair<Integer, String> o2) {
            return o1.getLeft().compareTo(o2.getLeft());
        }
    }

}
