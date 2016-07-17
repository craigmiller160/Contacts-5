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
public class ContactsInGroupFragment extends AbstractContactsFragment<Contact> {

    private static final String TAG = "ContactsInGroupFragment";
    private static final Logger logger = Logger.newLogger(TAG);

    @Override
    protected ArrayAdapter<Contact> newArrayAdapter() {
        return new ContactsArrayAdapter(getContext(), R.string.prop_contacts_in_group_list);
    }

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        logger.v(TAG, "ContactsInGroupFragment created");
    }

}