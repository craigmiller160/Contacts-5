package io.craigmiller160.contacts5.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import io.craigmiller160.contacts5.R;
import io.craigmiller160.contacts5.service.AccountService;
import io.craigmiller160.contacts5.service.PermissionsService;
import io.craigmiller160.contacts5.service.ServiceFactory;
import io.craigmiller160.contacts5.view.AndroidActivityView;
import io.craigmiller160.contacts5.view.ContactsActivityView;

import static io.craigmiller160.contacts5.util.ContactsConstants.*;

/**
 * Created by craig on 5/4/16.
 */
public class ContactsActivity extends AndroidActivity {

    private static final String TAG = "ContactsActivity";

    private PermissionsService permissionsService;

    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        this.permissionsService = ServiceFactory.getInstance().getPermissionsService();

        //Check permissions
        if(!permissionsService.hasReadContactsPermission()){
            permissionsService.requestReadContactsPermission(this);
        }
    }

    @Override
    protected AndroidActivityView getAndroidView() {
        return new ContactsActivityView(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //TODO review and restore this code
        if(requestCode == SETTINGS_ACTIVITY_ID){
            //TODO review threading use here
            //Using a handler here so that recreate will be called after main thread has finished current task
            Handler h = new Handler();
            h.post(new Runnable() {
                @Override
                public void run() {
                    recreate();
                }
            });
        }
//        else if(requestCode == CONTACT_ACTION_VIEW_ID){
//            if(data != null){
//                System.out.println("View Contact: Data Not Null");
//            }
//            else{
//                System.out.println("View Contact: Data Null");
//            }
//
//            contactModified();
//        }
//        else if(requestCode == NEW_CONTACT_VIEW_ID){
//            if(data != null){
//                System.out.println("New Contact: Data Not Null");
//                System.out.println("URI: " + data.getData());
//            }
//            else{
//                System.out.println("New Contact: Data Null");
//            }
//
//            contactAdded();
//        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PermissionsService.CONTACTS_PERMISSION_REQUEST:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Log.d(TAG, "Contacts Permission Granted");
                    Intent intent = new Intent(this, ContactsActivity.class);
                    startActivity(intent);
                    finish(); //TODO do I really want to do this here anymore?
                }
                else{
                    Log.e(TAG, "Contacts permission denied");
                    View view = findViewById(R.id.contactsActivityLayout);
                    //TODO move text to Strings
                    Snackbar snackbar = Snackbar.make(view, "Permission Denied!", Snackbar.LENGTH_LONG)
                            .setAction("GRANT", new View.OnClickListener(){
                                @Override
                                public void onClick(View v) {
                                    Log.d(TAG, "Requesting Contacts permissions from snackbar");
                                    permissionsService.requestReadContactsPermission(ContactsActivity.this);
                                }
                            });
                    snackbar.setActionTextColor(Color.YELLOW);
                    snackbar.show();
                }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //TODO refactor this for changes to application
        if (item.getItemId() == R.id.displaySettings) {
            Log.i(TAG, "Opening display settings");
            Intent intent = new Intent(this, DisplaySettingsActivity.class);
            startActivityForResult(intent, SETTINGS_ACTIVITY_ID);
            return true;
        }
        else if(item.getItemId() == R.id.grantPermissions){
            if(!permissionsService.hasReadContactsPermission()){
                Log.i(TAG, "Requesting Permissions from menu item");
                permissionsService.requestReadContactsPermission(this);
            }
        }

        return false;
    }

}
