package io.craigmiller160.contacts5.controller;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import io.craigmiller160.contacts5.R;
import io.craigmiller160.contacts5.activity.ContactsActivity;
import io.craigmiller160.contacts5.adapter.ContactsArrayAdapter;
import io.craigmiller160.contacts5.application.ContactsApplication;
import io.craigmiller160.contacts5.helper.Helper;
import io.craigmiller160.contacts5.model.Contact;
import io.craigmiller160.contacts5.model.LookupContact;
import io.craigmiller160.contacts5.service.ContactsRetrievalService2;

/**
 * Created by Craig on 2/17/2016.
 */
public class ContactsInGroupActivityController extends AbstractActivityController {

    private static final String TAG = "ContactsGroupActControl";
    ContactsRetrievalService2 service = new ContactsRetrievalService2();

    public ContactsInGroupActivityController() {
    }

    public ContactsInGroupActivityController(Helper helper) {
        super(helper);
    }

    public ContactsInGroupActivityController(Activity activity, Activity activity1) {
        super(activity, activity1);
    }

    public ContactsInGroupActivityController(Activity activity, Helper helper, Activity activity1) {
        super(activity, helper, activity1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == ContactsActivity.CONTACT_ACTION_VIEW_ID){
            contactModified(); //TODO need unified behavior between this activity and ContactsActivity
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            getActivity().finish();
            return true;
        }
        return false;
    }

    public void contactModified(){
        ListView view = (ListView) getActivity().findViewById(R.id.contactsList);
        LookupContact lookupContact = ContactsApplication.getInstance().getLookupContact();
        if(lookupContact != null){
            ContactsApplication.getInstance().clearLookupContact();
            ContactsArrayAdapter adapter = (ContactsArrayAdapter) view.getAdapter();
            Contact contact = service.getContact(getActivity(), lookupContact.getContactId());
            if(contact != null){
                Log.i(TAG, "Updating list with modified contact");
                //TODO adapter.replace(contact, lookupContact.getPosition());
            }
            else{
                Log.i(TAG, "Removing deleted contact from list");
                //TODO adapter.remove(lookupContact.getPosition());
            }
        }
    }

    public void contactAdded(){
        ListView view = (ListView) getActivity().findViewById(R.id.contactsList);
        ((ArrayAdapter) view.getAdapter()).notifyDataSetChanged();
    }
}
