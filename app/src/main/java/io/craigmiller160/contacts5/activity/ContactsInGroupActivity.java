package io.craigmiller160.contacts5.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.List;

import io.craigmiller160.contacts5.R;
import io.craigmiller160.contacts5.adapter.ContactsArrayAdapter;
import io.craigmiller160.contacts5.controller.ContactUpdateController;
import io.craigmiller160.contacts5.model.Contact;
import io.craigmiller160.contacts5.model.ContactGroup;
import io.craigmiller160.contacts5.service.ContactsRetrievalService;

/**
 * Created by Craig on 2/3/2016.
 */
public class ContactsInGroupActivity extends AppCompatActivity {

    private static final String TAG = "ContactsInGroupActivity";

    private ContactsRetrievalService contactsService = new ContactsRetrievalService();

    private ContactUpdateController contactUpdateController = new ContactUpdateController(this);

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
            listView.setAdapter(new ContactsArrayAdapter(this, contactsList, listView));
        }
        else{
            Log.e(TAG, "Group ID: " + -1);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home){
            finish();
            return true;
        }

        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == ContactsActivity.CONTACT_ACTION_VIEW_ID){
            contactUpdateController.contactModified();
        }
    }


}
