package io.craigmiller160.contacts5.controller;

import android.app.Activity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import io.craigmiller160.contacts5.application.ContactsApplication;
import io.craigmiller160.contacts5.R;
import io.craigmiller160.contacts5.adapter.ContactsArrayAdapter;
import io.craigmiller160.contacts5.model.Contact;
import io.craigmiller160.contacts5.model.LookupContact;
import io.craigmiller160.contacts5.service.ContactsRetrievalService2;

/**
 * Created by Craig on 2/6/2016.
 */
@Deprecated
public class ContactUpdateController {

    private static final String TAG = "ContactUpdateController";

    private Activity activity;
    ContactsRetrievalService2 service = new ContactsRetrievalService2();

    public ContactUpdateController(Activity activity){
        this.activity = activity;
    }

    public void contactModified(){
        ListView view = (ListView) activity.findViewById(R.id.contactsList);
        LookupContact lookupContact = ContactsApplication.getInstance().getLookupContact();
        if(lookupContact != null){
            ContactsApplication.getInstance().clearLookupContact();
            ContactsArrayAdapter adapter = (ContactsArrayAdapter) view.getAdapter();
            Contact contact = service.getContact(activity, lookupContact.getContactId());
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
        ListView view = (ListView) activity.findViewById(R.id.contactsList);
        ((ArrayAdapter) view.getAdapter()).notifyDataSetChanged();
    }

}
