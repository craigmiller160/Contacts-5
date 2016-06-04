package io.craigmiller160.contacts5.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import io.craigmiller160.contacts5.R;
import io.craigmiller160.contacts5.adapter.ContactsArrayAdapter;
import io.craigmiller160.contacts5.model.Contact;
import io.craigmiller160.contacts5.model.ContactGroup;
import io.craigmiller160.contacts5.model.ContactsDataCallback;
import io.craigmiller160.contacts5.service.ContactsRetrievalService;
import io.craigmiller160.contacts5.service.ServiceFactory;

import static io.craigmiller160.contacts5.util.ContactsConstants.CONTACTS_LIST;
import static io.craigmiller160.contacts5.util.ContactsConstants.SELECT_CONTACT_ID;

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
        ListView listView = (ListView) findViewById(R.id.content_list);
        listView.setFastScrollEnabled(true);
        listView.setDivider(null);
        listView.setAdapter(contactsArrayAdapter);

        if(bundle != null && bundle.getSerializable(CONTACTS_LIST) != null){
            contactsArrayAdapter.setContactsList((List<Contact>) bundle.getSerializable(CONTACTS_LIST));
        }
        else if(groupId >= 0){
            Log.d(TAG, "Displaying contacts from group: " + groupName);
            contactsRetrievalService.loadAllContactsInGroup(this, groupId);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == SELECT_CONTACT_ID){
            Intent intent = getIntent();
            if(intent != null){
                long groupId = intent.getLongExtra(getString(R.string.group_id), -1);
                if(groupId != -1){
                    contactsRetrievalService.loadAllContactsInGroup(this, groupId);
                }
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedState){
        if(contactsArrayAdapter != null){
            List<Contact> contacts = contactsArrayAdapter.getContactsList();
            if(contacts != null){
                savedState.putSerializable(CONTACTS_LIST, (ArrayList) contacts);
            }
        }
    }

    @Override
    public void onBackPressed(){
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
