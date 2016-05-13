package io.craigmiller160.contacts5.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import io.craigmiller160.contacts5.R;
import io.craigmiller160.contacts5.adapter.ContactsArrayAdapter;
import io.craigmiller160.contacts5.model.Contact;
import io.craigmiller160.contacts5.service.PermissionsService;
import io.craigmiller160.contacts5.service.ServiceFactory;

/**
 * Created by craig on 5/8/16.
 */
public class AllContactsPage extends Fragment {

    private static final String TAG = "AllContactsPage";

    public static final String TITLE = "All Contacts";

    private ArrayAdapter<Contact> contactsArrayAdapter;

    private ListView view;

    private final PermissionsService permissionsService;

    public AllContactsPage(){
        this.permissionsService = ServiceFactory.getInstance().getPermissionsService();
    }

    public void notifyStorageChanged(){
        if(contactsArrayAdapter != null){
            contactsArrayAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    //TODO this should be revamped further to be more MVP-style

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance){
        if(permissionsService.hasReadContactsPermission()){
            Log.d(TAG, "Building list of contacts for All Contacts page");

            view = (ListView) inflater.inflate(R.layout.content_contacts_list, container, false);
            view.setDivider(null);
            view.setFastScrollEnabled(true);

            contactsArrayAdapter = new ContactsArrayAdapter(getActivity(), view);

            view.setAdapter(contactsArrayAdapter);

            return view;
        }
        else{
            Log.d(TAG, "Displaying no permissions page on All Contacts tab");
            View view = inflater.inflate(R.layout.content_no_permissions, container, false);
            return view;
        }
    }

}
