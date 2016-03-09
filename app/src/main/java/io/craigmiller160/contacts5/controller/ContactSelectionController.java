package io.craigmiller160.contacts5.controller;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;

import io.craigmiller160.contacts5.application.ContactsApplication;
import io.craigmiller160.contacts5.activity.ContactsActivity;
import io.craigmiller160.contacts5.model.Contact;
import io.craigmiller160.contacts5.model.LookupContact;

/**
 * Created by Craig on 2/6/2016.
 */
public class ContactSelectionController implements View.OnClickListener {

    private Contact contact;
    private int position;

    private static final String TAG = "ContactSelectController";

    public ContactSelectionController(Contact contact, int position){
        this.contact = contact;
        this.position = position;
    }

    @Override
    public void onClick(View view) {
        Log.i(TAG, "Opening action view for contact. Contact: " + contact.getDisplayName());
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(contact.getId()));
        intent.setData(uri);
        LookupContact lookupContact = new LookupContact(position, contact.getId());
        ContactsApplication.getInstance().setLookupContact(lookupContact);
        ((Activity) view.getContext()).startActivityForResult(intent, ContactsActivity.CONTACT_ACTION_VIEW_ID);
    }
}
