package io.craigmiller160.contacts5.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import io.craigmiller160.contacts5.R;
import io.craigmiller160.contacts5.activity.ContactsInGroupActivity;
import io.craigmiller160.contacts5.controller.GroupSelectionController;
import io.craigmiller160.contacts5.model.ContactGroup;

/**
 * Created by Craig on 2/3/2016.
 */
public class ContactsGroupsArrayAdapter extends ArrayAdapter<ContactGroup> {

    private List<ContactGroup> groupsList;

    public ContactsGroupsArrayAdapter(Activity activity, List<ContactGroup> groupsList){
        super(activity, R.layout.contact_group_row, groupsList);
        this.groupsList = groupsList;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent){
        if(view == null){
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.contact_group_row, parent, false);
        }

        TextView nameTextView = (TextView) view.findViewById(R.id.groupName);
        TextView accountTextView = (TextView) view.findViewById(R.id.accountName);
        final ContactGroup group = groupsList.get(position);
        nameTextView.setText(group.getGroupName() + " (" + group.getGroupSize() + ")");
        accountTextView.setText(group.getGroupAccountName());
//        view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getContext(), ContactsInGroupActivity.class);
//                intent.putExtra("GroupId", group.getGroupId());
//                intent.putExtra("GroupName", group.getGroupName());
//                getContext().startActivity(intent);
//            }
//        });
        view.setOnClickListener(new GroupSelectionController(group));

        return view;
    }

}
