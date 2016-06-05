package io.craigmiller160.contacts5.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.List;

import io.craigmiller160.contacts5.R;
import io.craigmiller160.contacts5.adapter.ContactsArrayAdapter;
import io.craigmiller160.contacts5.adapter.ContactsViewAdapter;
import io.craigmiller160.contacts5.model.Contact;
import io.craigmiller160.contacts5.service.PermissionsService;
import io.craigmiller160.contacts5.service.ServiceFactory;

/**
 * Created by craig on 5/8/16.
 */
public class AllContactsPage extends Fragment {

    private static final String TAG = "AllContactsPage";

    public static final String TITLE = "All Contacts";

    private ContactsArrayAdapter contactsArrayAdapter;

    private final PermissionsService permissionsService;
    private ListView listView;

//    private List<Contact> contacts;

    public AllContactsPage(){
        this.permissionsService = ServiceFactory.getInstance().getPermissionsService();
    }

//    public void setContactsList(List<Contact> contacts){
//        this.contacts = contacts;
//        if(contactsArrayAdapter != null){
//            contactsArrayAdapter.setContactsList(contacts);
//        }
//    }

//    public List<Contact> getContactsList(){
//        return contacts;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contactsArrayAdapter = new ContactsArrayAdapter(getActivity(), ContactsArrayAdapter.CONTACTS);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance){
        if(permissionsService.hasReadContactsPermission()){
            LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.content_list, container, false);
            listView= (ListView) layout.findViewById(R.id.content_list);
            listView.setDivider(null);
            listView.setFastScrollEnabled(true);
            listView.setNestedScrollingEnabled(true); //TODO need to test SDK version here

            listView.setAdapter(contactsArrayAdapter);
//            RecyclerView view = (RecyclerView) layout.findViewById(R.id.content_list);
//            view.setLayoutManager(new LinearLayoutManager(getContext()));
//            view.setAdapter(new ContactsViewAdapter());
            return layout;
        }
        else{
            Log.d(TAG, "Displaying no permissions page on All Contacts tab");
            View view = inflater.inflate(R.layout.content_no_permissions, container, false);
            return view;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstance){
        super.onActivityCreated(savedInstance);
//        listView.setAdapter(contactsArrayAdapter);
    }

}
