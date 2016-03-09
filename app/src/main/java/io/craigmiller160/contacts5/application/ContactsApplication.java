package io.craigmiller160.contacts5.application;

import android.os.Looper;

import io.craigmiller160.contacts5.helper.ContactsHelper;
import io.craigmiller160.contacts5.helper.ContactsHelperBuilderFactory;
import io.craigmiller160.contacts5.helper.HelperBuilder;
import io.craigmiller160.contacts5.model.Contact;
import io.craigmiller160.contacts5.model.ContactGroup;
import io.craigmiller160.contacts5.model.ContactsStorage;
import io.craigmiller160.contacts5.model.LookupContact;
import io.craigmiller160.contacts5.service.ContactsPreferences;
import io.craigmiller160.contacts5.service.ContactsService;
import io.craigmiller160.contacts5.service.ContactsServiceFactory;

import static io.craigmiller160.contacts5.helper.ContactsHelper.CONTACTS_STORAGE_PROP;

/**
 * Created by Craig on 2/6/2016.
 */
public class ContactsApplication extends AbstractAndroidMVPApp {

    private ContactsService contactsService = ContactsServiceFactory.getInstance().getContactsService();

    private static ContactsApplication instance;

    private LookupContact lookupContact;

    public static ContactsApplication getInstance(){
        return instance;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        instance = this;
        ContactsPreferences.getInstance().loadSavedPreferences(this);


    }

    public void loadAllContacts(){
        if(Looper.getMainLooper() == Looper.myLooper()){
            Thread t = new Thread(){
                @Override
                public void run(){
                    loadContacts();
                }
            };
            t.start();
        }
        else{
            loadContacts();
        }
    }

    private void loadContacts(){
        ContactsStorage storage = contactsService.getAllContacts(this);
        setModelProperty(CONTACTS_STORAGE_PROP, storage);
    }

    @Override
    protected HelperBuilder createHelperBuilder() {
        return ContactsHelperBuilderFactory.getInstance().newHelperBuilder();
    }

    public Contact getContactAtIndex(int index){
        return ((ContactsHelper) getHelper()).getContactAtIndex(index);
    }

    public Contact getContactInGroupAtIndex(ContactGroup group, int index){
        return ((ContactsHelper) getHelper()).getContactInGroupAtIndex(group, index);
    }

    public ContactGroup getGroupAtIndex(int index){
        return ((ContactsHelper) getHelper()).getGroupAtIndex(index);
    }

    //TODO remove deprecated methods

    @Deprecated
    public synchronized void setLookupContact(LookupContact lookupContact){
        this.lookupContact = lookupContact;
    }

    @Deprecated
    public synchronized void clearLookupContact(){
        this.lookupContact = null;
    }

    @Deprecated
    public synchronized LookupContact getLookupContact(){
        return lookupContact;
    }

}
