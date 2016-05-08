package io.craigmiller160.contacts5.old.controller;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import io.craigmiller160.contacts5.R;
import io.craigmiller160.contacts5.old.activity.ContactsActivity;
import io.craigmiller160.contacts5.old.activity.DisplaySettingsActivity;
import io.craigmiller160.contacts5.old.adapter.ContactsArrayAdapter;
import io.craigmiller160.contacts5.old.application.ContactsApplication;
import io.craigmiller160.contacts5.old.helper.Helper;
import io.craigmiller160.contacts5.old.model.Contact;
import io.craigmiller160.contacts5.old.model.LookupContact;
import io.craigmiller160.contacts5.old.service.ContactsRetrievalService2;
import io.craigmiller160.contacts5.service.PermissionsService;

import static io.craigmiller160.contacts5.old.activity.ContactsActivity.*;

/**
 * Created by Craig on 2/17/2016.
 */
public class ContactsActivityController extends AbstractActivityController{

    private static final String TAG = "ContactsActivityControl";
    ContactsRetrievalService2 service = new ContactsRetrievalService2();

    public ContactsActivityController(){}

    public ContactsActivityController(Helper helper) {
        super(helper);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //TODO this will need to be heavily restructured
        if(requestCode == SETTINGS_ACTIVITY_ID){
            //Using a handler here so that recreate will be called after main thread has finished current task
            Handler h = new Handler();
            h.post(new Runnable() {
                @Override
                public void run() {
                    getActivity().recreate();
                }
            });
        }
        else if(requestCode == CONTACT_ACTION_VIEW_ID){
            if(data != null){
                System.out.println("View Contact: Data Not Null");
            }
            else{
                System.out.println("View Contact: Data Null");
            }

            contactModified();
        }
        else if(requestCode == NEW_CONTACT_VIEW_ID){
            if(data != null){
                System.out.println("New Contact: Data Not Null");
                System.out.println("URI: " + data.getData());
            }
            else{
                System.out.println("New Contact: Data Null");
            }

            contactAdded();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PermissionsService.CONTACTS_PERMISSION_REQUEST:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Log.d(TAG, "Contacts Permission Granted");
                    Intent intent = new Intent(getActivity(), ContactsActivity.class);
                    getActivity().startActivity(intent);
                    getActivity().finish(); //TODO do I really want to do this here anymore?
                }
                else{
                    Log.e(TAG, "Contacts permission denied");
                    View view = getActivity().findViewById(R.id.contactsActivityLayout);
                    //TODO move text to Strings
//                    Snackbar snackbar = Snackbar.make(view, "Permission Denied!", Snackbar.LENGTH_LONG)
//                            .setAction("GRANT", new View.OnClickListener(){
//                                @Override
//                                public void onClick(View v) {
//                                    Log.d(TAG, "Requesting Contacts permissions from snackbar");
//                                    PermissionsService.requestReadContactsPermission(getActivity());
//                                }
//                            });
//                    snackbar.setActionTextColor(Color.YELLOW);
//                    snackbar.show();
                }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.displaySettings) {
            Log.i(TAG, "Opening display settings");
            Intent intent = new Intent(getActivity(), DisplaySettingsActivity.class);
            getActivity().startActivityForResult(intent, SETTINGS_ACTIVITY_ID);
            return true;
        }
        else if(item.getItemId() == R.id.grantPermissions){
//            if(!PermissionsService.hasReadContactsPermission(getActivity())){
//                Log.i(TAG, "Requesting Permissions from menu item");
//                PermissionsService.requestReadContactsPermission(getActivity());
//            }
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
