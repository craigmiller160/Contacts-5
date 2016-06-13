package io.craigmiller160.contacts5.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.craigmiller160.contacts5.ContactsApp;
import io.craigmiller160.contacts5.R;
import io.craigmiller160.contacts5.adapter.ContactsTabsPagerAdapter;
import io.craigmiller160.contacts5.model.AndroidModel;
import io.craigmiller160.contacts5.service.ContactsRetrievalService;
import io.craigmiller160.contacts5.service.PermissionsService;

import static io.craigmiller160.contacts5.util.ContactsConstants.*;

/**
 * Created by craig on 6/5/16.
 */
public class TabsFragment extends Fragment {

    private Fragment contactsFragment;
    private Fragment groupsFragment;
    private Fragment favoritesFragment;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        ContactsRetrievalService contactsService = ContactsApp.getApp().serviceFactory().getContactsRetrievalService();
        PermissionsService permissionsService = ContactsApp.getApp().serviceFactory().getPermissionsService();

        //Get the existing instances of the fragments, if they exist
        if (savedInstance != null) {
            favoritesFragment = getFragmentManager().findFragmentByTag(getFragmentPageTag(R.id.contactsTabsViewPager, FavContactsFragment.PAGE_INDEX));
            contactsFragment = getFragmentManager().findFragmentByTag(getFragmentPageTag(R.id.contactsTabsViewPager, AllContactsFragment.PAGE_INDEX));
            groupsFragment = getFragmentManager().findFragmentByTag(getFragmentPageTag(R.id.contactsTabsViewPager, AllGroupsFragment.PAGE_INDEX));
        }

        if(favoritesFragment == null){
            favoritesFragment = new FavContactsFragment();
        }

        if(contactsFragment == null){
            contactsFragment = new AllContactsFragment();
        }

        if(groupsFragment == null){
            groupsFragment = new AllGroupsFragment();
        }

        AndroidModel contactsModel = ContactsApp.getApp().modelFactory().getModel(CONTACTS_MODEL);
        contactsModel.clearProperty(SELECTED_GROUP_ID);
        contactsModel.clearProperty(SELECTED_GROUP_NAME);

        if(permissionsService.hasReadContactsPermission()){
            contactsService.loadAllContacts();
            contactsService.loadAllGroups();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getActivity().setTitle(getString(R.string.contacts_activity_name));

        TabLayout tabLayout = (TabLayout) inflater.inflate(R.layout.tab_layout, container, false);
        ViewPager viewPager = (ViewPager) getActivity().findViewById(R.id.contactsTabsViewPager);
        viewPager.setVisibility(View.VISIBLE);

        boolean setDefaultTab = false;

        ContactsTabsPagerAdapter tabsAdapter = null;
        if(viewPager.getAdapter() != null){
            tabsAdapter = (ContactsTabsPagerAdapter) viewPager.getAdapter();
            tabsAdapter.removeAllPages();
            setDefaultTab = false;
        }
        else{
            tabsAdapter = new ContactsTabsPagerAdapter(getFragmentManager());
            viewPager.setAdapter(tabsAdapter);
            setDefaultTab = true;
        }

        tabsAdapter.addPage((AbstractContactsPageFragment<?>) groupsFragment);
        tabsAdapter.addPage((AbstractContactsPageFragment<?>) favoritesFragment);
        tabsAdapter.addPage((AbstractContactsPageFragment<?>) contactsFragment);

        tabLayout.setupWithViewPager(viewPager);

        //If it's the first time this fragment is being displayed, set the default tab
        if(setDefaultTab){
            viewPager.setCurrentItem(1);
        }

        return tabLayout;
    }

}
