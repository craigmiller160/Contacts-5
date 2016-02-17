package io.craigmiller160.contacts5.helper;

import java.beans.PropertyChangeEvent;

import io.craigmiller160.contacts5.model.AbstractModel;
import io.craigmiller160.contacts5.model.Contact;
import io.craigmiller160.contacts5.model.ContactGroup;
import io.craigmiller160.contacts5.model.ContactsModel;

/**
 * Created by Craig on 2/14/2016.
 */
public class ContactsHelper extends AbstractReflectiveHelper {

    /*
     * ContactsModel property names
     */
    public static final String NEW_CONTACT_PROP = "NewContact";
    public static final String CONTACTS_STORAGE_PROP = "ContactsStorage";
    public static final String UPDATED_CONTACT_PROP = "UpdatedContact";
    public static final String CONTACT_TO_REMOVE_PROP = "ContactToRemove";
    public static final String CONTACT_AT_INDEX_PROP = "ContactAtIndex";
    public static final String GROUP_AT_INDEX_PROP = "GroupAtIndex";
    public static final String CONTACT_IN_GROUP_AT_INDEX_PROP = "ContactInGroupAtIndex";
    public static final String SELECTED_INDEX_PROPERTY = "SelectedIndex";

    private ContactsModel contactsModel;

    @Override
    public void addModel(AbstractModel model) {
        super.addModel(model);
        if(model instanceof ContactsModel){
            this.contactsModel = (ContactsModel) model;
        }
    }

    @Override
    public void removeModel(AbstractModel model) {
        super.removeModel(model);
        if(model instanceof ContactsModel){
            this.contactsModel = null;
        }
    }

    //TODO decide if these methods are necessary, or if the reflective invocation will be enough?

    public Contact getContactAtIndex(int index){
        return contactsModel.getContactAtIndex(index);
    }

    public Contact getContactInGroupAtIndex(ContactGroup group, int index){
        return contactsModel.getContactInGroupAtIndex(group, index);
    }

    public ContactGroup getGroupAtIndex(int index){
        return contactsModel.getGroupAtIndex(index);
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        //TODO probably moving this into AbstractHelper, with a special method call that
        // gets reflectively implemented in AbstractReflectiveHelper
    }
}
