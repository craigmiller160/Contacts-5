package io.craigmiller160.contacts5.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import io.craigmiller160.contacts5.R;
import io.craigmiller160.contacts5.util.AndroidSystemUtil;

/**
 * Created by craig on 6/5/16.
 */
public abstract class AbstractContactsFragment<T> extends Fragment {

    private AndroidSystemUtil androidSystemUtil;
    private ArrayAdapter<T> arrayAdapter;

    protected AbstractContactsFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.arrayAdapter = getArrayAdapter();
        this.androidSystemUtil = new AndroidSystemUtil(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance){
        if(androidSystemUtil.permissions().hasReadContactsPermission()){
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
