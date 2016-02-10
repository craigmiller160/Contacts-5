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
import io.craigmiller160.contacts5.adapter.ContactsGroupsArrayAdapter;
import io.craigmiller160.contacts5.model.ContactGroup;
import io.craigmiller160.contacts5.service.ContactsRetrievalService;
import io.craigmiller160.contacts5.service.PermissionsManager;


/**
 * Created by Craig on 1/24/2016.
 */
public class ContactsGroupsPage extends Fragment {

    private static final String TAG = "ContactsGroupsPage";

    public static final String TITLE = "Groups";

    private ContactsRetrievalService contactsService = new ContactsRetrievalService();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance){
        if(PermissionsManager.hasReadContactsPermission(getActivity())){
            Log.d(TAG, "Building list of groups for Groups page");

            ListView view = (ListView) inflater.inflate(R.layout.content_contacts_list, container, false);
            view.setDivider(null);
            view.setFastScrollEnabled(true);

            List<ContactGroup> groupsList = contactsService.getAllGroups(getActivity());
            view.setAdapter(new ContactsGroupsArrayAdapter(getActivity(), groupsList));
            return view;
        }
        else{
            Log.d(TAG, "Displaying no permissions page on Groups tab");
            View view = inflater.inflate(R.layout.content_no_permissions, container, false);
            return view;
        }
    }

}
