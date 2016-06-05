package io.craigmiller160.contacts5.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import io.craigmiller160.contacts5.R;
import io.craigmiller160.contacts5.adapter.ContactsTabsPagerAdapter;
import io.craigmiller160.contacts5.service.ContactsRetrievalService;

/**
 * Created by craig on 6/4/16.
 */
public class TabsFragment extends Fragment {

    private AllContactsPage allContactsPage;
    private AllGroupsPage allGroupsPage;

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        //Get the existing instances of the fragments, if they exist
        if(savedInstance != null){
            Fragment oldContactsPage = getFragmentManager().findFragmentByTag("android:switcher:" + R.id.contactsTabsViewPager + ":" + 0);
            if(oldContactsPage != null && oldContactsPage instanceof AllContactsPage){
                allContactsPage = (AllContactsPage) oldContactsPage;
            }
            else{
                allContactsPage = new AllContactsPage();
            }

            Fragment oldGroupsPage = getFragmentManager().findFragmentByTag("android:switcher:" + R.id.contactsTabsViewPager + ":" + 1);
            if(oldGroupsPage != null && oldGroupsPage instanceof AllGroupsPage){
                allGroupsPage = (AllGroupsPage) oldGroupsPage;
            }
            else{
                allGroupsPage = new AllGroupsPage();
            }
        }
        else{
            allContactsPage = new AllContactsPage();
            allGroupsPage = new AllGroupsPage();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance){
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.tabs_layout, container, false);
        TabLayout tabLayout = (TabLayout) layout.findViewById(R.id.contacts_tabs);
        ViewPager viewPager = (ViewPager) layout.findViewById(R.id.contactsTabsViewPager);

        ContactsTabsPagerAdapter tabsAdapter = new ContactsTabsPagerAdapter(getFragmentManager(), allContactsPage, allGroupsPage);
        viewPager.setAdapter(tabsAdapter);
        tabLayout.setupWithViewPager(viewPager);

        return layout;
    }

}
