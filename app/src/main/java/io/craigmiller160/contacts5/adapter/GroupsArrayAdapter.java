package io.craigmiller160.contacts5.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import io.craigmiller160.contacts5.ContactsApp;
import io.craigmiller160.contacts5.R;
import io.craigmiller160.contacts5.controller.OnClickController;
import io.craigmiller160.contacts5.model.ContactGroup;
import io.craigmiller160.contacts5.util.ContactIconProvider;

import static io.craigmiller160.contacts5.util.ContactsConstants.GROUPS_LIST;
import static io.craigmiller160.contacts5.util.ContactsConstants.SELECT_GROUP_CONTROLLER;

/**
 * Created by craig on 5/29/16.
 */
public class GroupsArrayAdapter extends MyArrayAdapter<ContactGroup>{

    private final ContactIconProvider contactIconProvider;

    public GroupsArrayAdapter(Context context){
        super(context, R.layout.group_row, GROUPS_LIST);
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
            view.setTag(R.string.group_id, group.getGroupId());
            view.setTag(R.string.group_name, group.getGroupName());

            Drawable groupIcon = contactIconProvider.createSquareContactIcon(group.getGroupName() + group.getAccountName(), group.getGroupName().charAt(0));

            ImageView groupIconView = (ImageView) view.findViewById(R.id.group_icon);
            groupIconView.setImageDrawable(groupIcon);

            Map<String,Object> args = new HashMap<>();
            args.put(getContext().getString(R.string.group_id), group.getGroupId());
            args.put(getContext().getString(R.string.group_name), group.getGroupName());

            view.setOnClickListener(new OnClickController(getContext(), args, OnClickController.GROUPS_LIST));
        }

        return view;
    }
}
