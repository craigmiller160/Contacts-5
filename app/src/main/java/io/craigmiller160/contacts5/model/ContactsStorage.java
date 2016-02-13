package io.craigmiller160.contacts5.model;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import io.craigmiller160.contacts5.model.collection.SortedList;
import io.craigmiller160.contacts5.model.collection.SortedMultiValueMap;

/**
 * Created by Craig on 2/12/2016.
 */
public class ContactsStorage {

    private SortedList<Contact> allContacts;
    private SortedMultiValueMap<ContactGroup, Contact> contactGroups;

    public ContactsStorage(){
        this(null, null);
    }

    public ContactsStorage(Comparator<Contact> contactComparator,
                           Comparator<ContactGroup> groupComparator){
        allContacts = new SortedList<>(contactComparator);
        contactGroups = new SortedMultiValueMap<>(groupComparator, contactComparator);
    }

    public void clear(){
        allContacts.clear();
        contactGroups.clear();
    }

    public void addContact(Contact contact){
        if(contact != null){
            allContacts.add(contact);
            List<ContactGroup> groups = contact.getGroups();
            if(groups != null && groups.size() > 0){
                contactGroups.putValueInMultipleCollections(groups, contact);
            }
        }
    }

    public void addAllContacts(SortedList<Contact> allContacts){
        if(allContacts != null){
            this.allContacts = allContacts;
            for(Contact c : allContacts){
                List<ContactGroup> groups = c.getGroups();
                if(groups != null && groups.size() > 0){
                    contactGroups.putValueInMultipleCollections(groups, c);
                }
            }
        }
    }

    public void removeContact(Contact contact){
        if(contact != null){
            allContacts.remove(contact);
            contactGroups.removeValue(contact);
        }
    }

    public void removeContact(int index){
        Contact contact = allContacts.remove(index);
        if(contact != null){
            contactGroups.removeValue(contact);
        }
    }

    public Contact getContact(int index){
        return allContacts.get(index);
    }

    public void updateContact(Contact contact, int index){
        Contact oldContact = allContacts.get(index);
        allContacts.set(index, contact);
        contactGroups.removeValue(oldContact);
        List<ContactGroup> groups = contact.getGroups();
        if(groups != null){
            for(ContactGroup group : groups){
                contactGroups.putValue(group, contact);
            }
        }
    }

    public ContactGroup getGroup(int index){
        return contactGroups.getSortedKeys().get(index);
    }

    public int getGroupSize(ContactGroup group){
        return contactGroups.get(group).size();
    }

    public int getGroupSize(int index){
        ContactGroup group = contactGroups.getSortedKeys().get(index);
        return contactGroups.get(group).size();
    }

    public Contact getContactInGroup(ContactGroup group, int index){
        return ((SortedList<Contact>) contactGroups.get(group)).get(index);
    }

    public void setContactComparator(Comparator<Contact> contactComparator){
        allContacts.setComparator(contactComparator);
        contactGroups.setValueComparator(contactComparator);
    }

    public void setGroupComparator(Comparator<ContactGroup> groupComparator){
        contactGroups.setKeyComparator(groupComparator);
    }

    public int getContactCount(){
        return allContacts.size();
    }

    public int getGroupCount(){
        return contactGroups.size();
    }

}
