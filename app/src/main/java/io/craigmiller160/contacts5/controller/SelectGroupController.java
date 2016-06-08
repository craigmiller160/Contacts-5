package io.craigmiller160.contacts5.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import io.craigmiller160.contacts5.ContactsApp;
import io.craigmiller160.contacts5.R;
import io.craigmiller160.contacts5.activity.ContactsInGroupActivity;
import io.craigmiller160.contacts5.fragment.ContactsInGroupFragment;
import io.craigmiller160.contacts5.fragment.TabsFragment;
import io.craigmiller160.contacts5.service.ContactsRetrievalService;

import static io.craigmiller160.contacts5.util.ContactsConstants.CONTACTS_MODEL;
import static io.craigmiller160.contacts5.util.ContactsConstants.DISPLAYED_FRAGMENT;
import static io.craigmiller160.contacts5.util.ContactsConstants.SINGLE_FRAGMENT_TAG;
import static io.craigmiller160.contacts5.util.ContactsConstants.SELECTED_GROUP_ID;
import static io.craigmiller160.contacts5.util.ContactsConstants.SELECTED_GROUP_NAME;
import static io.craigmiller160.contacts5.util.ContactsConstants.*;

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
        Intent intent = new Intent(getContext(), ContactsInGroupActivity.class);
        intent.putExtra(getResources().getString(R.string.group_id), groupId);
        intent.putExtra(getResources().getString(R.string.group_name), groupName);
        ((Activity) view.getContext()).startActivityForResult(intent, SELECT_GROUP_REQUEST);
    }
}
