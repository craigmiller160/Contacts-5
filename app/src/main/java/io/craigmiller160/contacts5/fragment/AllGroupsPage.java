package io.craigmiller160.contacts5.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.List;

import io.craigmiller160.contacts5.R;
import io.craigmiller160.contacts5.adapter.GroupsArrayAdapter;
import io.craigmiller160.contacts5.model.ContactGroup;
import io.craigmiller160.contacts5.service.PermissionsService;
import io.craigmiller160.contacts5.service.ServiceFactory;

/**
 * Created by craig on 5/29/16.
 */
public class AllGroupsPage extends Fragment{

    private List<ContactGroup> groups;
    private PermissionsService permissionsService;
    private GroupsArrayAdapter groupsArrayAdapter;

    private static final String TAG = "AllGroupsPage";

    public AllGroupsPage(){
        this.permissionsService = ServiceFactory.getInstance().getPermissionsService();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        groupsArrayAdapter = new GroupsArrayAdapter(getActivity());
    }

    public void setGroupsList(List<ContactGroup> groups){
        this.groups = groups;
        if(groupsArrayAdapter != null){
            groupsArrayAdapter.setGroupsList(groups);
        }
    }

    public List<ContactGroup> getGroupsList(){
        return groups;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance){
        if(permissionsService.hasReadContactsPermission()){
            ListView view = (ListView) inflater.inflate(R.layout.content_contacts_list, container, false);
            view.setDivider(null);
            view.setFastScrollEnabled(true);


            if(groups != null){
                groupsArrayAdapter.setGroupsList(groups);
            }

            view.setAdapter(groupsArrayAdapter);

            return view;
        }
        else{
            Log.d(TAG, "Displaying no permissions page on Groups tab");
            View view = inflater.inflate(R.layout.content_no_permissions, container, false);
            return view;
        }
    }

}
