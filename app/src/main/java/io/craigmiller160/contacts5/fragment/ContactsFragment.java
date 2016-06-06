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
public class ContactsFragment extends Fragment {

    private static final String TAG = "ContactsFragment";
    private static final String PROPERTY_NAME = "PropertyName";
    public static final String TITLE = "All Contacts";

    private ArrayAdapter<Contact> contactsArrayAdapter;
    private final PermissionsService permissionsService;

    private String propertyName;


    public ContactsFragment(){
        this.permissionsService = ServiceFactory.getInstance().getPermissionsService();
    }

    public void setPropertyName(String propertyName){
        this.propertyName = propertyName;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null){
            propertyName = savedInstanceState.getString(PROPERTY_NAME);
        }

        if(propertyName == null){
            throw new IllegalStateException("ContactsFragment cannot be created until its property name value is set");
        }
        contactsArrayAdapter = new ContactsArrayAdapter(getContext(), propertyName);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstance){
        savedInstance.putString(PROPERTY_NAME, propertyName);
        super.onSaveInstanceState(savedInstance);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance){
        if(permissionsService.hasReadContactsPermission()){
            ListView view = (ListView) inflater.inflate(R.layout.content_list, container, false);
            view.setDivider(null);
            view.setFastScrollEnabled(true);
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
