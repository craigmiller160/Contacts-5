package io.craigmiller160.contacts5.fragment;

import android.widget.ArrayAdapter;

import io.craigmiller160.contacts5.adapter.GroupsArrayAdapter;
import io.craigmiller160.contacts5.model.ContactGroup;

/**
 * Created by craig on 6/5/16.
 */
public class AllGroupsFragment extends AbstractContactsPageFragment<ContactGroup> {

    private static final String pageTitle = "Groups";

    @Override
    protected ArrayAdapter<ContactGroup> getArrayAdapter() {
        return new GroupsArrayAdapter(getContext());
    }

    @Override
    public String getPageTitle() {
        return pageTitle;
    }
}
