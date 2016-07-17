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
 * Created by craig on 6/12/16.
 */
public class FavContactsFragment extends AbstractContactsPageFragment<Contact> {

    private static final String TAG = "FavContactsFragment";
    private static final Logger logger = Logger.newLogger(TAG);

    public static final int PAGE_INDEX = 0;

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        logger.v(TAG, "FavContactsFragment created");
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
