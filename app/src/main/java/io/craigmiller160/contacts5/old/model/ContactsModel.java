package io.craigmiller160.contacts5.old.model;

import static io.craigmiller160.contacts5.old.helper.ContactsHelper.*;

/**
 * Created by Craig on 2/14/2016.
 */
public class ContactsModel extends AbstractModel {

    //TODO this is now totally thread safe

    private ContactsStorage storage = new ContactsStorage();
    private int selectedIndex;

    public void setContactsStorage(ContactsStorage storage){
        System.out.println("Setter running"); //TODO delete this
        synchronized (this){
            this.storage = storage;
        }
        firePropertyChangeEvent(CONTACTS_STORAGE_PROP, null, new Object());
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

    public synchronized Integer getContactCount(){
        return storage.getContactCount();
    }

    public synchronized Integer getGroupCount(){
        return storage.getGroupCount();
    }

    public synchronized Contact getContactAtIndex(Integer index){
        return storage.getContact(index);
    }

    public synchronized ContactGroup getGroupAtIndex(Integer index){
        ContactGroup group = storage.getGroup(index);
        if(group != null){
            group.setGroupSize(storage.getGroupSize(group));
        }
        return group;
    }

    public synchronized Contact getContactInGroupAtIndex(ContactGroup group, Integer index){
        return storage.getContactInGroup(group, index);
    }

    public synchronized void setSelectedIndex(Integer index){
        this.selectedIndex = index;
        firePropertyChangeEvent(SELECTED_INDEX_PROPERTY, null, index);
    }

    public synchronized int getSelectedIndex(){
        return selectedIndex;
    }
}
