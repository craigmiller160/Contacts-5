package io.craigmiller160.contacts5.model;

import java.util.Collections;
import java.util.List;

import static io.craigmiller160.contacts5.util.ContactsConstants.*;

/**
 * Created by craig on 6/4/16.
 */
public class ContactsModel extends AbstractModel{

    private List<Contact> contacts;
    private List<ContactGroup> groups;
    private List<Contact> contactsInGroup;

    public ContactsModel() {
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = Collections.synchronizedList(contacts);
        firePropertyChangeEvent(CONTACTS_LIST, null, this.contacts);
    }

    public List<ContactGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<ContactGroup> groups) {
        this.groups = Collections.synchronizedList(groups);
        firePropertyChangeEvent(GROUPS_LIST, null, this.groups);
    }

    public List<Contact> getContactsInGroup() {
        return contactsInGroup;
    }

    public void setContactsInGroup(List<Contact> contactsInGroup) {
        this.contactsInGroup = Collections.synchronizedList(contactsInGroup);
        firePropertyChangeEvent(CONTACTS_IN_GROUP_LIST, null, contactsInGroup);
    }

    public Contact getContact(int position){
        return contacts.get(position);
    }

    public Contact getContactInGroup(int position){
        return contactsInGroup.get(position);
    }

    public ContactGroup getGroup(int position){
        return groups.get(position);
    }
}
