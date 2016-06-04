package io.craigmiller160.contacts5.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Created by craig on 6/4/16.
 */
public abstract class AbstractModel {

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    public void addPropertyChangeListener(PropertyChangeListener listener){
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener){
        support.removePropertyChangeListener(listener);
    }

    protected void firePropertyChangeEvent(String propertyName, Object oldValue, Object newValue){
        support.firePropertyChange(propertyName, oldValue, newValue);
    }

    protected void fireIndexedPropertyChangeEvent(String propertyName, int index, Object oldValue, Object newValue){
        support.fireIndexedPropertyChange(propertyName, index, oldValue, newValue);
    }

}
