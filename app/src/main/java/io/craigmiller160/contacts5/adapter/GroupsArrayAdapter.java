package io.craigmiller160.contacts5.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.craigmiller160.contacts5.R;
import io.craigmiller160.contacts5.controller.ControllerFactory;
import io.craigmiller160.contacts5.model.ContactGroup;
import io.craigmiller160.contacts5.model.ModelFactory;
import io.craigmiller160.contacts5.service.ContactIconService;
import io.craigmiller160.contacts5.service.ServiceFactory;

import static io.craigmiller160.contacts5.util.ContactsConstants.CONTACTS_MODEL;
import static io.craigmiller160.contacts5.util.ContactsConstants.GROUPS_LIST;
import static io.craigmiller160.contacts5.util.ContactsConstants.SELECT_GROUP_CONTROLLER;

/**
 * Created by craig on 5/29/16.
 */
public class GroupsArrayAdapter extends ArrayAdapter<ContactGroup> implements PropertyChangeListener {

    private List<ContactGroup> groups;

    private ContactIconService contactIconService;

    public GroupsArrayAdapter(Activity activity){
        super(activity, R.layout.group_row);
        contactIconService = ServiceFactory.getInstance().getContactIconService();
        ModelFactory.getInstance().getModel(CONTACTS_MODEL).addPropertyChangeListener(this);
    }

    public void setGroupsList(final List<ContactGroup> groups){
        if(Looper.myLooper() == Looper.getMainLooper()){
            this.groups = groups;
            notifyDataSetChanged();
        }
        else{
            Handler h = new Handler(Looper.getMainLooper());
            h.post(new Runnable() {
                @Override
                public void run() {
                    GroupsArrayAdapter.this.groups = groups;
                    notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if(view == null){
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.group_row, parent, false);
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

            Map<String,Object> args = new HashMap<>();
            args.put(getContext().getString(R.string.group_id), group.getGroupId());
            args.put(getContext().getString(R.string.group_name), group.getGroupName());

            view.setOnClickListener(ControllerFactory.getInstance().getController(SELECT_GROUP_CONTROLLER, View.OnClickListener.class, args));
        }

        return view;
    }

    @Override
    public int getCount(){
        return groups != null ? groups.size() : 0;
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        if(event.getPropertyName().equals(GROUPS_LIST)){
            setGroupsList((List<ContactGroup>) event.getNewValue());
        }
    }
}
