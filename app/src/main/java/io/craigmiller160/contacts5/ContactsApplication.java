package io.craigmiller160.contacts5;

import android.app.Application;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import io.craigmiller160.contacts5.controller.ControllerFactory;
import io.craigmiller160.contacts5.model.ModelFactory;
import io.craigmiller160.contacts5.service.ServiceFactory;

/**
 * The application class for Contacts 5+.
 *
 * Created by craig on 5/4/16.
 */
public class ContactsApplication extends Application {

    private static ContactsApplication instance;

    public static ContactsApplication getApplication(){
        return instance;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        instance = this;

        if(!ServiceFactory.isInitialized()){
            ServiceFactory.initialize(this);
        }

        if(!ControllerFactory.isInitialized()){
            ControllerFactory.initialize(this);
        }

        if(!ImageLoader.getInstance().isInited()){
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                    .threadPoolSize(5)
                    .build();
            ImageLoader.getInstance().init(config);
        }

        if(!ModelFactory.isInitialized()){
            ModelFactory.initialize(this);
        }

        //TODO Thread.setDefaultUncaughtExceptionHandler(new ContactsThreadFactory.ContactsUncaughtExceptionHandler()); //TODO move this if it works
    }

}
