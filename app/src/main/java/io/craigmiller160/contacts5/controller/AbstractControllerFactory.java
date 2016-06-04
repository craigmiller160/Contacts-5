package io.craigmiller160.contacts5.controller;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

import io.craigmiller160.contacts5.util.AbstractAndroidUtil;
import io.craigmiller160.utils.reflect.ObjectCreator;

/**
 * Created by craig on 6/4/16.
 */
public abstract class AbstractControllerFactory extends AbstractAndroidUtil {

    private final Map<String,Class<? extends AbstractAndroidController>> controllers = new HashMap<>();

    protected AbstractControllerFactory(Context context) {
        super(context);
    }

    public void registerControllerType(String controllerName, Class<? extends AbstractAndroidController> controllerType){
        controllers.put(controllerName, controllerType);
    }

    public void unregisterControllerType(String controllerName){
        controllers.remove(controllerName);
    }

    public int getControllerTypeCount(){
        return controllers.size();
    }

    public Class<? extends AbstractAndroidController> getControllerType(String controllerName){
        return controllers.get(controllerName);
    }

    public AbstractAndroidController getController(String controllerName){
        return getController(controllerName, new HashMap<String, Object>());
    }

    public AbstractAndroidController getController(String controllerName, Map<String,Object> args){
        Class<? extends AbstractAndroidController> controllerClass = getControllerType(controllerName);
        if(controllerClass == null){
            throw new IllegalArgumentException("Invalid controller name: " + controllerName);
        }

        AbstractAndroidController controller = ObjectCreator.instantiateClassWithParams(controllerClass, getContext());
        if(controller != null){
            controller.setArgs(args);
        }
        return controller;
    }

    public <T> T getController(String controllerName, Class<T> controllerType){
        return getController(controllerName, controllerType, new HashMap<String, Object>());
    }

    public <T> T getController(String controllerName, Class<T> controllerType, Map<String,Object> args){
        if(controllerType == null){
            throw new IllegalArgumentException("ControllerType argument cannot be null");
        }

        AbstractAndroidController controller = getController(controllerName, args);
        if(controller != null){
            if(controllerType.isAssignableFrom(controller.getClass())){
                return (T) controller;
            }
            else{
                throw new IllegalArgumentException(String.format("Invalid type for controller. %1$s is not assignable from %2$s", controllerType.getName(), controller.getClass().getName()));
            }
        }
        return null;
    }
}
