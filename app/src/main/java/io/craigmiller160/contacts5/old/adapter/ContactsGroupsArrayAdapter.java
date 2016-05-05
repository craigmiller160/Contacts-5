package io.craigmiller160.contacts5.old.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import io.craigmiller160.contacts5.R;
import io.craigmiller160.contacts5.old.application.ContactsApplication;
import io.craigmiller160.contacts5.old.controller.GroupSelectionController;
import io.craigmiller160.contacts5.old.model.ContactGroup;

import static io.craigmiller160.contacts5.old.helper.ContactsHelper.*;

/**
 * Created by Craig on 2/3/2016.
 */
public class ContactsGroupsArrayAdapter extends ArrayAdapter<ContactGroup> {

    public ContactsGroupsArrayAdapter(Activity activity){
        super(activity, R.layout.contact_group_row);
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
        //final ContactGroup group = ContactsApplication.getInstance().getGroupAtIndex(position);
        ContactGroup group = (ContactGroup) ContactsApplication.getInstance().getModelProperty(GROUP_AT_INDEX_PROP, position);
        if(group != null){
            nameTextView.setText(group.getGroupName() + " (" + group.getGroupSize() + ")");
            accountTextView.setText(group.getAccountName());
        }

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

    @Override
    public int getCount(){
        return (Integer) ContactsApplication.getInstance().getModelProperty(GROUP_COUNT_PROP);
    }

}
