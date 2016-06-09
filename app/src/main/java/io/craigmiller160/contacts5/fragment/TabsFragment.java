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
    private ContactsRetrievalService contactsService;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        contactsService = ContactsApp.getApp().serviceFactory().getContactsRetrievalService();

        //Get the existing instances of the fragments, if they exist
        if (savedInstance != null) {
            contactsFragment = getFragmentManager().findFragmentByTag("android:switcher:" + R.id.contactsTabsViewPager + ":" + 0);
            groupsFragment = getFragmentManager().findFragmentByTag("android:switcher:" + R.id.contactsTabsViewPager + ":" + 1);
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

//        CoordinatorLayout rootView = (CoordinatorLayout) ((ViewGroup) getActivity().findViewById(android.R.id.content)).getChildAt(0);
//        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.view_pager_layout, rootView, true);

//        ViewPager viewPager = (ViewPager) viewGroup.findViewById(R.id.contactsTabsViewPager);


//        rootView.removeView(viewPager);
//        rootView.addView(viewPager, rootView.getChildCount() - 1);





        ViewPager viewPager = (ViewPager) getActivity().findViewById(R.id.contactsTabsViewPager);

        ContactsTabsPagerAdapter tabsAdapter = new ContactsTabsPagerAdapter(getFragmentManager(), contactsFragment, groupsFragment);
        viewPager.setAdapter(tabsAdapter);
        tabLayout.setupWithViewPager(viewPager);

        return tabLayout;
    }

//    @Override
//    public void onActivityCreated(Bundle savedInstance){
//        super.onActivityCreated(savedInstance);
//        AppCompatActivity activity = (AppCompatActivity) getActivity();
//        activity.setTitle(getString(R.string.contacts_activity_name));
//        activity.getSupportActionBar().setHomeButtonEnabled(false);
//        TabLayout tabLayout = (TabLayout) activity.findViewById(R.id.contactsActivityTabs);
//        tabLayout.setVisibility(View.VISIBLE);
//        ViewPager viewPager = (ViewPager) activity.findViewById(R.id.contactsTabsViewPager);
//
//        if(viewPager.getAdapter() != null && (viewPager.getAdapter() instanceof ContactsTabsPagerAdapter)){
//            ContactsTabsPagerAdapter tabsAdapter = (ContactsTabsPagerAdapter) viewPager.getAdapter();
//            tabsAdapter.setContactsFragment(contactsFragment);
//            tabsAdapter.setGroupsFragment(groupsFragment);
//        }
//        else{
//            ContactsTabsPagerAdapter tabsAdapter = new ContactsTabsPagerAdapter(activity.getSupportFragmentManager(), contactsFragment, groupsFragment);
//            viewPager.setAdapter(tabsAdapter);
//            tabLayout.setupWithViewPager(viewPager);
//        }
//
//
//    }

//    public void removeFragments(){
//        getFragmentManager().beginTransaction()
//                .remove(contactsFragment)
//                .remove(groupsFragment)
//                .commit();
//    }

}
