package io.craigmiller160.contacts5.util;

import java.util.Comparator;

import io.craigmiller160.contacts5.R;
import io.craigmiller160.contacts5.model.Contact;
import io.craigmiller160.contacts5.model.ContactGroup;
import io.craigmiller160.contacts5.service.ContactsPreferences;

/**
 * Created by Craig on 2/11/2016.
 */
public class Comparators {

    private static ContactsPreferences prefs = ContactsPreferences.getInstance();

    public static Comparator<Contact> getContactComparator(){
        String sortOrder = prefs.getSortOrder();
        if(sortOrder != null){
            if(sortOrder.equals("ASC")){
                return CONTACT_DISPLAY_NAME_ASC;
            }
            else if(sortOrder.equals("DESC")){
                return CONTACT_DISPLAY_NAME_DESC;
            }
        }

        return CONTACT_DISPLAY_NAME_ASC;
    }

    public static Comparator<ContactGroup> getGroupComparator(){
        return GROUP_NAME_ONLY_ASC;
    }

    public static final Comparator<Contact> CONTACT_DISPLAY_NAME_ASC = new Comparator<Contact>() {
        @Override
        public int compare(Contact c1, Contact c2) {
            return c1.getDisplayName().compareToIgnoreCase(c2.getDisplayName());
        }
    };

    public static final Comparator<Contact> CONTACT_DISPLAY_NAME_DESC = new Comparator<Contact>() {
        @Override
        public int compare(Contact c1, Contact c2) {
            return (c1.getDisplayName().compareToIgnoreCase(c2.getDisplayName())) * -1;
        }
    };

    public static final Comparator<ContactGroup> GROUP_NAME_ONLY_ASC = new Comparator<ContactGroup>() {
        @Override
        public int compare(ContactGroup cg1, ContactGroup cg2) {
            return cg1.getGroupName().compareToIgnoreCase(cg2.getGroupName());
        }
    };

    public static final Comparator<ContactGroup> GROUP_NAME_ONLY_DESC = new Comparator<ContactGroup>() {
        @Override
        public int compare(ContactGroup cg1, ContactGroup cg2) {
            return cg1.getGroupName().compareToIgnoreCase(cg2.getGroupName());
        }
    };

}
