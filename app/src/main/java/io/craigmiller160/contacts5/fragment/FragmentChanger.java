package io.craigmiller160.contacts5.fragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import org.apache.commons.lang3.StringUtils;

import io.craigmiller160.contacts5.ContactsApp;
import io.craigmiller160.contacts5.R;
import io.craigmiller160.contacts5.log.Logger;
import io.craigmiller160.contacts5.model.AndroidModel;
import io.craigmiller160.contacts5.util.AbstractAndroidUtil;
import io.craigmiller160.utils.reflect.ObjectCreator;

/**
 * Created by craig on 6/12/16.
 */
public class FragmentChanger extends AbstractAndroidUtil{

    private static final String TAG = "FragmentChanger";
    private static final Logger logger = Logger.newLogger(TAG);

    private AndroidModel contactsModel;

    public FragmentChanger(Context context) {
        super(context);
        this.contactsModel = ContactsApp.getApp().modelFactory().getModel(R.string.model_contacts);
    }

    public void addFragment(FragmentManager fm, int displayContainerId,
                            Class<? extends Fragment> displayFragmentType, String displayFragmentTag){
        Fragment fragment = ObjectCreator.instantiateClass(displayFragmentType);
        addFragment(fm, displayContainerId, fragment, displayFragmentTag);
    }

    public void addFragment(FragmentManager fm, int displayContainerId,
                            Fragment displayFragment, String displayFragmentTag){
        logger.d(TAG, "Adding Fragment: " + displayFragmentTag);
        contactsModel.setProperty(R.string.prop_displayed_fragment, displayFragmentTag);
        fm.beginTransaction()
                .replace(displayContainerId, displayFragment, displayFragmentTag)
                .addToBackStack(null)
                .commit();
    }

    public void addTabsFragment(FragmentManager fm){
        addFragment(fm, R.id.tabs_fragment_container, TabsFragment.class, getString(R.string.tag_tabs_fragment));
    }

    public void addNoTabsFragment(FragmentManager fm){
        addFragment(fm, R.id.no_tabs_fragment_container, ContactsInGroupFragment.class, getString(R.string.tag_no_tabs_fragment));
    }

    public void addSearchFragment(FragmentManager fm){
        //TODO finish this
    }




//    displayFragment(fm, R.id.tabs_fragment_container, TabsFragment.class,
//                getString(R.string.tag_tabs_fragment), new String[]{displayedFragment}, true);


//    public void displayFragment(FragmentManager fm, int displayContainerId, Fragment displayFragment,
//                                    String displayFragmentTag, String[] fragmentsToRemoveTags, boolean changeProp){
//        logger.d(TAG, "Displaying " + displayFragmentTag);
//        FragmentTransaction transaction = fm.beginTransaction();
//
//        for(String tag : fragmentsToRemoveTags){
//            Fragment fragment = fm.findFragmentByTag(tag);
//            if(fragment != null){
//                transaction.remove(fragment);
//            }
//        }
//
//        transaction.replace(displayContainerId, displayFragment, displayFragmentTag);
//        transaction.commit();
//        if(changeProp){
//            contactsModel.setProperty(R.string.prop_displayed_fragment, displayFragmentTag);
//        }
//    }











    /*
     * TODO Everything below is old code and should be removed
     */

//    private boolean isFragmentUIActive(Fragment fragment){
//        return fragment != null && fragment.isAdded() && !fragment.isDetached() && !fragment.isRemoving();
//    }
//
//    public void displayCurrentFragment(FragmentManager fm){
//        Fragment fragment = null;
//        int containerId = -1;
//        String displayedFragment = contactsModel.getProperty(R.string.prop_displayed_fragment, String.class);
//        if(!StringUtils.isEmpty(displayedFragment)){
//            fragment = fm.findFragmentByTag(displayedFragment);
//            if(displayedFragment.equals(getString(R.string.tag_tabs_fragment))){
//                containerId = R.id.tabs_fragment_container;
//            }
//            else{
//                containerId = R.id.no_tabs_fragment_container;
//            }
//        }
//
//        if(fragment != null && isFragmentUIActive(fragment)){
//            displayFragment(fm, containerId, fragment, displayedFragment, new String[] {}, true);
//        }
//        else{
//            displayFragment(fm, R.id.tabs_fragment_container, TabsFragment.class, getString(R.string.tag_tabs_fragment), new String[] {}, true);
//        }
//    }
//
//    public void displayTabsFragment(FragmentManager fm){
//        String displayedFragment = contactsModel.getProperty(R.string.prop_displayed_fragment, String.class);
//        displayFragment(fm, R.id.tabs_fragment_container, TabsFragment.class,
//                getString(R.string.tag_tabs_fragment), new String[]{displayedFragment}, true);
//    }
//
//    public void displayFragment(FragmentManager fm, int displayContainerId, Class<? extends Fragment> displayFragmentType,
//                                        String displayFragmentTag, String[] fragmentsToRemoveTags, boolean changeProp){
//        Fragment fragment = ObjectCreator.instantiateClass(displayFragmentType);
//        displayFragment(fm, displayContainerId, fragment, displayFragmentTag, fragmentsToRemoveTags, changeProp);
//    }
//
//    public void displayFragment(FragmentManager fm, int displayContainerId, Fragment displayFragment,
//                                    String displayFragmentTag, String[] fragmentsToRemoveTags, boolean changeProp){
//        logger.d(TAG, "Displaying " + displayFragmentTag);
//        FragmentTransaction transaction = fm.beginTransaction();
//
//        for(String tag : fragmentsToRemoveTags){
//            Fragment fragment = fm.findFragmentByTag(tag);
//            if(fragment != null){
//                transaction.remove(fragment);
//            }
//        }
//
//        transaction.replace(displayContainerId, displayFragment, displayFragmentTag);
//        transaction.commit();
//        if(changeProp){
//            contactsModel.setProperty(R.string.prop_displayed_fragment, displayFragmentTag);
//        }
//    }
//
//    public void displayNoTabsFragment(FragmentManager fm){
//        String displayedFragment = contactsModel.getProperty(R.string.prop_displayed_fragment, String.class);
//        displayFragment(fm, R.id.no_tabs_fragment_container, ContactsInGroupFragment.class,
//                getString(R.string.tag_no_tabs_fragment), new String[]{displayedFragment}, true);
//    }
//
//    public void displayContactsFragment(FragmentManager fm) {
//        String displayedFragment = contactsModel.getProperty(R.string.prop_displayed_fragment, String.class);
//        if(getString(R.string.tag_tabs_fragment).equals(displayedFragment)){
//            Fragment fragment = new AllContactsFragment();
//            displayFragment(fm, R.id.no_tabs_fragment_container, fragment,
//                    getString(R.string.tag_search_fragment), new String[]{displayedFragment}, false);
//        }
//    }

}
