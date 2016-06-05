package io.craigmiller160.contacts5.model;

import android.content.Context;
import android.os.Bundle;

import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import io.craigmiller160.contacts5.util.AbstractAndroidUtil;
import io.craigmiller160.utils.reflect.ReflectiveException;

/**
 * Created by craig on 6/4/16.
 */
public abstract class ModelManager extends AbstractAndroidUtil{

    private final Map<String,AndroidModel> allModels = new HashMap<>();

    protected ModelManager(Context context) {
        super(context);
    }

    public void registerModel(String modelName, AndroidModel model){
        allModels.put(modelName, model);
    }

    public void unregisterModel(String modelName){
        allModels.remove(modelName);
    }

    public void linkToModel(String modelName, PropertyChangeListener object){
        AndroidModel model = allModels.get(modelName);
        if(model == null){
            throw new IllegalArgumentException("No model registered with manager with name: " + modelName);
        }
        model.addPropertyChangeListener(object);
    }

    public void setModelProperty(String modelName, String propertyName, Object value){
        AndroidModel model = allModels.get(modelName);
        if(model == null){
            throw new IllegalArgumentException("No model registered with manager with name: " + modelName);
        }
        try{
            Method m = model.getClass().getMethod("set" + propertyName, value.getClass());
            m.invoke(model, value);
        }
        catch(NoSuchMethodException | IllegalAccessException | InvocationTargetException ex){
            throw new ReflectiveException("Unable to reflectively invoke model method", ex);
        }
    }

    public Object getModelProperty(String modelName, String propertyName){
        AndroidModel model = allModels.get(modelName);
        if(model == null){
            throw new IllegalArgumentException("No model registered with manager with name: " + modelName);
        }
        Object result = null;
        try{
            Method m = model.getClass().getMethod("get" + propertyName);
            result = m.invoke(model);
        }
        catch(NoSuchMethodException | IllegalAccessException | InvocationTargetException ex){
            throw new ReflectiveException("Unable to reflectively invoke model method", ex);
        }

        return result;
    }

    public void storeModelState(Bundle savedInstance){
        Collection<AndroidModel> values = allModels.values();
        for(AndroidModel model : values){
            model.storeState(savedInstance);
        }
    }

    public void restoreModelState(Bundle savedInstance){
        Collection<AndroidModel> values = allModels.values();
        for(AndroidModel model : values){
            model.restoreState(savedInstance);
        }
    }
}
