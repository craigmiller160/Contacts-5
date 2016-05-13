package io.craigmiller160.contacts5.model;

import io.craigmiller160.locus.annotations.LModel;

/**
 * Created by craig on 5/8/16.
 */
@LModel
public class ContactsModel {

    private ContactsStorage storage;

    public synchronized void setContactsStorage(ContactsStorage storage){
        this.storage = storage;
    }

    public synchronized ContactsStorage getContactsStorage(){
        return storage;
    }

    public synchronized Contact getContactAtIndex(int index){
        return storage.getContact(index);
    }

    public synchronized int getContactCount(){
        return storage != null ? storage.getContactCount() : 0;
    }

}
