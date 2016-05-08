package io.craigmiller160.contacts5.old.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import io.craigmiller160.contacts5.R;
import io.craigmiller160.contacts5.old.adapter.ContactsGroupsArrayAdapter;
import io.craigmiller160.contacts5.old.model.ContactGroup;
import io.craigmiller160.contacts5.old.service.ContactsService;
import io.craigmiller160.contacts5.old.service.ContactsServiceFactory;
import io.craigmiller160.contacts5.service.PermissionsService;


/**
 * Created by Craig on 1/24/2016.
 */
public class ContactsGroupsPage extends Fragment {

    private static final String TAG = "ContactsGroupsPage";

    public static final String TITLE = "Groups";

    private ContactsService contactsService = ContactsServiceFactory.getInstance().getContactsService();

    private ArrayAdapter<ContactGroup> groupArrayAdapter;

    public void notifyStorageChanged(){
        if(groupArrayAdapter != null){
            groupArrayAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance){
//        if(PermissionsService.hasReadContactsPermission(getActivity())){
//            Log.d(TAG, "Building list of groups for Groups page");
//
//            ListView view = (ListView) inflater.inflate(R.layout.content_contacts_list, container, false);
//            view.setDivider(null);
//            view.setFastScrollEnabled(true);
//
//            groupArrayAdapter = new ContactsGroupsArrayAdapter(getActivity());
//
//            view.setAdapter(groupArrayAdapter);
//            return view;
//        }
//        else{
//            Log.d(TAG, "Displaying no permissions page on Groups tab");
//            View view = inflater.inflate(R.layout.content_no_permissions, container, false);
//            return view;
//        }
        return null;
    }

}
