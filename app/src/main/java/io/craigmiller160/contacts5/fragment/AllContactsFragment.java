package io.craigmiller160.contacts5.fragment;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

import io.craigmiller160.contacts5.R;
import io.craigmiller160.contacts5.adapter.ContactsArrayAdapter;
import io.craigmiller160.contacts5.model.Contact;

/**
 * Created by craig on 6/5/16.
 */
public class AllContactsFragment extends AbstractContactsPageFragment<Contact> {

    private static final String TAG = "AllContactsFragment";

    public static final int PAGE_INDEX = 1;

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        Log.v(TAG, "AllContactsFragment created");
    }

    @Override
    protected ArrayAdapter<Contact> newArrayAdapter() {
        return new ContactsArrayAdapter(getContext(), R.string.prop_contacts_list);
    }

    @Override
    public int getPageIndex() {
        return PAGE_INDEX;
    }


    @Override
    public int getPageTitleResId(){
        return R.string.tab_contacts;
    }
}
