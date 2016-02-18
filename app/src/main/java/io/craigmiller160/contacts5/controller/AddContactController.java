package io.craigmiller160.contacts5.controller;

import android.app.Activity;
import android.content.Intent;
import android.provider.ContactsContract;
import android.view.View;

import static io.craigmiller160.contacts5.activity.ContactsActivity.*;

import io.craigmiller160.contacts5.helper.Helper;

/**
 * Created by Craig on 2/17/2016.
 */
public class AddContactController extends AbstractAndroidController implements View.OnClickListener {

    //TODO add logging here

    public AddContactController() {
    }

    public AddContactController(Helper helper) {
        super(helper);
    }

    public AddContactController(Activity activity) {
        super(activity);
    }

    public AddContactController(Activity activity, Helper helper) {
        super(activity, helper);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
        intent.putExtra(INTENT_KEY_FINISH_ACTIVITY_ON_SAVE_COMPLETED, true);
        getActivity().startActivityForResult(intent, NEW_CONTACT_VIEW_ID);
    }
}
