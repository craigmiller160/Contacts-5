package io.craigmiller160.contacts5.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.util.Map;

import io.craigmiller160.contacts5.ContactsApp;
import io.craigmiller160.contacts5.IllegalArgumentCtxException;
import io.craigmiller160.contacts5.R;
import io.craigmiller160.contacts5.fragment.FragmentChanger;
import io.craigmiller160.contacts5.model.AndroidModel;

import static io.craigmiller160.contacts5.util.ContactsConstants.CONTACTS_MODEL;
import static io.craigmiller160.contacts5.util.ContactsConstants.DISPLAYED_FRAGMENT;
import static io.craigmiller160.contacts5.util.ContactsConstants.NEW_CONTACT_REQUEST;
import static io.craigmiller160.contacts5.util.ContactsConstants.SELECTED_GROUP_ID;
import static io.craigmiller160.contacts5.util.ContactsConstants.SELECTED_GROUP_NAME;
import static io.craigmiller160.contacts5.util.ContactsConstants.SELECT_CONTACT_REQUEST;
import static io.craigmiller160.contacts5.util.ContactsConstants.TABS_FRAGMENT_TAG;

/**
 * Created by craig on 6/18/16.
 */
public class OnClickController extends AbstractAndroidController implements View.OnClickListener {

    private static final String TAG = "OnClickController";

    public static final int CONTACTS_LIST = 501;
    public static final int GROUPS_LIST = 502;
    public static final int ADD_BUTTON = 503;

    private final int type;
    private final AndroidModel contactsModel;

    public OnClickController(Context context, Map<String,Object> args, int type){
        super(context, args);
        switch(type){
            case CONTACTS_LIST:
            case GROUPS_LIST:
            case ADD_BUTTON:
                this.type = type;
                break;
            default:
                throw new IllegalArgumentCtxException("Provided type not supported by this controller")
                        .addContextValue("Type", type);
        }
        this.contactsModel = ContactsApp.getApp().modelFactory().getModel(CONTACTS_MODEL);
    }

    @Override
    public void onClick(View view) {
        switch (type){
            case CONTACTS_LIST:
                onClickContact(view);
                break;
            case GROUPS_LIST:
                onClickGroup(view);
                break;
            case ADD_BUTTON:
                onClickAdd(view);
                break;
        }
    }

    private void onClickAdd(View view){
        Log.i(TAG, "Opening Add New Contact ActionView");
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
        ((Activity) view.getContext()).startActivityForResult(intent, NEW_CONTACT_REQUEST);
    }

    private void onClickContact(View view){
        String name = getArg(getString(R.string.contact_name), String.class);
        Uri uri = getArg(getString(R.string.contact_uri), Uri.class);

        Log.i(TAG, "Opening Edit Contact ActionView for contact: " + name);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);
        ((Activity) view.getContext()).startActivityForResult(intent, SELECT_CONTACT_REQUEST);
    }

    private void onClickGroup(View view){
        long groupId = getArg(getString(R.string.group_id), Long.class);
        String groupName = getArg(getString(R.string.group_name), String.class);

        Log.i(TAG, "Opening group: " + groupName);

        contactsModel.setProperty(SELECTED_GROUP_ID, groupId);
        contactsModel.setProperty(SELECTED_GROUP_NAME, groupName);

        if(contactsModel.getProperty(DISPLAYED_FRAGMENT) == null ||
                contactsModel.getProperty(DISPLAYED_FRAGMENT, String.class).equals(TABS_FRAGMENT_TAG)){
            FragmentChanger.displayNoTabsFragment(((AppCompatActivity) view.getContext()).getSupportFragmentManager());
        }
    }
}
