package io.craigmiller160.contacts5.fragment;

import android.widget.ArrayAdapter;

import io.craigmiller160.contacts5.adapter.ContactsArrayAdapter;
import io.craigmiller160.contacts5.model.Contact;

import static io.craigmiller160.contacts5.util.ContactsConstants.*;

/**
 * Created by craig on 6/12/16.
 */
public class FavContactsFragment extends AbstractContactsPageFragment<Contact> {

    private static final String pageTitle = "Favorites";

    @Override
    public String getPageTitle() {
        return pageTitle;
    }

    @Override
    protected ArrayAdapter<Contact> getArrayAdapter() {
        return new ContactsArrayAdapter(getContext(), FAVORITES_LIST);
    }
}
