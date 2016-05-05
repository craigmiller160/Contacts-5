package io.craigmiller160.contacts5.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import io.craigmiller160.contacts5.R;
import io.craigmiller160.contacts5.old.activity.DisplaySettingsActivity;
import io.craigmiller160.contacts5.old.adapter.TabsPagerAdapter;
import io.craigmiller160.contacts5.old.fragment.AllContactsPage;
import io.craigmiller160.contacts5.old.fragment.ContactsGroupsPage;
import io.craigmiller160.contacts5.service.PermissionsManager;
import io.craigmiller160.contacts5.view.AndroidActivityView;
import io.craigmiller160.contacts5.view.ContactsActivityView;
import io.craigmiller160.locus.Locus;
import io.craigmiller160.locus.LocusDebug;

import static io.craigmiller160.contacts5.old.activity.ContactsActivity.CONTACT_ACTION_VIEW_ID;
import static io.craigmiller160.contacts5.old.activity.ContactsActivity.NEW_CONTACT_VIEW_ID;
import static io.craigmiller160.contacts5.old.activity.ContactsActivity.SETTINGS_ACTIVITY_ID;
import static io.craigmiller160.contacts5.util.ContactsConstants.*;

/**
 * Created by craig on 5/4/16.
 */
public class ContactsActivity extends AndroidActivity {

    private static final String TAG = "ContactsActivity";

    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);

        //Check permissions
        if(!PermissionsManager.hasReadContactsPermission(this)){
            PermissionsManager.requestReadContactsPermission(this);
        }
    }

    @Override
    protected AndroidActivityView getView() {
        return new ContactsActivityView(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //TODO review and restore this code
//        if(requestCode == SETTINGS_ACTIVITY_ID){
//            //Using a handler here so that recreate will be called after main thread has finished current task
//            Handler h = new Handler();
//            h.post(new Runnable() {
//                @Override
//                public void run() {
//                    getActivity().recreate();
//                }
//            });
//        }
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
            case PermissionsManager.CONTACTS_PERMISSION_REQUEST:
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
                                    PermissionsManager.requestReadContactsPermission(ContactsActivity.this);
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
            if(!PermissionsManager.hasReadContactsPermission(this)){
                Log.i(TAG, "Requesting Permissions from menu item");
                PermissionsManager.requestReadContactsPermission(this);
            }
        }

        return false;
    }

}
