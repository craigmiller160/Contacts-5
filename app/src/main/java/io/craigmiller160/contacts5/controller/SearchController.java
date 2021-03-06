/*
 * Copyright 2016 ShadowAngler <craigmiller160@gmail.com> - All Rights Reserved
 * Proprietary / Confidential
 * Unauthorized copying, use, or redistribution of this file is prohibited
 */

package io.craigmiller160.contacts5.controller;

import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.MenuItem;

import java.util.HashMap;
import java.util.Map;

import io.craigmiller160.contacts5.ContactsApp;
import io.craigmiller160.contacts5.R;
import io.craigmiller160.contacts5.adapter.ContactsArrayAdapter;
import io.craigmiller160.contacts5.adapter.MyArrayAdapter;
import io.craigmiller160.contacts5.fragment.AbstractContactsFragment;
import io.craigmiller160.contacts5.fragment.FragmentChanger;
import io.craigmiller160.contacts5.log.Logger;
import io.craigmiller160.contacts5.model.AndroidModel;
import io.craigmiller160.contacts5.model.Contact;

/**
 * Created by craig on 6/25/16.
 */
public class SearchController extends AbstractAndroidController implements SearchView.OnQueryTextListener, MenuItemCompat.OnActionExpandListener{

    private static final String TAG = "SearchController";
    private static final Logger logger = Logger.newLogger(TAG);

    private final FragmentChanger fragmentChanger;
    private final AndroidModel contactsModel;
    private MyArrayAdapter<Contact> adapter;
    private boolean isSearchOpen = false;
    private String previousDisplayedFragment;

    public SearchController(AppCompatActivity activity) {
        this(activity, new HashMap<String, Object>());
    }

    public SearchController(AppCompatActivity activity, Map<String,Object> args){
        super(activity, args);
        this.fragmentChanger = new FragmentChanger(activity);
        this.contactsModel = ContactsApp.getApp().modelFactory().getModel(R.string.model_contacts);
    }

    public AppCompatActivity getActivity(){
        return (AppCompatActivity) getContext();
    }

    private void findAdapter(){
        String fragmentTag = "";
        String displayedFragment = contactsModel.getProperty(R.string.prop_displayed_fragment, String.class);
        if(getString(R.string.tag_tabs_fragment).equals(displayedFragment)){
            fragmentTag = getString(R.string.tag_search_fragment);
        }
        else{
            fragmentTag = getString(R.string.tag_no_tabs_fragment);
        }
        AbstractContactsFragment<Contact> fragment = (AbstractContactsFragment<Contact>) getActivity()
                .getSupportFragmentManager().findFragmentByTag(fragmentTag);
        if(fragment != null){
            adapter = (MyArrayAdapter<Contact>) fragment.getArrayAdapter();
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        if(isSearchOpen){
            adapter.filter(query);
        }

        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        logger.d(TAG, "Closing SearchView");
        isSearchOpen = false;
        if(adapter != null){
            adapter.clearFilter();
            adapter = null;
        }

        contactsModel.setProperty(R.string.prop_displayed_fragment, previousDisplayedFragment);
        getActivity().getSupportFragmentManager().popBackStack();

        return true;
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        logger.d(TAG, "Opening SearchView");
        isSearchOpen = true;

        previousDisplayedFragment = contactsModel.getProperty(R.string.prop_displayed_fragment, String.class);
        contactsModel.setProperty(R.string.prop_displayed_fragment, getString(R.string.tag_search_fragment));

        adapter = new ContactsArrayAdapter(getContext(), getAdapterProperty());
        fragmentChanger.addSearchFragment(getActivity().getSupportFragmentManager(), adapter);

        return true;
    }

    private String getAdapterProperty(){
        if(previousDisplayedFragment.equals(getString(R.string.tag_tabs_fragment))){
            return getString(R.string.prop_contacts_list);
        }
        return getString(R.string.prop_contacts_in_group_list);
    }
}
