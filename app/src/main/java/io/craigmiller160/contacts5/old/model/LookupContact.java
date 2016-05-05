package io.craigmiller160.contacts5.old.model;

/**
 * Created by Craig on 2/6/2016.
 */
public class LookupContact {

    private int position;
    private long contactId;

    public LookupContact(){}

    public LookupContact(int position, long contactId){
        this.position = position;
        this.contactId = contactId;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public long getContactId() {
        return contactId;
    }

    public void setContactId(long contactId) {
        this.contactId = contactId;
    }
}
