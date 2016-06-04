package io.craigmiller160.contacts5.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.view.View;

import io.craigmiller160.contacts5.util.AbstractAndroidUtil;

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
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
        ((Activity) view.getContext()).startActivityForResult(intent, NEW_CONTACT_ID);
    }
}
