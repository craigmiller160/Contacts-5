package io.craigmiller160.contacts5.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import io.craigmiller160.contacts5.R;
import io.craigmiller160.contacts5.controller.ControllerFactory;
import io.craigmiller160.contacts5.model.ContactGroup;
import io.craigmiller160.contacts5.service.ContactIconService;
import io.craigmiller160.contacts5.service.ServiceFactory;

import static io.craigmiller160.contacts5.util.ContactsConstants.SELECT_CONTACT_CONTROLLER;
import static io.craigmiller160.contacts5.util.ContactsConstants.SELECT_GROUP_CONTROLLER;

/**
 * Created by craig on 5/29/16.
 */
public class GroupsArrayAdapter extends ArrayAdapter<ContactGroup> {

    private List<ContactGroup> groups;

    private ContactIconService contactIconService;

    public GroupsArrayAdapter(Activity activity){
        super(activity, R.layout.contact_group_row);
        contactIconService = ServiceFactory.getInstance().getContactIconService();
    }

    public void setGroupsList(List<ContactGroup> groups){
        this.groups = groups;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if(view == null){
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.contact_group_row, parent, false);
        }

        if(groups != null && groups.get(position) != null){
            ContactGroup group = groups.get(position);
            TextView nameTextView = (TextView) view.findViewById(R.id.groupName);
            TextView accountTextView = (TextView) view.findViewById(R.id.accountName);
            nameTextView.setText(group.getGroupName() + " (" + group.getGroupSize() + ")");
            accountTextView.setText(group.getAccountName());
            view.setTag(R.string.group_id, group.getGroupId());
            view.setTag(R.string.group_name, group.getGroupName());

            Drawable groupIcon = contactIconService.createContactIcon(group.getGroupName() + group.getAccountName(), group.getGroupName().charAt(0));

            ImageView groupIconView = (ImageView) view.findViewById(R.id.group_icon);
            groupIconView.setImageDrawable(groupIcon);

            view.setOnClickListener(ControllerFactory.getInstance().getController(SELECT_GROUP_CONTROLLER, View.OnClickListener.class));
        }

        return view;
    }

    @Override
    public int getCount(){
        return groups != null ? groups.size() : 0;
    }
}
