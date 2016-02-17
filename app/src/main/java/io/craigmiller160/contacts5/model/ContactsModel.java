package io.craigmiller160.contacts5.model;

import static io.craigmiller160.contacts5.helper.ContactsHelper.*;

/**
 * Created by Craig on 2/14/2016.
 */
public class ContactsModel extends AbstractModel {

    //TODO this is now totally thread safe

    private ContactsStorage storage;
    private int selectedIndex;

    public synchronized void setContactsStorage(ContactsStorage storage){
        this.storage = storage;
    }

    public synchronized ContactsStorage getContactsStorage(){
        return storage;
    }

    public void setNewContact(Contact contact){
        synchronized(this){
            storage.addContact(contact);
        }

        firePropertyChangeEvent(NEW_CONTACT_PROP, null, contact);
    }

    public void setUpdatedContact(Contact contact, int index){
        synchronized (this){
            storage.updateContact(contact, index);
        }

        fireIndexedPropertyChangeEvent(UPDATED_CONTACT_PROP, index, null, contact);
    }

    public void setContactToRemove(Contact contact){
        boolean result = false;
        synchronized (this){
            result = storage.removeContact(contact);
        }

        if(result){
            firePropertyChangeEvent(CONTACT_TO_REMOVE_PROP, null, contact);
        }
    }

    public void setContactToRemove(int index){
        Contact contact = null;
        synchronized (this){
            contact = storage.removeContact(index);
        }

        if(contact != null){
            fireIndexedPropertyChangeEvent(CONTACT_TO_REMOVE_PROP, index, null, contact);
        }

    }

    public synchronized Contact getContactAtIndex(int index){
        return storage.getContact(index);
    }

    public synchronized ContactGroup getGroupAtIndex(int index){
        return storage.getGroup(index);
    }

    public synchronized Contact getContactInGroupAtIndex(ContactGroup group, int index){
        return storage.getContactInGroup(group, index);
    }

    public synchronized void setSelectedIndex(int index){
        this.selectedIndex = index;
        firePropertyChangeEvent(SELECTED_INDEX_PROPERTY, null, index);
    }

    public synchronized int getSelectedIndex(){
        return selectedIndex;
    }
}
