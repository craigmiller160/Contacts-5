package io.craigmiller160.contacts5;

import android.app.Application;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.utils.L;

import io.craigmiller160.contacts5.controller.ControllerFactory;
import io.craigmiller160.contacts5.model.ModelFactory;
import io.craigmiller160.contacts5.service.ServiceFactory;
import io.craigmiller160.contacts5.util.ContactsUncaughtExceptionHandler;

/**
 * The application class for Contacts 5+.
 *
 * Created by craig on 5/4/16.
 */
public class ContactsApp extends Application {

    private static ContactsApp instance;

    private static final Object factoryLock = new Object();

    private ServiceFactory serviceFactory;
    private ControllerFactory controllerFactory;
    private ModelFactory modelFactory;

    public static ContactsApp getApp(){
        return instance;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        instance = this;

        synchronized (factoryLock){
            serviceFactory = new ServiceFactory(this);
            controllerFactory = new ControllerFactory(this);
            modelFactory = new ModelFactory(this);
        }

        if(!ImageLoader.getInstance().isInited()){
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                    .threadPoolSize(5)
                    .build();
            ImageLoader.getInstance().init(config);
        }

        //Disable UniversalImageLoader logging
        L.writeLogs(false);

        Thread.setDefaultUncaughtExceptionHandler(new ContactsUncaughtExceptionHandler());
    }

    public ServiceFactory serviceFactory(){
        synchronized (factoryLock){
            return serviceFactory;
        }
    }

    public ControllerFactory controllerFactory(){
        synchronized (factoryLock){
            return controllerFactory;
        }
    }

    public ModelFactory modelFactory(){
        synchronized (factoryLock){
            return modelFactory;
        }
    }

}
