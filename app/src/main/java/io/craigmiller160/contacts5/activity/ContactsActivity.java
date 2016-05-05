package io.craigmiller160.contacts5.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import io.craigmiller160.contacts5.R;
import io.craigmiller160.contacts5.service.PermissionsManager;
import io.craigmiller160.locus.Locus;
import io.craigmiller160.locus.LocusDebug;

import static io.craigmiller160.contacts5.util.ContactsConstants.*;

/**
 * Created by craig on 5/4/16.
 */
public class ContactsActivity extends AppCompatActivity {

    private static final String TAG = "ContactsActivity";

    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_contacts);

        //Check permissions
        if(!PermissionsManager.hasReadContactsPermission(this)){
            PermissionsManager.requestReadContactsPermission(this);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.contactsActivityToolbar);
        setSupportActionBar(toolbar);



        findViewById(R.id.addContact).setOnClickListener(
                Locus.controller.getController(
                        ADD_CONTACT_CONTROLLER, View.OnClickListener.class, this)
        );
    }

}
