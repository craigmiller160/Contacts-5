package io.craigmiller160.contacts5.fragment;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

import io.craigmiller160.contacts5.R;
import io.craigmiller160.contacts5.adapter.ContactsArrayAdapter;
import io.craigmiller160.contacts5.model.Contact;

/**
 * Created by craig on 6/12/16.
 */
public class FavContactsFragment extends AbstractContactsPageFragment<Contact> {

    private static final String TAG = "FavContactsFragment";

    public static final int PAGE_INDEX = 0;

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        Log.v(TAG, "FavContactsFragment created");
    }

    @Override
    public int getPageIndex() {
        return PAGE_INDEX;
    }

    @Override
    protected ArrayAdapter<Contact> newArrayAdapter() {
        return new ContactsArrayAdapter(getContext(), R.string.prop_favorites_list);
    }

    @Override
    public int getPageTitleResId() {
        return R.string.tab_favorites;
    }
}
