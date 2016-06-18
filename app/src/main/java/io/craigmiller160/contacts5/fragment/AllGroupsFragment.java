package io.craigmiller160.contacts5.fragment;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

import io.craigmiller160.contacts5.R;
import io.craigmiller160.contacts5.adapter.GroupsArrayAdapter;
import io.craigmiller160.contacts5.model.ContactGroup;

/**
 * Created by craig on 6/5/16.
 */
public class AllGroupsFragment extends AbstractContactsPageFragment<ContactGroup> {

    private static final String TAG = "AllGroupsFragment";

    public static final int PAGE_INDEX = 2;

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        Log.v(TAG, "AllGroupsFragment created");
    }

    @Override
    protected ArrayAdapter<ContactGroup> getArrayAdapter() {
        return new GroupsArrayAdapter(getContext());
    }

    @Override
    public int getPageIndex() {
        return PAGE_INDEX;
    }

    @Override
    public int getPageTitleResId() {
        return R.string.tab_groups;
    }
}
