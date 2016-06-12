package io.craigmiller160.contacts5.fragment;

import android.widget.ArrayAdapter;

import io.craigmiller160.contacts5.adapter.ContactsArrayAdapter;
import io.craigmiller160.contacts5.model.Contact;

import static io.craigmiller160.contacts5.util.ContactsConstants.CONTACTS_LIST;

/**
 * Created by craig on 6/5/16.
 */
public class AllContactsFragment extends AbstractContactsPageFragment<Contact> {

    private static final String pageTitle = "All Contacts";

    @Override
    protected ArrayAdapter<Contact> getArrayAdapter() {
        return new ContactsArrayAdapter(getContext(), CONTACTS_LIST);
    }

    @Override
    public String getPageTitle() {
        return pageTitle;
    }
}
