package io.craigmiller160.contacts5.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import io.craigmiller160.contacts5.ContactsApp;
import io.craigmiller160.contacts5.R;
import io.craigmiller160.contacts5.adapter.ContactsArrayAdapter;
import io.craigmiller160.contacts5.log.Logger;
import io.craigmiller160.contacts5.model.AndroidModel;
import io.craigmiller160.contacts5.model.Contact;
import io.craigmiller160.contacts5.service.ContactsService;
import io.craigmiller160.contacts5.util.AndroidSystemUtil;

/**
 * Created by craig on 6/5/16.
 */
public class ContactsInGroupFragment extends AbstractContactsFragment<Contact> {

    private static final String TAG = "ContactsInGroupFragment";
    private static final Logger logger = Logger.newLogger(TAG);

    private AndroidModel contactsModel;

    @Override
    protected ArrayAdapter<Contact> newArrayAdapter() {
        return new ContactsArrayAdapter(getContext(), R.string.prop_contacts_in_group_list);
    }

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        logger.v(TAG, "ContactsInGroupFragment created");
        this.contactsModel = ContactsApp.getApp().modelFactory().getModel(R.string.model_contacts);
        AndroidSystemUtil androidSystemUtil = new AndroidSystemUtil(getActivity());

        Long groupId = contactsModel.getProperty(R.string.prop_selected_group_id, Long.class);
        String groupName = contactsModel.getProperty(R.string.prop_selected_group_name, String.class);

        if(groupId != null && groupId >= 0 && androidSystemUtil.permissions().hasReadContactsPermission() && savedInstance == null){
            Intent intent = new Intent(getActivity(), ContactsService.class);
            intent.putExtra(ContactsService.LOAD_CONTACTS_IN_GROUP, true);
            intent.putExtra(getString(R.string.prop_selected_group_id), groupId);
            intent.putExtra(getString(R.string.prop_selected_group_name), groupName);

            getActivity().startService(intent);
        }
    }

}