package io.craigmiller160.contacts5.view;

import android.app.Fragment;
import android.preference.PreferenceFragment;
import android.util.AndroidRuntimeException;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.View;

/**
 * Created by craig on 5/7/16.
 */
public abstract class AndroidFragmentView {

    private final Fragment fragment;

    protected AndroidFragmentView(Fragment fragment){
        this.fragment = fragment;
        if(fragment instanceof PreferenceFragment){
            ((PreferenceFragment) fragment).addPreferencesFromResource(getViewResourceId());
        }
    }

    public Fragment getFragment(){
        return fragment;
    }

    protected final View findViewById(int id){
        if(fragment.getView() == null){
            throw new AndroidRuntimeException("No default view set to search for view IDs on the current fragment");
        }
        return fragment.getView().findViewById(id);
    }

    protected abstract int getViewResourceId();

    public abstract void onCreateOptionsMenu(Menu menu);

    public abstract void onPrepareOptionsMenu(Menu menu);

    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo){
        //Do nothing unless overridden by subclass
    }

}
