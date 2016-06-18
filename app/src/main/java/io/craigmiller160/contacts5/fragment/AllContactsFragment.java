package io.craigmiller160.contacts5.fragment;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

import io.craigmiller160.contacts5.R;
import io.craigmiller160.contacts5.adapter.ContactsArrayAdapter;
import io.craigmiller160.contacts5.model.Contact;

import static io.craigmiller160.contacts5.util.ContactsConstants.CONTACTS_LIST;

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
    protected ArrayAdapter<Contact> getArrayAdapter() {
        return new ContactsArrayAdapter(getContext(), CONTACTS_LIST);
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
