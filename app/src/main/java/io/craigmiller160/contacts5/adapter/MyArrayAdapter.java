/*
 * Copyright 2016 ShadowAngler <craigmiller160@gmail.com> - All Rights Reserved
 * Proprietary / Confidential
 * Unauthorized copying, use, or redistribution of this file is prohibited
 */

package io.craigmiller160.contacts5.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.ArrayAdapter;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import io.craigmiller160.contacts5.ContactsApp;
import io.craigmiller160.contacts5.R;
import io.craigmiller160.contacts5.log.Logger;
import io.craigmiller160.contacts5.model.AndroidModel;

/**
 * Created by craig on 6/5/16.
 */
public abstract class MyArrayAdapter<T> extends ArrayAdapter<T> implements PropertyChangeListener {

    private static final String TAG = "MyArrayAdapter";
    private static final Logger logger = Logger.newLogger(TAG);

    private List<T> contents;
    private List<T> originalContents;
    private final String propertyName;
    private final AndroidModel contactsModel;
    private String filterQuery;

    protected MyArrayAdapter(Context context, int resource, String propertyName) {
        super(context, resource);
        this.propertyName = propertyName;
        this.contactsModel = ContactsApp.getApp().modelFactory().getModel(R.string.model_contacts);
        this.contactsModel.addPropertyChangeListener(this);
        refreshContentsFromModel();
    }

    protected MyArrayAdapter(Context context, int resource, int resId){
        this(context, resource, context.getString(resId));
    }

    protected void refreshContentsFromModel(){
        if(Looper.myLooper() == Looper.getMainLooper()){
            safelyRefreshContactsFromModel();
        }
        else{
            Handler h = new Handler(Looper.getMainLooper());
            h.post(new Runnable() {
                @Override
                public void run() {
                    safelyRefreshContactsFromModel();
                }
            });
        }
    }

    private void safelyRefreshContactsFromModel(){
        logger.v(TAG, "Refreshing ArrayAdapter contents from model. Property: " + propertyName);
        setOriginalContents(contactsModel.getProperty(propertyName, List.class));
        if(!isFiltered()){
            setContents(contactsModel.getProperty(propertyName, List.class));
        }
        else{
            filter(filterQuery);
        }
    }

    public void setContents(final List<T> contents){
        this.contents = contents;
        notifyDataSetChanged();
    }

    public void setOriginalContents(final List<T> contents){
        this.originalContents = contents;
        notifyDataSetChanged();
    }

    public boolean isFiltered(){
        return filterQuery != null;
    }

    public void clearFilter(){
        this.filterQuery = null;
        refreshContentsFromModel();
    }

    public void filter(String query){
        this.filterQuery = query;
        getFilter().filter(query);
    }

    public List<T> getContents(){
        return contents;
    }

    public List<T> getOriginalContents(){
        return originalContents;
    }

    public String getPropertyName(){
        return propertyName;
    }

    @Override
    public int getCount(){
        return contents != null ? contents.size() : 0;
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        if(event.getPropertyName().equals(propertyName)){
            refreshContentsFromModel();
        }
    }
}
