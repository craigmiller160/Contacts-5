package io.craigmiller160.contacts5.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import io.craigmiller160.contacts5.R;
import io.craigmiller160.contacts5.adapter.ContactsArrayAdapter;
import io.craigmiller160.contacts5.service.PermissionsService;
import io.craigmiller160.contacts5.service.ServiceFactory;

/**
 * Created by craig on 6/5/16.
 */
public abstract class AbstractContactsFragment<T> extends Fragment {

    private static final String PROPERTY_NAME = "PropertyName";

    private PermissionsService permissionsService;
    private ArrayAdapter<T> arrayAdapter;

    protected AbstractContactsFragment(){
        this.permissionsService = ServiceFactory.getInstance().getPermissionsService();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        arrayAdapter = getArrayAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance){
        if(permissionsService.hasReadContactsPermission()){
            ListView view = (ListView) inflater.inflate(R.layout.content_list, container, false);
            view.setDivider(null);
            view.setFastScrollEnabled(true);
            view.setAdapter(arrayAdapter);

            return view;
        }
        else{
            View view = inflater.inflate(R.layout.content_no_permissions, container, false);
            return view;
        }
    }

    protected abstract ArrayAdapter<T> getArrayAdapter();

}
