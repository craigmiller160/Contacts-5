/*
 * Copyright 2016 ShadowAngler <craigmiller160@gmail.com> - All Rights Reserved
 * Proprietary / Confidential
 * Unauthorized copying, use, or redistribution of this file is prohibited
 */

package io.craigmiller160.contacts5.fragment;

import android.os.Bundle;
import android.widget.ArrayAdapter;

import io.craigmiller160.contacts5.R;
import io.craigmiller160.contacts5.adapter.ContactsArrayAdapter;
import io.craigmiller160.contacts5.log.Logger;
import io.craigmiller160.contacts5.model.Contact;

/**
 * Created by craig on 6/5/16.
 */
public class AllContactsFragment extends AbstractContactsPageFragment<Contact> {

    private static final String TAG = "AllContactsFragment";
    private static final Logger logger = Logger.newLogger(TAG);

    public static final int PAGE_INDEX = 1;

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        logger.v(TAG, "AllContactsFragment created");
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
