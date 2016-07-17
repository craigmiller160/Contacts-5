/*
 * Copyright 2016 ShadowAngler <craigmiller160@gmail.com> - All Rights Reserved
 * Proprietary / Confidential
 * Unauthorized copying, use, or redistribution of this file is prohibited
 */

package io.craigmiller160.contacts5.model;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import io.craigmiller160.contacts5.IllegalArgumentCtxException;
import io.craigmiller160.contacts5.util.AbstractAndroidUtil;

/**
 * Created by craig on 6/4/16.
 */
public class AndroidModel extends AbstractAndroidUtil{

    //TODO test to make sure that the weak references to the listeners are working. the ArrayAdapters are good candidates for this

    private String modelName;
    private static final String TAG = "AndroidModel";

    private static final String RESTORE_KEYS = "RestoreKeys";

    private final WeakPropertyChangeSupport support = new WeakPropertyChangeSupport(this);
    private final Map<String,Object> props = Collections.synchronizedMap(new HashMap<String, Object>());

    protected AndroidModel(Context context){
        super(context);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener){
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener){
        support.removePropertyChangeListener(listener);
    }

    protected void firePropertyChangeEvent(String propertyName, Object oldValue, Object newValue){
        support.firePropertyChangeEvent(propertyName, oldValue, newValue);
    }

    protected void fireIndexedPropertyChangeEvent(String propertyName, int index, Object oldValue, Object newValue){
        support.fireIndexedPropertyChangeEvent(propertyName, index, oldValue, newValue);
    }

    protected synchronized void setModelName(String modelName){
        this.modelName = modelName;
    }

    protected synchronized void setModelName(int resId){
        setModelName(getString(resId));
    }

    protected synchronized String getModelName(){
        return modelName;
    }

    public synchronized void setProperty(String propertyName, Object value){
        props.put(propertyName, value);
        firePropertyChangeEvent(propertyName, null, value);
    }

    public void setProperty(int resId, Object value){
        setProperty(getString(resId), value);
    }

    public synchronized void clearProperty(String propertyName){
        props.remove(propertyName);
        firePropertyChangeEvent(propertyName, null, null);
    }

    public void clearProperty(int resId){
        clearProperty(getString(resId));
    }

    public int getPropertyCount(){
        return props.size();
    }

    public void clearAllProperties(){
        props.clear();
    }

    public Object getProperty(String propertyName){
        return props.get(propertyName);
    }

    public Object getProperty(int resId){
        return getProperty(getString(resId));
    }

    public <T> T getProperty(String propertyName, Class<T> returnType){
        if(returnType == null){
            throw new IllegalArgumentCtxException("Return type value cannot be null")
                    .addContextValue("Property Name", propertyName);
        }

        Object value = getProperty(propertyName);
        if(value == null){
            return null;
        }

        if(!returnType.isAssignableFrom(value.getClass())){
            throw new IllegalArgumentCtxException("Invalid return type for property value")
                    .addContextValue("Property Name", propertyName)
                    .addContextValue("Value Type", value.getClass().getName())
                    .addContextValue("Specified Return Type", returnType.getName());
        }
        return (T) value;
    }

    public <T> T getProperty(int resId, Class<T> returnType){
        return getProperty(getString(resId), returnType);
    }

    public void storeState(Bundle savedInstance){
        synchronized (props){
            Set<String> keySet = new HashSet<>(props.keySet());
            savedInstance.putSerializable(RESTORE_KEYS, (Serializable) keySet);
            Set<Map.Entry<String,Object>> entries = props.entrySet();
            for(Map.Entry<String,Object> entry : entries){
                if(entry.getValue() instanceof Serializable){
                    try{
                        savedInstance.putSerializable(entry.getKey(), (Serializable) entry.getValue());
                    }
                    catch(Exception ex){
                        Log.e(TAG, "Unable to store state of model property. Cause: Exception | Model: " + getModelName() + " | Property: " + entry.getKey(), ex);
                    }
                }
                else{
                    Log.e(TAG, "Unable to store state of model property. Cause: Not Serializable | Model: " + getModelName() + " | Property: " + entry.getKey());
                }
            }
        }

    }

    public void restoreState(Bundle savedInstance){
        synchronized (props){
            Set<String> keys = (Set<String>) savedInstance.get(RESTORE_KEYS);
            if(keys != null){
                for(String key : keys){
                    props.put(key, savedInstance.get(key));
                    firePropertyChangeEvent(key, null, savedInstance.get(key));
                }
            }

        }
    }
}