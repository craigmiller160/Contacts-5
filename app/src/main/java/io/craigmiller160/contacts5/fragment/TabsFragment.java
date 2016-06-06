package io.craigmiller160.contacts5.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import io.craigmiller160.contacts5.R;
import io.craigmiller160.contacts5.adapter.ContactsTabsPagerAdapter;
import io.craigmiller160.contacts5.service.ContactsRetrievalService;
import io.craigmiller160.contacts5.service.ServiceFactory;

/**
 * Created by craig on 6/5/16.
 */
public class TabsFragment extends Fragment {

    private AllContactsPage allContactsPage;
    private AllGroupsPage allGroupsPage;
    private ContactsRetrievalService contactsService;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        contactsService = ServiceFactory.getInstance().getContactsRetrievalService();

        //Get the existing instances of the fragments, if they exist
        if (savedInstance != null) {
            Fragment oldContactsPage = getFragmentManager().findFragmentByTag("android:switcher:" + R.id.contactsTabsViewPager + ":" + 0);
            if (oldContactsPage != null && oldContactsPage instanceof AllContactsPage) {
                allContactsPage = (AllContactsPage) oldContactsPage;
            } else {
                allContactsPage = new AllContactsPage();
            }

            Fragment oldGroupsPage = getFragmentManager().findFragmentByTag("android:switcher:" + R.id.contactsTabsViewPager + ":" + 1);
            if (oldGroupsPage != null && oldGroupsPage instanceof AllGroupsPage) {
                allGroupsPage = (AllGroupsPage) oldGroupsPage;
            } else {
                allGroupsPage = new AllGroupsPage();
            }
        } else {
            allContactsPage = new AllContactsPage();
            allGroupsPage = new AllGroupsPage();
        }

        contactsService.loadAllContacts(null);
        contactsService.loadAllGroups(null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstance){
        super.onActivityCreated(savedInstance);
        getActivity().setTitle(getString(R.string.contacts_activity_name));
        TabLayout tabLayout = (TabLayout) getActivity().findViewById(R.id.contactsActivityTabs);
        tabLayout.setVisibility(View.VISIBLE);
        ViewPager viewPager = (ViewPager) getActivity().findViewById(R.id.contactsTabsViewPager);

        ContactsTabsPagerAdapter tabsAdapter = new ContactsTabsPagerAdapter(getFragmentManager(), allContactsPage, allGroupsPage);
        viewPager.setAdapter(tabsAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

}
