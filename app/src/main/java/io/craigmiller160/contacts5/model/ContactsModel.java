package io.craigmiller160.contacts5.model;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static io.craigmiller160.contacts5.util.ContactsConstants.*;

/**
 * Created by craig on 6/4/16.
 */
public class ContactsModel extends AndroidModel {

//    private List<Contact> contacts;
//    private List<ContactGroup> groups;
//    private List<Contact> contactsInGroup;
//
//    public ContactsModel() {
//    }
//
//    public List<Contact> getContactsList() {
//        return contacts;
//    }
//
//    public void setContactsList(List<Contact> contacts) {
//        this.contacts = Collections.synchronizedList(contacts);
//        firePropertyChangeEvent(CONTACTS_LIST, null, this.contacts);
//    }
//
//    public List<ContactGroup> getGroupsList() {
//        return groups;
//    }
//
//    public void setGroupsList(List<ContactGroup> groups) {
//        this.groups = Collections.synchronizedList(groups);
//        firePropertyChangeEvent(GROUPS_LIST, null, this.groups);
//    }
//
//    public List<Contact> getContactsInGroupList() {
//        return contactsInGroup;
//    }
//
//    public void setContactsInGroupList(List<Contact> contactsInGroup) {
//        this.contactsInGroup = Collections.synchronizedList(contactsInGroup);
//        firePropertyChangeEvent(CONTACTS_IN_GROUP_LIST, null, contactsInGroup);
//    }
//
//    public Contact getContact(int position){
//        return contacts.get(position);
//    }
//
//    public Contact getContactInGroup(int position){
//        return contactsInGroup.get(position);
//    }
//
//    public ContactGroup getGroup(int position){
//        return groups.get(position);
//    }
//
//    @Override
//    protected void storeState(Bundle savedInstance) {
//        if(contacts != null){
//            savedInstance.putSerializable(CONTACTS_LIST, (ArrayList<Contact>) contacts);
//        }
//
//        if(groups != null){
//            savedInstance.putSerializable(GROUPS_LIST, (ArrayList<ContactGroup>) groups);
//        }
//
//        if(contactsInGroup != null){
//            savedInstance.putSerializable(CONTACTS_IN_GROUP_LIST, (ArrayList<Contact>) contactsInGroup);
//        }
//    }
//
//    @Override
//    protected void restoreState(Bundle savedInstance) {
//        Object cList = savedInstance.getSerializable(CONTACTS_LIST);
//        if(cList != null){
//            contacts = (List<Contact>) cList;
//        }
//
//        Object gList = savedInstance.getSerializable(GROUPS_LIST);
//        if(gList != null){
//            groups = (List<ContactGroup>) gList;
//        }
//
//        Object cgList = savedInstance.getSerializable(CONTACTS_IN_GROUP_LIST);
//        if(cgList != null){
//            contactsInGroup = (List<Contact>) cgList;
//        }
//    }
//
//    @Override
//    public void setModelProperty(String propertyName, Object value) {
//        switch (propertyName){
//            case CONTACTS_LIST:
//                setContactsList((List<Contact>) value);
//                break;
//            case GROUPS_LIST:
//                setGroupsList((List<ContactGroup>) value);
//                break;
//            case CONTACTS_IN_GROUP_LIST:
//                setContactsInGroupList((List<Contact>) value);
//                break;
//        }
//    }
//
//    @Override
//    public Object getModelProperty(String propertyName) {
//        Object result = null;
//        switch(propertyName){
//            case CONTACTS_LIST:
//                result = getContactsList();
//                break;
//            case GROUPS_LIST:
//                result = getGroupsList();
//                break;
//            case CONTACTS_IN_GROUP_LIST:
//                result = getContactsInGroupList();
//                break;
//        }
//        return result;
//    }
}
