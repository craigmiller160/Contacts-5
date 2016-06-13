package io.craigmiller160.contacts5.fragment;

import android.widget.ArrayAdapter;

import io.craigmiller160.contacts5.adapter.ContactsArrayAdapter;
import io.craigmiller160.contacts5.model.Contact;

import static io.craigmiller160.contacts5.util.ContactsConstants.*;

/**
 * Created by craig on 6/12/16.
 */
public class FavContactsFragment extends AbstractContactsPageFragment<Contact> {

    public static final int PAGE_INDEX = 0;

    private static final String pageTitle = "Favorites";

    @Override
    public String getPageTitle() {
        return pageTitle;
    }

    @Override
    public int getPageIndex() {
        return PAGE_INDEX;
    }

    @Override
    protected ArrayAdapter<Contact> getArrayAdapter() {
        return new ContactsArrayAdapter(getContext(), FAVORITES_LIST);
    }
}