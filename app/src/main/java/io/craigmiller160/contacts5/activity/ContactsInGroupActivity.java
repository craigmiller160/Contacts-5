package io.craigmiller160.contacts5.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;

import io.craigmiller160.contacts5.ContactsApp;
import io.craigmiller160.contacts5.R;
import io.craigmiller160.contacts5.adapter.ContactsArrayAdapter;
import io.craigmiller160.contacts5.service.ContactsRetrievalService;

import static io.craigmiller160.contacts5.util.ContactsConstants.CONTACTS_LIST;
import static io.craigmiller160.contacts5.util.ContactsConstants.*;

/**
 * Created by craig on 5/30/16.
 */
public class ContactsInGroupActivity extends AppCompatActivity {

    private static final String TAG = "ContactsInGroupActivity";

    private ContactsRetrievalService contactsRetrievalService;
    private ContactsArrayAdapter contactsArrayAdapter;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_contacts_in_group);
        contactsRetrievalService = ContactsApp.getApp().serviceFactory().getContactsRetrievalService();

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

        if(savedInstance != null){
            ContactsApp.getApp().modelFactory().getModel(CONTACTS_MODEL).restoreState(savedInstance);
        }


        contactsArrayAdapter = new ContactsArrayAdapter(this, CONTACTS_LIST);
        ListView listView = (ListView) findViewById(R.id.content_list);
        listView.setFastScrollEnabled(true);
        listView.setDivider(null);
        listView.setAdapter(contactsArrayAdapter);

        if(savedInstance == null){
            contactsRetrievalService.loadAllContactsInGroup(groupId);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == SELECT_CONTACT_REQUEST){
            Intent intent = getIntent();
            if(intent != null){
                long groupId = intent.getLongExtra(getString(R.string.group_id), -1);
                if(groupId != -1){
                    contactsRetrievalService.loadAllContactsInGroup(groupId);
                }
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedState){
        ContactsApp.getApp().modelFactory().getModel(CONTACTS_MODEL).storeState(savedState);
        super.onSaveInstanceState(savedState);
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
}