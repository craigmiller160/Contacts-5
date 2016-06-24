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

    public FragmentChanger(Context context) {
        super(context);
    }

    public void displayTabsFragment(FragmentManager fm){
        Log.d(TAG, "Displaying TabsFragment");
        displayFragment(fm, R.id.tabs_fragment_container, TabsFragment.class,
                getString(R.string.tag_tabs_fragment), new String[]{getString(R.string.tag_no_tabs_fragment)});
    }

    private void displayFragment(FragmentManager fm, int displayContainerId, Class<? extends Fragment> displayFragmentType,
                                        String displayFragmentTag, String[] fragmentsToRemoveTags){
        AndroidModel contactsModel = ContactsApp.getApp().modelFactory().getModel(R.string.model_contacts);
        FragmentTransaction transaction = fm.beginTransaction();

        for(String tag : fragmentsToRemoveTags){
            Fragment fragment = fm.findFragmentByTag(tag);
            if(fragment != null){
                transaction.remove(fragment);
            }
        }

        transaction.replace(displayContainerId, ObjectCreator.instantiateClass(displayFragmentType), displayFragmentTag);
        transaction.commit();
        contactsModel.setProperty(R.string.prop_displayed_fragment, displayFragmentTag);
    }

    public void displayNoTabsFragment(FragmentManager fm){
        Log.d(TAG, "Displaying NoTabsFragment");
        displayFragment(fm, R.id.no_tabs_fragment_container, ContactsInGroupFragment.class,
                getString(R.string.tag_no_tabs_fragment), new String[]{getString(R.string.tag_tabs_fragment)});
    }

}
