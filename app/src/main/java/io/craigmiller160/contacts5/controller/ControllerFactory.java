package io.craigmiller160.contacts5.controller;

import android.content.Context;
import android.util.AndroidRuntimeException;

import java.util.HashMap;
import java.util.Map;

import io.craigmiller160.utils.reflect.ObjectCreator;

import static io.craigmiller160.contacts5.util.ContactsConstants.*;

/**
 * Created by craig on 5/25/16.
 */
public class ControllerFactory extends AbstractControllerFactory{

    private static ControllerFactory instance;
    private static final Object instanceLock = new Object();

    protected ControllerFactory(Context context){
        super(context);
        init();
    }

    private void init(){
        registerControllerType(ADD_CONTACT_CONTROLLER, AddContactController.class);
        registerControllerType(DISPLAY_SETTINGS_CONTROLLER, DisplaySettingsController.class);
        registerControllerType(SELECT_CONTACT_CONTROLLER, SelectContactController.class);
        registerControllerType(SELECT_GROUP_CONTROLLER, SelectGroupController.class);
    }

    public static void initialize(Context context){
        if(instance == null){
            synchronized (instanceLock){
                if(instance == null){
                    instance = new ControllerFactory(context);
                }
                else{
                    throw new AndroidRuntimeException("ServiceFactory can only be initialized once");
                }
            }
        }
        else{
            throw new AndroidRuntimeException("ServiceFactory can only be initialized once");
        }
    }

    public static boolean isInitialized(){
        synchronized (instanceLock){
            return instance != null;
        }
    }

    public static ControllerFactory getInstance(){
        if(instance == null){
            synchronized (instanceLock){
                if(instance == null){
                    throw new AndroidRuntimeException("ServiceFacotry is not initialized");
                }
            }
        }
        return instance;
    }

}
