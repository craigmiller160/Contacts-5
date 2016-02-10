package io.craigmiller160.contacts5.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.List;

import io.craigmiller160.contacts5.R;
import io.craigmiller160.contacts5.adapter.ContactsArrayAdapter;
import io.craigmiller160.contacts5.model.Contact;
import io.craigmiller160.contacts5.service.ContactsRetrievalService;
import io.craigmiller160.contacts5.service.PermissionsManager;

/**
* Created by Craig on 1/24/2016.
*/
public class AllContactsPage extends Fragment {
    private ContactsRetrievalService contactsService = new ContactsRetrievalService();

    private static final String TAG = "AllContactsPage";

    public static final String TITLE = "All Contacts";

    public AllContactsPage(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance){
        if(PermissionsManager.hasReadContactsPermission(getActivity())){
            Log.d(TAG, "Building list of contacts for All Contacts page");

            ListView view = (ListView) inflater.inflate(R.layout.content_contacts_list, container, false);
            view.setDivider(null);
            view.setFastScrollEnabled(true);

            List<Contact> contactsList = contactsService.getAllContacts(getActivity());
            if(contactsList != null){ //TODO consider changing the service so it will return an empty list instead of null
                view.setAdapter(new ContactsArrayAdapter(getActivity(), contactsList, view));
            }

            return view;
        }
        else{
            Log.d(TAG, "Displaying no permissions page on All Contacts tab");
            View view = inflater.inflate(R.layout.content_no_permissions, container, false);
            return view;
        }
    }

}
