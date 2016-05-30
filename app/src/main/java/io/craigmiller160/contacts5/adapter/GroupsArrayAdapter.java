package io.craigmiller160.contacts5.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import io.craigmiller160.contacts5.R;
import io.craigmiller160.contacts5.model.ContactGroup;

/**
 * Created by craig on 5/29/16.
 */
public class GroupsArrayAdapter extends ArrayAdapter<ContactGroup> {

    private List<ContactGroup> groups;

    public GroupsArrayAdapter(Activity activity){
        super(activity, R.layout.contact_group_row);
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

        if(groups != null){
            ContactGroup group = groups.get(position);
            if(group != null){
                TextView nameTextView = (TextView) view.findViewById(R.id.groupName);
                TextView accountTextView = (TextView) view.findViewById(R.id.accountName);
                nameTextView.setText(group.getGroupName() + " (" + group.getGroupSize() + ")");
                accountTextView.setText(group.getAccountName());
            }
        }

        return view;
    }

    @Override
    public int getCount(){
        return groups != null ? groups.size() : 0;
    }
}
