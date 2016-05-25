package io.craigmiller160.contacts5;

import android.app.Application;

import io.craigmiller160.contacts5.controller.ControllerFactory;
import io.craigmiller160.contacts5.service.ContactsPrefsService;
import io.craigmiller160.contacts5.service.ServiceFactory;
import io.craigmiller160.contacts5.util.ContactsConfigurationBuilder;
import io.craigmiller160.contacts5.util.ContactsThreadFactory;

/**
 * The application class for Contacts 5+.
 *
 * Created by craig on 5/4/16.
 */
public class ContactsApplication extends Application {

    @Override
    public void onCreate(){
        super.onCreate();
        if(!ServiceFactory.isInitialized()){
            ServiceFactory.initialize(this);
        }

        if(!ControllerFactory.isInitialized()){
            ControllerFactory.initialize(this);
        }

        Thread.setDefaultUncaughtExceptionHandler(new ContactsThreadFactory.ContactsUncaughtExceptionHandler()); //TODO move this if it works
    }

}
