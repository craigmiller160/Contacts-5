package io.craigmiller160.contacts5;

import android.app.Application;

import io.craigmiller160.contacts5.model.LookupContact;

/**
 * Created by Craig on 2/6/2016.
 */
public class ContactsApplication extends Application {

    private static ContactsApplication instance;

    private LookupContact lookupContact;

    public static ContactsApplication getInstance(){
        return instance;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        instance = this;
    }

    public synchronized void setLookupContact(LookupContact lookupContact){
        this.lookupContact = lookupContact;
    }

    public synchronized void clearLookupContact(){
        this.lookupContact = null;
    }

    public synchronized LookupContact getLookupContact(){
        return lookupContact;
    }

}
