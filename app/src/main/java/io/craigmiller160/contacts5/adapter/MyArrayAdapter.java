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

/**
 * Created by craig on 6/5/16.
 */
public abstract class MyArrayAdapter<T> extends ArrayAdapter<T> implements PropertyChangeListener {

    private List<T> contents;
    private final String propertyName;

    protected MyArrayAdapter(Context context, int resource, String propertyName) {
        super(context, resource);
        this.propertyName = propertyName;
        ContactsApp.getApp().modelFactory().getModel(R.string.model_contacts).addPropertyChangeListener(this);
        contents = ContactsApp.getApp().modelFactory().getModel(R.string.model_contacts).getProperty(propertyName, List.class);
    }

    protected MyArrayAdapter(Context context, int resource, int resId){
        this(context, resource, context.getString(resId));
    }

    public void setContents(final List<T> contents){
        if(Looper.myLooper() == Looper.getMainLooper()){
            this.contents = contents;
            notifyDataSetChanged();
        }
        else{
            Handler h = new Handler(Looper.getMainLooper());
            h.post(new Runnable() {
                @Override
                public void run() {
                    MyArrayAdapter.this.contents = contents;
                    MyArrayAdapter.this.notifyDataSetChanged();
                }
            });
        }
    }

    public List<T> getContents(){
        return contents;
    }

    @Override
    public int getCount(){
        return contents != null ? contents.size() : 0;
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        if(event.getPropertyName().equals(propertyName)){
            setContents((List<T>) event.getNewValue());
        }
    }
}
