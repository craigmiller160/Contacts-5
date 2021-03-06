/*
 * Copyright 2016 ShadowAngler <craigmiller160@gmail.com> - All Rights Reserved
 * Proprietary / Confidential
 * Unauthorized copying, use, or redistribution of this file is prohibited
 */

package io.craigmiller160.contacts5.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import io.craigmiller160.contacts5.R;
import io.craigmiller160.contacts5.controller.OnClickController;
import io.craigmiller160.contacts5.model.ContactGroup;
import io.craigmiller160.contacts5.util.ContactIconProvider;

/**
 * Created by craig on 5/29/16.
 */
public class GroupsArrayAdapter extends MyArrayAdapter<ContactGroup>{

    private final ContactIconProvider contactIconProvider;

    public GroupsArrayAdapter(Context context){
        super(context, R.layout.group_row, R.string.prop_groups_list);
        contactIconProvider = new ContactIconProvider(context);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if(view == null){
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.group_row, parent, false);
        }

        if(getContents() != null && getContents().get(position) != null){
            ContactGroup group = getContents().get(position);
            TextView nameTextView = (TextView) view.findViewById(R.id.groupName);
            TextView accountTextView = (TextView) view.findViewById(R.id.accountName);
            nameTextView.setText(group.getGroupName() + " (" + group.getGroupSize() + ")");
            accountTextView.setText(group.getAccountName());

            Drawable groupIcon = contactIconProvider.createSquareContactIcon(group.getGroupName() + group.getAccountName(), group.getGroupName().charAt(0));

            ImageView groupIconView = (ImageView) view.findViewById(R.id.group_icon);
            groupIconView.setImageDrawable(groupIcon);

            OnClickController onClickController = new OnClickController(getContext());
            onClickController.addArg(R.string.on_click_controller_type, OnClickController.GROUPS_LIST);
            onClickController.addArg(R.string.prop_selected_group_id, group.getGroupId());
            onClickController.addArg(R.string.prop_selected_group_name, group.getGroupName());

            view.setOnClickListener(onClickController);
        }

        return view;
    }
}
