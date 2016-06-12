package io.craigmiller160.contacts5.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import io.craigmiller160.contacts5.ContactsApp;
import io.craigmiller160.contacts5.R;
import io.craigmiller160.contacts5.model.AndroidModel;

import static io.craigmiller160.contacts5.util.ContactsConstants.*;

/**
 * Created by craig on 6/12/16.
 */
public class FragmentChanger {

    public static void displayTabsFragment(FragmentManager fm){
        AndroidModel contactsModel = ContactsApp.getApp().modelFactory().getModel(CONTACTS_MODEL);
        String displayedFragment = contactsModel.getProperty(DISPLAYED_FRAGMENT, String.class);

        if(TABS_FRAGMENT_TAG.equals(displayedFragment)){
            return;
        }

        FragmentTransaction transaction = fm.beginTransaction();

        if(NO_TABS_FRAGMENT_TAG.equals(displayedFragment)){
            Fragment noTabsFragment = fm.findFragmentByTag(NO_TABS_FRAGMENT_TAG);
            if(noTabsFragment != null){
                transaction.remove(noTabsFragment);
            }
        }

        transaction.replace(R.id.tabs_fragment_container, new TabsFragment(), TABS_FRAGMENT_TAG);
        transaction.commit();
        contactsModel.setProperty(DISPLAYED_FRAGMENT, TABS_FRAGMENT_TAG);
    }

    public static void displayNoTabsFragmnet(FragmentManager fm){
        AndroidModel contactsModel = ContactsApp.getApp().modelFactory().getModel(CONTACTS_MODEL);
        String displayedFragment = contactsModel.getProperty(DISPLAYED_FRAGMENT, String.class);

        if(NO_TABS_FRAGMENT_TAG.equals(displayedFragment)){
            return;
        }

        FragmentTransaction transaction = fm.beginTransaction();

        if(TABS_FRAGMENT_TAG.equals(displayedFragment)){
            Fragment tabsFragment = fm.findFragmentByTag(TABS_FRAGMENT_TAG);
            if(tabsFragment != null){
                transaction.remove(tabsFragment);
            }
        }

        transaction.replace(R.id.no_tabs_fragment_container, new ContactsInGroupFragment(), NO_TABS_FRAGMENT_TAG);
        transaction.commit();
        contactsModel.setProperty(DISPLAYED_FRAGMENT, NO_TABS_FRAGMENT_TAG);
    }

}
