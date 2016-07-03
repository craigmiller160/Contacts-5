package io.craigmiller160.contacts5.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
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
        FrameLayout layout = (FrameLayout) inflater.inflate(R.layout.content_page, container, false);
        attachChildView(layout, inflater);

        return layout;
    }

    private void attachChildView(FrameLayout layout, LayoutInflater inflater){
        layout.removeAllViews();
        if(androidSystemUtil.permissions().hasReadContactsPermission()){
            ListView view = (ListView) inflater.inflate(R.layout.content_list, layout, false);
            view.setDivider(null);
            view.setFastScrollEnabled(true);
            view.setAdapter(arrayAdapter);
            layout.addView(view);
        }
        else{
            View view = inflater.inflate(R.layout.content_no_permissions, layout, false);
            layout.addView(view);
        }
    }

    public void refreshView(){
        FrameLayout layout = (FrameLayout) getView();
        attachChildView(layout, getActivity().getLayoutInflater());
    }

    public final ArrayAdapter<T> getArrayAdapter(){
        if(arrayAdapter == null){
            arrayAdapter = newArrayAdapter();
        }
        return arrayAdapter;
    }

    protected abstract ArrayAdapter<T> newArrayAdapter();

}
