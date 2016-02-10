package io.craigmiller160.contacts5.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Craig on 1/24/2016.
 */
public class TabsPagerAdapter extends FragmentPagerAdapter {

    private final List<Fragment> fragmentPages = new ArrayList<>();
    private final List<String> fragmentPageTitles = new ArrayList<>();

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public boolean addFragmentPage(Fragment fragmentPage, String pageTitle){
        fragmentPages.add(fragmentPage);
        fragmentPageTitles.add(pageTitle);
        notifyDataSetChanged();
        return true;
    }

    public boolean setFragmentPage(Fragment fragmentPage, String pageTitle){
        int index = fragmentPageTitles.indexOf(pageTitle);
        if(index >= 0){
            fragmentPages.set(index, fragmentPage);
            notifyDataSetChanged();
            return true;
        }
        return false;
    }

    public void removeAllPages(){
        fragmentPages.clear();
        fragmentPageTitles.clear();
        notifyDataSetChanged();
    }

    public boolean removeFragmentPage(String pageTitle){
        int index = fragmentPageTitles.indexOf(pageTitle);
        return removeFragmentPage(index);
    }

    public boolean removeFragmentPage(int index){
        if(index >= 0 && index < fragmentPageTitles.size()){
            fragmentPageTitles.remove(index);
            fragmentPages.remove(index);
            notifyDataSetChanged();
            return true;
        }
        return false;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentPages.get(position);
    }

    @Override
    public int getCount() {
        return fragmentPages.size();
    }

    @Override
    public CharSequence getPageTitle(int position){
        return fragmentPageTitles.get(position);
    }
}
