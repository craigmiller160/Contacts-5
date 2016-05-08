package io.craigmiller160.contacts5.model;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import io.craigmiller160.contacts5.model.collection.SortedMultiValueMap;
import io.craigmiller160.utils.collection.SortedList;


/**
 * Created by Craig on 2/12/2016.
 */
public class ContactsStorage {

    private SortedList<Contact> allContacts;
    private SortedList<ContactGroup> allGroups; //TODO need to keep this in sync with contactGroups
    private SortedMultiValueMap<Long, Contact> contactGroups;

    public ContactsStorage(){
        this(null, null);
    }

    public ContactsStorage(Comparator<Contact> contactComparator,
                           Comparator<ContactGroup> groupComparator){
        allContacts = new SortedList<>(contactComparator);
        allGroups = new SortedList<>(groupComparator);
        contactGroups = new SortedMultiValueMap<>();
        contactGroups.setValueComparator(contactComparator);
    }

    public void clear(){
        allContacts.clear();
        allGroups.clear();
        contactGroups.clear();
    }

    public void addContact(Contact contact){
        if(contact != null){
            allContacts.add(contact);
            List<Long> groups = contact.getGroupIds();
            if(groups != null && groups.size() > 0){
                contactGroups.putValueInMultipleCollections(groups, contact);
            }
        }
    }

    public void addAllContacts(SortedList<Contact> allContacts){
        if(allContacts != null){
            this.allContacts = allContacts;
            for(Contact c : allContacts){
                List<Long> groups = c.getGroupIds();
                if(groups != null && groups.size() > 0){
                    contactGroups.putValueInMultipleCollections(groups, c);
                }
            }
        }
    }

    public void addAllGroups(SortedList<ContactGroup> groups){
        if(groups != null){
            this.allGroups.addAll(groups);
        }
    }

    public boolean removeContact(Contact contact){
        boolean result = false;
        if(contact != null){
            result = allContacts.remove(contact);
            contactGroups.removeValue(contact);
            //TODO need to update all groups here
        }
        return result;
    }

    public Contact removeContact(int index){
        Contact contact = allContacts.remove(index);
        if(contact != null){
            contactGroups.removeValue(contact);
        }
        return contact;
    }

    public Contact getContact(int index){
        return allContacts.get(index);
    }

    public void updateContact(Contact contact, int index){
        Contact oldContact = allContacts.get(index);
        allContacts.set(index, contact);
        contactGroups.removeValue(oldContact);
        List<Long> groups = contact.getGroupIds();
        if(groups != null){
            for(Long id : groups){
                contactGroups.putValue(id, contact);
            }
        }
    }

    public void addGroup(ContactGroup group){
        allGroups.add(group);
    }

    public void removeGroup(ContactGroup group){
        allGroups.remove(group);
    }

    public void removeGroup(int index){
        allGroups.remove(index);
    }

    public ContactGroup getGroup(int index){
        return allGroups.get(index);
    }

    public int getGroupSize(ContactGroup group){
        if(group == null){
            return 0;
        }

        Collection<Contact> groupMembers = contactGroups.get(group.getGroupId());
        if(groupMembers != null){
            return groupMembers.size();
        }
        return 0;
    }

    public int getGroupSize(int index){
        if(index < allGroups.size()){
            return 0;
        }
        ContactGroup group = allGroups.get(index);
        return getGroupSize(group);
    }

    public Contact getContactInGroup(ContactGroup group, int index){
        Collection<Contact> groupMembers = contactGroups.get(group.getGroupId());
        if(groupMembers != null && groupMembers instanceof List){
            return ((List<Contact>) groupMembers).get(index);
        }
        return null;
    }

    public void setContactComparator(Comparator<Contact> contactComparator){
        allContacts.setComparator(contactComparator);
        contactGroups.setValueComparator(contactComparator);
    }

    public void setGroupComparator(Comparator<ContactGroup> groupComparator){
        allGroups.setComparator(groupComparator);
    }

    public int getContactCount(){
        return allContacts.size();
    }

    public int getGroupCount(){
        return allGroups.size();
    }

}
