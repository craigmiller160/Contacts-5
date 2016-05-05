package io.craigmiller160.contacts5.old.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Created by Craig on 2/13/2016.
 */
public abstract class AbstractModel {

    private PropertyChangeSupport support = new PropertyChangeSupport(this);

    public void addPropertyChangeListener(PropertyChangeListener listener){
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener){
        support.removePropertyChangeListener(listener);
    }

    protected void firePropertyChangeEvent(String propName, Object oldValue, Object newValue){
        support.firePropertyChange(propName, oldValue, newValue);
    }

    protected void fireIndexedPropertyChangeEvent(String propName, int index, Object oldValue, Object newValue){
        support.fireIndexedPropertyChange(propName, index, oldValue, newValue);
    }

}
