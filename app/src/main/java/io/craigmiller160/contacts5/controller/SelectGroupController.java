package io.craigmiller160.contacts5.controller;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import io.craigmiller160.contacts5.ContactsApp;
import io.craigmiller160.contacts5.R;
import io.craigmiller160.contacts5.fragment.FragmentChanger;
import io.craigmiller160.contacts5.model.AndroidModel;

import static io.craigmiller160.contacts5.util.ContactsConstants.CONTACTS_MODEL;
import static io.craigmiller160.contacts5.util.ContactsConstants.SELECTED_GROUP_ID;
import static io.craigmiller160.contacts5.util.ContactsConstants.SELECTED_GROUP_NAME;

/**
 * Created by craig on 6/3/16.
 */
public class SelectGroupController extends AbstractAndroidController implements View.OnClickListener {

    private AndroidModel contactsModel;

    public SelectGroupController(Context context) {
        super(context);
        contactsModel = ContactsApp.getApp().modelFactory().getModel(CONTACTS_MODEL);
    }

    @Override
    public void onClick(View view) {
        long groupId = getArg(getResources().getString(R.string.group_id), Long.class);
        String groupName = getArg(getResources().getString(R.string.group_name), String.class);

        contactsModel.setProperty(SELECTED_GROUP_ID, groupId);
        contactsModel.setProperty(SELECTED_GROUP_NAME, groupName);

        FragmentChanger.displayNoTabsFragment(((AppCompatActivity) view.getContext()).getSupportFragmentManager());
    }
}
