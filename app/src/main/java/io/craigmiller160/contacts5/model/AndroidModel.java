package io.craigmiller160.contacts5.model;

import android.os.Bundle;
import android.util.Log;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.NotSerializableException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.craigmiller160.utils.reflect.ReflectiveException;

/**
 * Created by craig on 6/4/16.
 */
public class AndroidModel {

    private String modelName;
    private static final String TAG = "AndroidModel";

    private static final String RESTORE_KEYS = "RestoreKeys";

    private final WeakPropertyChangeSupport support = new WeakPropertyChangeSupport(this);
    private final Map<String,Object> props = Collections.synchronizedMap(new HashMap<String, Object>());

    protected AndroidModel(){}

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

    protected synchronized String getModelName(){
        return modelName;
    }

    public void setProperty(String propertyName, Object value){
        props.put(propertyName, value);
        firePropertyChangeEvent(propertyName, null, value);
    }

    public Object getProperty(String propertyName){
        return props.get(propertyName);
    }

    public <T> T getProperty(String propertyName, Class<?> returnType){
        if(returnType == null){
            throw new IllegalArgumentException("Return type value cannot be null");
        }

        Object value = getProperty(propertyName);
        if(value == null){
            return null;
        }

        if(!returnType.isAssignableFrom(value.getClass())){
            throw new IllegalArgumentException(String.format("Invalid type for property value. %1$s is not assignable from %2$s",
                    returnType.getName(), value.getClass().getName()));
        }
        return (T) value;
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
                    try{
                        System.out.println("KEY: " + key + " VALUE: " + savedInstance.get(key).getClass().getName());
                        props.put(key, savedInstance.get(key));
                        firePropertyChangeEvent(key, null, savedInstance.get(key));
                    }
                    catch(Exception ex){
                        //TODO do something... maybe
                        ex.printStackTrace();
                    }
                }
            }

        }
    }
}