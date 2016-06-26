package io.craigmiller160.contacts5.fragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import io.craigmiller160.contacts5.ContactsApp;
import io.craigmiller160.contacts5.R;
import io.craigmiller160.contacts5.model.AndroidModel;
import io.craigmiller160.contacts5.util.AbstractAndroidUtil;
import io.craigmiller160.utils.reflect.ObjectCreator;

/**
 * Created by craig on 6/12/16.
 */
public class FragmentChanger extends AbstractAndroidUtil{

    private static final String TAG = "FragmentChanger";

    private AndroidModel contactsModel;

    public FragmentChanger(Context context) {
        super(context);
        this.contactsModel = ContactsApp.getApp().modelFactory().getModel(R.string.model_contacts);
    }

    public void displayTabsFragment(FragmentManager fm){
        Log.d(TAG, "Displaying TabsFragment");
        String displayedFragment = contactsModel.getProperty(R.string.prop_displayed_fragment, String.class);
        displayFragment(fm, R.id.tabs_fragment_container, TabsFragment.class,
                getString(R.string.tag_tabs_fragment), new String[]{displayedFragment}, true);
    }

    public void displayFragment(FragmentManager fm, int displayContainerId, Class<? extends Fragment> displayFragmentType,
                                        String displayFragmentTag, String[] fragmentsToRemoveTags, boolean changeProp){
        AndroidModel contactsModel = ContactsApp.getApp().modelFactory().getModel(R.string.model_contacts);
        FragmentTransaction transaction = fm.beginTransaction();

        for(String tag : fragmentsToRemoveTags){
            Fragment fragment = fm.findFragmentByTag(tag);
            if(fragment != null){
                transaction.remove(fragment);
            }
        }

        transaction.commit();
        transaction = fm.beginTransaction();

        transaction.replace(displayContainerId, ObjectCreator.instantiateClass(displayFragmentType), displayFragmentTag);
        transaction.commit();
        if(changeProp){
            contactsModel.setProperty(R.string.prop_displayed_fragment, displayFragmentTag);
        }
    }

    public void displayNoTabsFragment(FragmentManager fm){
        Log.d(TAG, "Displaying NoTabsFragment");
        String displayedFragment = contactsModel.getProperty(R.string.prop_displayed_fragment, String.class);
        displayFragment(fm, R.id.no_tabs_fragment_container, ContactsInGroupFragment.class,
                getString(R.string.tag_no_tabs_fragment), new String[]{displayedFragment}, true);
    }

    public void displaySearchFragment(FragmentManager fm) {
        Log.d(TAG, "Displaying SearchFragment");
        String displayedFragment = contactsModel.getProperty(R.string.prop_displayed_fragment, String.class);
        displayFragment(fm, R.id.no_tabs_fragment_container, SearchFragment.class,
                getString(R.string.tag_search_fragment), new String[]{displayedFragment}, false);
    }

}
