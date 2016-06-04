package io.craigmiller160.contacts5.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import java.util.List;

import io.craigmiller160.contacts5.R;
import io.craigmiller160.contacts5.adapter.ContactsArrayAdapter;
import io.craigmiller160.contacts5.model.Contact;
import io.craigmiller160.contacts5.model.ContactGroup;
import io.craigmiller160.contacts5.model.ContactsDataCallback;
import io.craigmiller160.contacts5.service.ContactsRetrievalService;
import io.craigmiller160.contacts5.service.ServiceFactory;

/**
 * Created by craig on 5/30/16.
 */
public class ContactsInGroupActivity extends AppCompatActivity implements ContactsDataCallback {

    private static final String TAG = "ContactsInGroupActivity";

    private ContactsRetrievalService contactsRetrievalService;
    private ContactsArrayAdapter contactsArrayAdapter;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_contacts_in_group);
        contactsRetrievalService = ServiceFactory.getInstance().getContactsRetrievalService();

        Intent intent = getIntent();
        long groupId = -1;
        String groupName = "";
        if(intent != null){
            groupName = intent.getStringExtra(getString(R.string.group_name));
            setTitle(groupName);
            groupId = intent.getLongExtra(getString(R.string.group_id), -1);
        }
        else{
            Log.e(TAG, "getIntent() returned null");
            //TODO move text to String
            setTitle("Contacts 5+");
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        contactsArrayAdapter = new ContactsArrayAdapter(this);
        ListView listView = (ListView) findViewById(R.id.contactsList);
        listView.setFastScrollEnabled(true);
        listView.setDivider(null);
        listView.setAdapter(contactsArrayAdapter);

        if(groupId >= 0){
            Log.d(TAG, "Displaying contacts from group: " + groupName);
            contactsRetrievalService.loadAllContactsInGroup(this, groupId);
        }

//        if(groupId >= 0){
//            Log.i(TAG, "Displaying contacts from group: " + groupName);
//            List<Contact> contactsList = contactsService.getAllContactsInGroup(this, groupId);
//            listView.setAdapter(new ContactsArrayAdapter(this, listView));
//        }
//        else{
//            Log.e(TAG, "Group ID: " + -1);
//        }
    }

    @Override
    public void setContactsList(List<Contact> contacts) {
        contactsArrayAdapter.setContactsList(contacts);
    }

    @Override
    public void setGroupsList(List<ContactGroup> groups) {
        //Do nothing
    }
}
