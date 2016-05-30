package io.craigmiller160.contacts5.model;

import java.util.List;

/**
 * Created by craig on 5/29/16.
 */
public interface ContactsDataCallback {

    void setContactsList(List<Contact> contacts);

    void setGroupsList(List<ContactGroup> groups);

}
