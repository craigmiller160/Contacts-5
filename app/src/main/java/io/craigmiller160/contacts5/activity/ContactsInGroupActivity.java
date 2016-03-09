package io.craigmiller160.contacts5.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import java.beans.PropertyChangeEvent;
import java.util.List;

import io.craigmiller160.contacts5.R;
import io.craigmiller160.contacts5.adapter.ContactsArrayAdapter;
import io.craigmiller160.contacts5.model.Contact;
import io.craigmiller160.contacts5.model.ContactGroup;
import io.craigmiller160.contacts5.service.ContactsRetrievalService2;

import static io.craigmiller160.contacts5.helper.ContactsHelper.*;

/**
 * Created by Craig on 2/3/2016.
 */
public class ContactsInGroupActivity extends AbstractMVPActivity {

    private static final String TAG = "ContactsInGroupActivity";

    private ContactsRetrievalService2 contactsService = new ContactsRetrievalService2();

    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.activity_contacts_in_group);

        Intent intent = getIntent();
        long groupId = -1;
        String groupName = "";
        if(intent != null){
            groupName = intent.getStringExtra(ContactGroup.GROUP_NAME_PROP);
            setTitle(groupName);
            groupId = intent.getLongExtra(ContactGroup.GROUP_ID_PROP, -1);
        }
        else{
            Log.e(TAG, "getIntent() returned null");
            //TODO move text to String
            setTitle("Contacts 5+");
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ListView listView = (ListView) findViewById(R.id.contactsList);
        listView.setFastScrollEnabled(true);
        listView.setDivider(null);
        if(groupId >= 0){
            Log.i(TAG, "Displaying contacts from group: " + groupName);
            List<Contact> contactsList = contactsService.getAllContactsInGroup(this, groupId);
            listView.setAdapter(new ContactsArrayAdapter(this, listView));
        }
        else{
            Log.e(TAG, "Group ID: " + -1);
        }

    }

    @Override
    protected String getActivityControllerName() {
        return CONTACTS_IN_GROUP_ACTIVITY_CONTROLLER;
    }

    @Override
    public void updateView(PropertyChangeEvent event) {

    }
}
