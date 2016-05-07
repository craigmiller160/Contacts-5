package io.craigmiller160.contacts5.view;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.View;

/**
 * Created by craig on 5/4/16.
 */
public abstract class AndroidActivityView {

    private final Activity activity;

    protected AndroidActivityView(Activity activity){
        this.activity = activity;
        activity.setContentView(getViewResourceId());
        onCreate();
    }

    protected final Activity getActivity(){
        return activity;
    }

    protected final AppCompatActivity getAppCompatActivity(){
        if(activity instanceof AppCompatActivity){
            return (AppCompatActivity) activity;
        }
        throw new IllegalStateException(String.format("Activity field in %s is not an instance of AppCompatActivity", this.getClass().getName()));
    }

    protected final View findViewById(int id){
        return activity.findViewById(id);
    }

    //TODO all methods used in Activity wrapper class, super should be called afterwards

    protected abstract void onCreate();

    protected abstract int getViewResourceId();

    public abstract boolean onCreateOptionsMenu(Menu menu);

    public abstract boolean onPrepareOptionsMenu(Menu menu);

    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo){
        //Do nothing unless overridden by subclass
    }

    public boolean onCreatePanelMenu(int featureId, Menu menu){
        //Do nothing unless overridden by subclass
        return true;
    }

    public void onCreatePanelView(int featureId){
        //Do nothing unless overridden by subclass
    }

    public boolean onPreparePanel(int featureId, View view, Menu menu){
        //Do nothing unless overridden by subclass
        return true;
    }

    //TODO public abstract void addContentView(View view, ViewGroup.LayoutParams params);



}
