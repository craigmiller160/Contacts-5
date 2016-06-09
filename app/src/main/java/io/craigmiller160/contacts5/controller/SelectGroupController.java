package io.craigmiller160.contacts5.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

import io.craigmiller160.contacts5.ContactsApp;
import io.craigmiller160.contacts5.R;
import io.craigmiller160.contacts5.activity.ContactsInGroupActivity;
import io.craigmiller160.contacts5.fragment.ContactsInGroupFragment;
import io.craigmiller160.contacts5.fragment.TabsFragment;
import io.craigmiller160.contacts5.service.ContactsRetrievalService;

import static io.craigmiller160.contacts5.util.ContactsConstants.SELECT_GROUP_REQUEST;

/**
 * Created by craig on 6/3/16.
 */
public class SelectGroupController extends AbstractAndroidController implements View.OnClickListener {

    private final ContactsRetrievalService contactsService;

    public SelectGroupController(Context context) {
        super(context);
        this.contactsService = ContactsApp.getApp().serviceFactory().getContactsRetrievalService();
    }

    @Override
    public void onClick(View view) {
        long groupId = getArg(getResources().getString(R.string.group_id), Long.class);
        String groupName = getArg(getResources().getString(R.string.group_name), String.class);
//        Intent intent = new Intent(getContext(), ContactsInGroupActivity.class);
//        intent.putExtra(getResources().getString(R.string.group_id), groupId);
//        intent.putExtra(getResources().getString(R.string.group_name), groupName);
//        ((Activity) view.getContext()).startActivityForResult(intent, SELECT_GROUP_REQUEST);

        AppCompatActivity activity = (AppCompatActivity) view.getContext();

        FragmentManager fm = activity.getSupportFragmentManager();
        Fragment tabsFragment = fm.findFragmentByTag("TabsFragment");
        fm.beginTransaction().remove(tabsFragment).commit();

        View viewPager = activity.findViewById(R.id.contactsTabsViewPager);
        viewPager.setVisibility(View.GONE);

        activity.getSupportFragmentManager().beginTransaction().replace(R.id.list_fragment_container, new ContactsInGroupFragment()).commit();

        contactsService.loadAllContactsInGroup(groupId);

    }
}
