package io.craigmiller160.contacts5.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Craig on 2/2/2016.
 */
public class ContactListModel {

    public static final String CONTACT_ADDED = "ContactAdded";
    public static final String CONTACT_REMOVED = "ContactRemoved";
    public static final String CONTACT_UPDATED = "ContactUpdated";
    public static final String ALL_CONTACTS_UPDATED = "AllContactsUpdated";

    PropertyChangeSupport support = new PropertyChangeSupport(this);

    private List<Contact> contactsList = new ArrayList<>();

    public ContactListModel(){}

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

    public boolean addContact(Contact contact){
        boolean result = contactsList.add(contact);
        if(result){
            firePropertyChangeEvent(CONTACT_ADDED, null, contact);
        }

        return result;
    }

    public boolean removeContact(Contact contact){
        int index = contactsList.indexOf(contact);
        boolean result = contactsList.remove(contact);
        if(result){
            fireIndexedPropertyChangeEvent(CONTACT_REMOVED, index, null, contact);
        }

        return result;
    }

    public void updateContactAt(Contact contact, int index){
        if(index >= contactsList.size() || index < 0){
            throw new IllegalArgumentException("Specified index is not contained in this contacts list: " + index);
        }

        contactsList.set(index, contact);
        fireIndexedPropertyChangeEvent(CONTACT_UPDATED, index, null, contact);
    }

    public Contact getContact(int index){
        return contactsList.get(index);
    }

    public void setContactsList(List<Contact> contactsList){
        if(contactsList == null){
            this.contactsList = new ArrayList<>();
        }
        else{
            this.contactsList = contactsList;
        }
        firePropertyChangeEvent(ALL_CONTACTS_UPDATED, null, this.contactsList);
    }

    public List<Contact> getContactsList(){
        return contactsList;
    }
}
