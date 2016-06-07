package io.craigmiller160.contacts5.controller;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import io.craigmiller160.contacts5.ContactsApp;
import io.craigmiller160.contacts5.R;
import io.craigmiller160.contacts5.fragment.ContactsInGroupFragment;
import io.craigmiller160.contacts5.fragment.TabsFragment;
import io.craigmiller160.contacts5.service.ContactsRetrievalService;

import static io.craigmiller160.contacts5.util.ContactsConstants.CONTACTS_MODEL;
import static io.craigmiller160.contacts5.util.ContactsConstants.DISPLAYED_FRAGMENT;
import static io.craigmiller160.contacts5.util.ContactsConstants.SINGLE_FRAGMENT_TAG;
import static io.craigmiller160.contacts5.util.ContactsConstants.SELECTED_GROUP_ID;
import static io.craigmiller160.contacts5.util.ContactsConstants.SELECTED_GROUP_NAME;
import static io.craigmiller160.contacts5.util.ContactsConstants.TABS_FRAGMENT_TAG;

/**
 * Created by craig on 6/3/16.
 */
public class SelectGroupController extends AbstractAndroidController implements View.OnClickListener {

    private ContactsRetrievalService contactsService;

    public SelectGroupController(Context context) {
        super(context);
        this.contactsService = ContactsApp.getApp().serviceFactory().getContactsRetrievalService();
    }

    @Override
    public void onClick(View view) {
        long groupId = getArg(getResources().getString(R.string.group_id), Long.class);
        String groupName = getArg(getResources().getString(R.string.group_name), String.class);

        ContactsApp.getApp().modelFactory().getModel(CONTACTS_MODEL).setProperty(SELECTED_GROUP_ID, groupId);
        ContactsApp.getApp().modelFactory().getModel(CONTACTS_MODEL).setProperty(SELECTED_GROUP_NAME, groupName);

        AppCompatActivity activity = (AppCompatActivity) view.getContext();
        FragmentManager fm = activity.getSupportFragmentManager();
        TabsFragment tabsFragment = (TabsFragment) fm.findFragmentByTag(TABS_FRAGMENT_TAG);
        tabsFragment.removeFragments();
        fm.beginTransaction().remove(tabsFragment).commit();
        fm.beginTransaction().replace(R.id.fragment_container, new ContactsInGroupFragment(), SINGLE_FRAGMENT_TAG).commit();
        ContactsApp.getApp().modelFactory().getModel(CONTACTS_MODEL).setProperty(DISPLAYED_FRAGMENT, SINGLE_FRAGMENT_TAG);

        contactsService.loadAllContactsInGroup(groupId);
    }
}
