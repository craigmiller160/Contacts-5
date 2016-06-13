package io.craigmiller160.contacts5.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import io.craigmiller160.contacts5.ContactsApp;
import io.craigmiller160.contacts5.R;
import io.craigmiller160.contacts5.adapter.ContactsArrayAdapter;
import io.craigmiller160.contacts5.model.AndroidModel;
import io.craigmiller160.contacts5.model.Contact;
import io.craigmiller160.contacts5.service.ContactsRetrievalService;
import io.craigmiller160.contacts5.service.PermissionsService;

import static io.craigmiller160.contacts5.util.ContactsConstants.CONTACTS_IN_GROUP_LIST;
import static io.craigmiller160.contacts5.util.ContactsConstants.CONTACTS_MODEL;
import static io.craigmiller160.contacts5.util.ContactsConstants.SELECTED_GROUP_ID;
import static io.craigmiller160.contacts5.util.ContactsConstants.SELECTED_GROUP_NAME;

/**
 * Created by craig on 6/5/16.
 */
public class ContactsInGroupFragment extends AbstractContactsFragment<Contact> {

    private AndroidModel contactsModel;

    @Override
    protected ArrayAdapter<Contact> getArrayAdapter() {
        return new ContactsArrayAdapter(getContext(), CONTACTS_IN_GROUP_LIST);
    }

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        this.contactsModel = ContactsApp.getApp().modelFactory().getModel(CONTACTS_MODEL);
        ContactsRetrievalService contactsService = ContactsApp.getApp().serviceFactory().getContactsRetrievalService();
        PermissionsService permissionsService = ContactsApp.getApp().serviceFactory().getPermissionsService();

        Long groupId = contactsModel.getProperty(SELECTED_GROUP_ID, Long.class);

        if(groupId != null && groupId >= 0 && permissionsService.hasReadContactsPermission() && savedInstance == null){
            contactsService.loadAllContactsInGroup(groupId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        View view = super.onCreateView(inflater, container, savedInstance);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        View viewPager = activity.findViewById(R.id.contactsTabsViewPager);
        viewPager.setVisibility(View.GONE);

        String groupName = contactsModel.getProperty(SELECTED_GROUP_NAME, String.class);
        if(groupName != null){
            activity.setTitle(groupName);
        }

        return view;
    }

}