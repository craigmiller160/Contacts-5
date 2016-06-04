package io.craigmiller160.contacts5.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

import io.craigmiller160.contacts5.R;
import io.craigmiller160.contacts5.activity.ContactsInGroupActivity;
import io.craigmiller160.contacts5.util.AbstractAndroidUtil;

import static io.craigmiller160.contacts5.util.ContactsConstants.SELECT_GROUP_ID;

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
        ((Activity) view.getContext()).startActivityForResult(intent, SELECT_GROUP_ID);
    }
}
