package io.craigmiller160.contacts5.controller;

import android.content.Intent;
import android.util.Log;
import android.view.View;

import io.craigmiller160.contacts5.activity.ContactsInGroupActivity;
import io.craigmiller160.contacts5.model.ContactGroup;

/**
 * Created by Craig on 2/6/2016.
 */
public class GroupSelectionController implements View.OnClickListener{

    private ContactGroup group;

    private static final String TAG = "GroupSelectController";

    public GroupSelectionController(ContactGroup group){
        this.group = group;
    }


    @Override
    public void onClick(View view) {
        Log.i(TAG, "Opening contacts list for group. Group: " + group.getGroupName());
        Intent intent = new Intent(view.getContext(), ContactsInGroupActivity.class);
        intent.putExtra(ContactGroup.GROUP_ID_PROP, group.getGroupId());
        intent.putExtra(ContactGroup.GROUP_NAME_PROP, group.getGroupName());
        view.getContext().startActivity(intent);
    }
}
