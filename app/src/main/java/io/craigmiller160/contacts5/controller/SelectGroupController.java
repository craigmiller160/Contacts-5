package io.craigmiller160.contacts5.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Display;
import android.view.View;

import io.craigmiller160.contacts5.ContactsApp;
import io.craigmiller160.contacts5.R;
import io.craigmiller160.contacts5.activity.ContactsInGroupActivity;
import io.craigmiller160.contacts5.model.ModelFactory;

import static io.craigmiller160.contacts5.util.ContactsConstants.CONTACTS_MODEL;
import static io.craigmiller160.contacts5.util.ContactsConstants.SELECTED_GROUP_ID;
import static io.craigmiller160.contacts5.util.ContactsConstants.SELECTED_GROUP_NAME;
import static io.craigmiller160.contacts5.util.ContactsConstants.SELECT_GROUP_REQUEST;

/**
 * Created by craig on 6/3/16.
 */
public class SelectGroupController extends AbstractAndroidController implements View.OnClickListener {

    public SelectGroupController(Context context) {
        super(context);
    }

    @Override
    public void onClick(View view) {
        long groupId = getArg(getResources().getString(R.string.group_id), Long.class);
        String groupName = getArg(getResources().getString(R.string.group_name), String.class);

        ContactsApp.getApp().modelFactory().getModel(CONTACTS_MODEL).setProperty(SELECTED_GROUP_ID, groupId);
        ContactsApp.getApp().modelFactory().getModel(CONTACTS_MODEL).setProperty(SELECTED_GROUP_NAME, groupName);

        Intent intent = new Intent(getContext(), ContactsInGroupActivity.class);
        intent.putExtra(getResources().getString(R.string.group_id), groupId);
        intent.putExtra(getResources().getString(R.string.group_name), groupName);
        ((Activity) view.getContext()).startActivityForResult(intent, SELECT_GROUP_REQUEST);
    }
}
