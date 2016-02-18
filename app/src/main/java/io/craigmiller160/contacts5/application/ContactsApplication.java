package io.craigmiller160.contacts5.application;

import io.craigmiller160.contacts5.helper.ContactsHelperBuilderFactory;
import io.craigmiller160.contacts5.helper.HelperBuilder;
import io.craigmiller160.contacts5.model.LookupContact;
import io.craigmiller160.contacts5.service.ContactsPreferences;

/**
 * Created by Craig on 2/6/2016.
 */
public class ContactsApplication extends AbstractAndroidMVPApp {

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

    @Override
    protected HelperBuilder createHelperBuilder() {
        return ContactsHelperBuilderFactory.getInstance().newHelperBuilder();
    }

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
