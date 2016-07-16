package io.craigmiller160.contacts5.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

import io.craigmiller160.contacts5.ContactsApp;
import io.craigmiller160.contacts5.R;
import io.craigmiller160.contacts5.activity.ContactsActivityViewChanger;
import io.craigmiller160.contacts5.fragment.FragmentChanger;
import io.craigmiller160.contacts5.model.AndroidModel;

import static io.craigmiller160.contacts5.util.ContactsConstants.NEW_CONTACT_REQUEST;
import static io.craigmiller160.contacts5.util.ContactsConstants.SELECT_CONTACT_REQUEST;

/**
 * Created by craig on 6/18/16.
 */
public class OnClickController extends AbstractAndroidController implements View.OnClickListener {

    private static final String TAG = "OnClickController";

    public static final int CONTACTS_LIST = 501;
    public static final int GROUPS_LIST = 502;
    public static final int ADD_BUTTON = 503;

    private final AndroidModel contactsModel;
    private final FragmentChanger fragmentChanger;
    private final ContactsActivityViewChanger viewChanger;

    public OnClickController(Context context){
        this(context, new HashMap<String, Object>());
    }

    public OnClickController(Context context, Map<String,Object> args){
        super(context, args);
        this.contactsModel = ContactsApp.getApp().modelFactory().getModel(R.string.model_contacts);
        this.fragmentChanger = new FragmentChanger(context);
        this.viewChanger = ContactsActivityViewChanger.getInstance();
    }

    @Override
    public void onClick(View view) {
        int type = getArg(R.string.on_click_controller_type, Integer.class);
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
        long groupId = getArg(getString(R.string.prop_selected_group_id), Long.class);
        String groupName = getArg(getString(R.string.prop_selected_group_name), String.class);

        Log.i(TAG, "Opening group: " + groupName);

        contactsModel.setProperty(R.string.prop_selected_group_id, groupId);
        contactsModel.setProperty(R.string.prop_selected_group_name, groupName);

        contactsModel.setProperty(R.string.prop_displayed_fragment, getString(R.string.tag_no_tabs_fragment));
        fragmentChanger.addNoTabsFragment(((AppCompatActivity) view.getContext()).getSupportFragmentManager());
    }
}
