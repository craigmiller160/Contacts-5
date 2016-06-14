package io.craigmiller160.contacts5.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import io.craigmiller160.contacts5.ContactsApp;
import io.craigmiller160.contacts5.R;
import io.craigmiller160.contacts5.model.AndroidModel;
import io.craigmiller160.utils.reflect.ObjectCreator;

import static io.craigmiller160.contacts5.util.ContactsConstants.*;

/**
 * Created by craig on 6/12/16.
 */
public class FragmentChanger {

    private static final String TAG = "FragmentChanger";

    public static void displayTabsFragment(FragmentManager fm){
        Log.d(TAG, "Displaying TabsFragment");
        displayFragment(fm, R.id.tabs_fragment_container, TabsFragment.class,
                TABS_FRAGMENT_TAG, new String[]{NO_TABS_FRAGMENT_TAG});
    }

    private static void displayFragment(FragmentManager fm, int displayContainerId, Class<? extends Fragment> displayFragmentType,
                                        String displayFragmentTag, String[] fragmentsToRemoveTags){
        AndroidModel contactsModel = ContactsApp.getApp().modelFactory().getModel(CONTACTS_MODEL);
        FragmentTransaction transaction = fm.beginTransaction();

        for(String tag : fragmentsToRemoveTags){
            Fragment fragment = fm.findFragmentByTag(tag);
            if(fragment != null){
                transaction.remove(fragment);
            }
        }

        transaction.replace(displayContainerId, ObjectCreator.instantiateClass(displayFragmentType), displayFragmentTag);
        transaction.commit();
        contactsModel.setProperty(DISPLAYED_FRAGMENT, displayFragmentTag);
    }

    public static void displayNoTabsFragment(FragmentManager fm){
        Log.d(TAG, "Displaying NoTabsFragment");
        displayFragment(fm, R.id.no_tabs_fragment_container, ContactsInGroupFragment.class,
                NO_TABS_FRAGMENT_TAG, new String[]{TABS_FRAGMENT_TAG});
    }

}
