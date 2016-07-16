package io.craigmiller160.contacts5.fragment;

import android.os.Bundle;
import android.widget.ArrayAdapter;

import io.craigmiller160.contacts5.R;
import io.craigmiller160.contacts5.log.Logger;
import io.craigmiller160.contacts5.model.Contact;

/**
 * Created by craig on 7/16/16.
 */
public class SearchFragment extends AbstractContactsFragment<Contact> {

    private static final String TAG = "SearchFragment";
    private static final Logger logger = Logger.newLogger(TAG);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logger.v(TAG, "Creating SearchFragment");
    }

    @Override
    protected ArrayAdapter<Contact> newArrayAdapter() {
        return new ArrayAdapter<>(getContext(), R.layout.content_list);
    }
}
