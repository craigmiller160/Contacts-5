package io.craigmiller160.contacts5.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.view.View;

import static io.craigmiller160.contacts5.util.ContactsConstants.*;

/**
 * Created by craig on 5/4/16.
 */
public class AddContactController extends AbstractAndroidController implements View.OnClickListener {

    public AddContactController(Context context) {
        super(context);
    }

    @Override
    public void onClick(View view) {
        Snackbar.make(view, "Button is working", Snackbar.LENGTH_SHORT).show(); //TODO delete this

        //TODO review and restore this code
//        Intent intent = new Intent(Intent.ACTION_INSERT);
//        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
//        intent.putExtra(INTENT_KEY_FINISH_ACTIVITY_ON_SAVE_COMPLETED, true);
//        getActivity().startActivityForResult(intent, NEW_CONTACT_VIEW_ID);
    }
}
