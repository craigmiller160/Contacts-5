package io.craigmiller160.contacts5.controller;

import android.content.Context;
import android.util.AndroidRuntimeException;

import java.util.HashMap;
import java.util.Map;

import static io.craigmiller160.contacts5.util.ContactsConstants.*;

/**
 * Created by craig on 5/25/16.
 */
public class ControllerFactory {

    private static ControllerFactory instance;
    private static final Object instanceLock = new Object();

    private final Context context;

    private static final Map<String,AbstractAndroidController> controllers = new HashMap<>();


    private ControllerFactory(Context context){
        this.context = context;
    }

    private void init(){
        controllers.put(ADD_CONTACT_CONTROLLER, new AddContactController(context));
        controllers.put(DISPLAY_SETTINGS_CONTROLLER, new DisplaySettingsController(context));
    }

    public AbstractAndroidController getController(String controllerName){
        return controllers.get(controllerName);
    }

    public <T> T getController(String controllerName, Class<T> controllerType){
        AbstractAndroidController controller = getController(controllerName);
        if(controller != null){
            if(controller.getClass().equals(controllerType)){
                return (T) controller;
            }
            else{
                throw new IllegalArgumentException("Invalid type for controller: Expected: " + controller.getClass().getName() + " Actual: " + controllerType.getName());
            }
        }
        return null;
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
