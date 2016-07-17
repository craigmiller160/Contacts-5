/*
 * Copyright 2016 ShadowAngler <craigmiller160@gmail.com> - All Rights Reserved
 * Proprietary / Confidential
 * Unauthorized copying, use, or redistribution of this file is prohibited
 */

package io.craigmiller160.contacts5.model;

import java.beans.IndexedPropertyChangeEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by craig on 6/5/16.
 */
public class WeakPropertyChangeSupport {

    private List<WeakReference<PropertyChangeListener>> listeners = new ArrayList<>();
    private final Object source;

    public WeakPropertyChangeSupport(Object source){
        this.source = source;
    }

    public synchronized void addPropertyChangeListener(PropertyChangeListener listener){
        listeners.add(new WeakReference<>(listener));
    }

    public synchronized void removePropertyChangeListener(PropertyChangeListener listener){
        //TODO this part may not work, needs to be tested
        listeners.remove(new WeakReference<>(listener));
    }

    public synchronized void firePropertyChangeEvent(String propertyName, Object oldValue, Object newValue){
        Iterator<WeakReference<PropertyChangeListener>> iterator = listeners.iterator();
        while(iterator.hasNext()){
            PropertyChangeListener listener = iterator.next().get();
            if(listener == null){
                iterator.remove();
            }
            else{
                PropertyChangeEvent event = new PropertyChangeEvent(source, propertyName, oldValue, newValue);
                listener.propertyChange(event);
            }
        }
    }

    public synchronized void fireIndexedPropertyChangeEvent(String propertyName, int index, Object oldValue, Object newValue){
        Iterator<WeakReference<PropertyChangeListener>> iterator = listeners.iterator();
        while(iterator.hasNext()){
            PropertyChangeListener listener = iterator.next().get();
            if(listener == null){
                iterator.remove();
            }
            else{
                PropertyChangeEvent event = new IndexedPropertyChangeEvent(source, propertyName, oldValue, newValue, index);
                listener.propertyChange(event);
            }
        }
    }

}