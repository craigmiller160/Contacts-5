package io.craigmiller160.contacts5.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.craigmiller160.contacts5.ContactsApp;
import io.craigmiller160.contacts5.R;
import io.craigmiller160.contacts5.adapter.ContactsTabsPagerAdapter;
import io.craigmiller160.contacts5.model.AndroidModel;

import static io.craigmiller160.contacts5.util.ContactsConstants.getFragmentPageTag;

/**
 * Created by craig on 6/5/16.
 */
public class TabsFragment extends Fragment {

    private static final String TAG = "TabsFragment";

    private Fragment contactsFragment;
    private Fragment groupsFragment;
    private Fragment favoritesFragment;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        Log.v(TAG, "TabsFragment created");

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

        AndroidModel contactsModel = ContactsApp.getApp().modelFactory().getModel(R.string.model_contacts);
        contactsModel.clearProperty(R.string.prop_selected_group_id);
        contactsModel.clearProperty(R.string.prop_selected_group_name);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        TabLayout tabLayout = (TabLayout) inflater.inflate(R.layout.tab_layout, container, false);
        ViewPager viewPager = (ViewPager) getActivity().findViewById(R.id.contactsTabsViewPager);

        boolean setDefaultTab = false;

        ContactsTabsPagerAdapter tabsAdapter = null;
        if(viewPager.getAdapter() != null){
            tabsAdapter = (ContactsTabsPagerAdapter) viewPager.getAdapter();
            tabsAdapter.removeAllPages();
            tabLayout.removeAllTabs();
            setDefaultTab = false;
        }
        else{
            tabsAdapter = new ContactsTabsPagerAdapter(getFragmentManager(), getActivity());
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

        setViewPagerViewVisibility(View.VISIBLE);

        return tabLayout;
    }

    private void setViewPagerViewVisibility(int visibility){
        ViewPager viewPager = (ViewPager) getActivity().findViewById(R.id.contactsTabsViewPager);
        viewPager.setVisibility(visibility);
        int childCount = viewPager.getChildCount();
        for(int i = 0; i < childCount; i++){
            View view = viewPager.getChildAt(i);
            view.setVisibility(visibility);
        }
    }

    @Override
    public void onDestroyView() {
        setViewPagerViewVisibility(View.GONE);
        super.onDestroyView();
    }
}
