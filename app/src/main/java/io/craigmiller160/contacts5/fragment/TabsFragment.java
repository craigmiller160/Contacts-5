package io.craigmiller160.contacts5.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import io.craigmiller160.contacts5.ContactsApp;
import io.craigmiller160.contacts5.R;
import io.craigmiller160.contacts5.adapter.ContactsTabsPagerAdapter;
import io.craigmiller160.contacts5.service.ContactsRetrievalService;

/**
 * Created by craig on 6/5/16.
 */
public class TabsFragment extends Fragment {

    private Fragment contactsFragment;
    private Fragment groupsFragment;
    private Fragment favoritesFragment;
    private ContactsRetrievalService contactsService;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        contactsService = ContactsApp.getApp().serviceFactory().getContactsRetrievalService();

        //Get the existing instances of the fragments, if they exist
        if (savedInstance != null) {
            favoritesFragment = getFragmentManager().findFragmentByTag("android:switcher:" + R.id.contactsTabsViewPager + ":" + 0);
            contactsFragment = getFragmentManager().findFragmentByTag("android:switcher:" + R.id.contactsTabsViewPager + ":" + 1);
            groupsFragment = getFragmentManager().findFragmentByTag("android:switcher:" + R.id.contactsTabsViewPager + ":" + 2);
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

        contactsService.loadAllContacts();
        contactsService.loadAllGroups();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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

        tabsAdapter.addPage((AbstractContactsPageFragment<?>) favoritesFragment);
        tabsAdapter.addPage((AbstractContactsPageFragment<?>) contactsFragment);
        tabsAdapter.addPage((AbstractContactsPageFragment<?>) groupsFragment);

        tabLayout.setupWithViewPager(viewPager);

        //If it's the first time this fragment is being displayed, set the default tab
        if(setDefaultTab){
            viewPager.setCurrentItem(1);
        }

        return tabLayout;
    }

}
