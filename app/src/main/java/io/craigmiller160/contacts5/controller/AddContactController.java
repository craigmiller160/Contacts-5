package io.craigmiller160.contacts5.controller;

import android.app.Activity;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.view.View;

import io.craigmiller160.locus.annotations.LController;

import static io.craigmiller160.contacts5.util.ContactsConstants.*;

/**
 * Created by craig on 5/4/16.
 */
@LController(name=ADD_CONTACT_CONTROLLER)
public class AddContactController implements View.OnClickListener {

    @Override
    public void onClick(View view) {
        Snackbar.make(view, "Button is working", Snackbar.LENGTH_SHORT).show();

        //TODO review and restore this code
//        Intent intent = new Intent(Intent.ACTION_INSERT);
//        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
//        intent.putExtra(INTENT_KEY_FINISH_ACTIVITY_ON_SAVE_COMPLETED, true);
//        getActivity().startActivityForResult(intent, NEW_CONTACT_VIEW_ID);
    }
}
