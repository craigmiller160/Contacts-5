package io.craigmiller160.contacts5.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import io.craigmiller160.contacts5.ContactsApp;
import io.craigmiller160.contacts5.R;
import io.craigmiller160.contacts5.adapter.ContactsArrayAdapter;
import io.craigmiller160.contacts5.model.Contact;

import static io.craigmiller160.contacts5.util.ContactsConstants.CONTACTS_IN_GROUP_LIST;
import static io.craigmiller160.contacts5.util.ContactsConstants.CONTACTS_MODEL;
import static io.craigmiller160.contacts5.util.ContactsConstants.SELECTED_GROUP_NAME;

/**
 * Created by craig on 6/5/16.
 */
public class ContactsInGroupFragment extends AbstractContactsFragment<Contact> {

    @Override
    protected ArrayAdapter<Contact> getArrayAdapter() {
        return new ContactsArrayAdapter(getContext(), CONTACTS_IN_GROUP_LIST);
    }

}