package io.craigmiller160.contacts5.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import io.craigmiller160.contacts5.ContactsApp;
import io.craigmiller160.contacts5.R;
import io.craigmiller160.contacts5.adapter.ContactsArrayAdapter;
import io.craigmiller160.contacts5.model.AndroidModel;
import io.craigmiller160.contacts5.model.Contact;
import io.craigmiller160.contacts5.service.ContactsRetrievalService;
import io.craigmiller160.contacts5.service.ContactsService;
import io.craigmiller160.contacts5.util.AndroidSystemUtil;

import static io.craigmiller160.contacts5.util.ContactsConstants.CONTACTS_IN_GROUP_LIST;
import static io.craigmiller160.contacts5.util.ContactsConstants.CONTACTS_MODEL;
import static io.craigmiller160.contacts5.util.ContactsConstants.SELECTED_GROUP_ID;
import static io.craigmiller160.contacts5.util.ContactsConstants.SELECTED_GROUP_NAME;

/**
 * Created by craig on 6/5/16.
 */
public class ContactsInGroupFragment extends AbstractContactsFragment<Contact> {

    private static final String TAG = "ContactsInGroupFragment";

    private AndroidModel contactsModel;

    @Override
    protected ArrayAdapter<Contact> getArrayAdapter() {
        return new ContactsArrayAdapter(getContext(), CONTACTS_IN_GROUP_LIST);
    }

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        Log.v(TAG, "ContactsInGroupFragment created");
        this.contactsModel = ContactsApp.getApp().modelFactory().getModel(CONTACTS_MODEL);
        ContactsRetrievalService contactsService = ContactsApp.getApp().serviceFactory().getContactsRetrievalService();
        AndroidSystemUtil androidSystemUtil = new AndroidSystemUtil(getActivity());

        Long groupId = contactsModel.getProperty(SELECTED_GROUP_ID, Long.class);
        String groupName = contactsModel.getProperty(SELECTED_GROUP_NAME, String.class);

        if(groupId != null && groupId >= 0 && androidSystemUtil.permissions().hasReadContactsPermission() && savedInstance == null){
            Intent intent = new Intent(getActivity(), ContactsService.class);
            intent.putExtra(ContactsService.LOAD_CONTACTS_IN_GROUP, true);
            intent.putExtra(SELECTED_GROUP_ID, groupId);
            intent.putExtra(SELECTED_GROUP_NAME, groupName);

            getActivity().startService(intent);
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