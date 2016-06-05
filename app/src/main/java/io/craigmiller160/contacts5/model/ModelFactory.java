package io.craigmiller160.contacts5.model;

import android.content.Context;
import android.util.AndroidRuntimeException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import io.craigmiller160.contacts5.util.AbstractAndroidUtil;

/**
 * Created by craig on 6/4/16.
 */
public class ModelFactory extends AbstractAndroidUtil{

    private static ModelFactory instance;
    private static final Object instanceLock = new Object();

    private final Map<String,AndroidModel> models = Collections.synchronizedMap(new HashMap<String, AndroidModel>());

    protected ModelFactory(Context context) {
        super(context);
    }

    protected void registerModel(String modelName, AndroidModel model){
        models.put(modelName, model);
    }

    protected void unregisterModel(String modelName){
        models.remove(modelName);
    }

    public int getModelCount(){
        return models.size();
    }

    public AndroidModel getModel(String modelName){
        AndroidModel model = null;
        synchronized (models){
            model = models.get(modelName);
            if(model == null){
                model = new AndroidModel();
                model.setModelName(modelName);
                models.put(modelName, model);
            }
        }
        return model;
    }

    public <T> T getModel(String modelName, Class<T> modelType){
        AndroidModel model = getModel(modelName);
        if(model == null){
            return null;
        }

        if(modelType == null){
            throw new IllegalArgumentException("Model type value cannot be null");
        }

        if(!modelType.isAssignableFrom(model.getClass())){
            throw new IllegalArgumentException(String.format("Invalid type for model. %1$s is not assignable from %2$s", modelType.getName(), model.getClass().getName()));
        }
        return (T) model;
    }

    public static void initialize(Context context){
        if(instance == null){
            synchronized (instanceLock){
                if(instance == null){
                    instance = new ModelFactory(context);
                }
                else{
                    throw new AndroidRuntimeException("ModelFactory can only be initialized once");
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

    public static ModelFactory getInstance(){
        if(instance == null){
            synchronized (instanceLock){
                if(instance == null){
                    throw new AndroidRuntimeException("ModelFactory is not initialized");
                }
            }
        }
        return instance;
    }
}