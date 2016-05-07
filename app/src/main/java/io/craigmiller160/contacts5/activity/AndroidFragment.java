package io.craigmiller160.contacts5.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import io.craigmiller160.contacts5.view.AndroidActivityView;
import io.craigmiller160.contacts5.view.AndroidFragmentView;

/**
 * Created by craig on 5/7/16.
 */
public abstract class AndroidFragment extends Fragment {

    protected abstract AndroidFragmentView getAndroidView();

    private AndroidFragmentView androidView;

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        this.androidView = getAndroidView();
    }

    @Override
    public final void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        if(androidView != null){
            androidView.onCreateOptionsMenu(menu);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public final void onPrepareOptionsMenu(Menu menu){
        if(androidView != null){
            androidView.onPrepareOptionsMenu(menu);
        }
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public final void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo){
        if(androidView != null){
            androidView.onCreateContextMenu(menu, view, menuInfo);
        }
        super.onCreateContextMenu(menu, view, menuInfo);
    }

}
