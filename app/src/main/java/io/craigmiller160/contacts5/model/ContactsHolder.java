package io.craigmiller160.contacts5.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by craig on 5/26/16.
 */
public class ContactsHolder {

    private List<Contact> contacts = new ArrayList<>();
    private List<ContactGroup> groups = new ArrayList<>();

    public void setContacts(List<Contact> contacts){
        this.contacts = contacts;
    }

    public Contact getContact(int index){
        return contacts.get(index);
    }

    public void setGroups(List<ContactGroup> groups){
        this.groups = groups;
    }

    public List<ContactGroup> getGroups(){
        return groups;
    }

    public ContactGroup getGroup(int index){
        return groups.get(index);
    }

}
