package io.craigmiller160.contacts5.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

import io.craigmiller160.contacts5.R;

import static io.craigmiller160.contacts5.util.ContactsConstants.SELECT_CONTACT_ID;

/**
 * Created by craig on 5/30/16.
 */
public class SelectContactController extends AbstractAndroidController implements View.OnClickListener{

    public SelectContactController(Context context) {
        super(context);
    }

    @Override
    public void onClick(View view) {
        Uri uri = getArg(getResources().getString(R.string.contact_uri), Uri.class);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);
        ((Activity) view.getContext()).startActivityForResult(intent, SELECT_CONTACT_ID);
    }
}
