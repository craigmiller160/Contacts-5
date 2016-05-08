package io.craigmiller160.contacts5;

import android.app.Application;

import io.craigmiller160.contacts5.service.ContactsPreferences;
import io.craigmiller160.contacts5.util.ContactsConfigurationBuilder;
import io.craigmiller160.locus.Locus;
import io.craigmiller160.locus.util.LocusConfiguration;

/**
 * The application class for Contacts 5+.
 *
 * Created by craig on 5/4/16.
 */
public class ContactsApplication extends Application {

    @Override
    public void onCreate(){
        super.onCreate();
        if(!Locus.isInitialized()){
            LocusConfiguration config = ContactsConfigurationBuilder.buildConfiguration();
            Locus.initialize(config);
        }
        ContactsPreferences.loadAllPreferences(this);
    }

}
