package io.craigmiller160.contacts5.model;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

import io.craigmiller160.contacts5.util.AbstractAndroidUtil;

/**
 * Created by craig on 6/4/16.
 */
public abstract class AbstractModelFactory extends AbstractAndroidUtil{

    private final Map<String,AbstractModel> models = new HashMap<>();

    protected AbstractModelFactory(Context context) {
        super(context);
    }

    public void registerModel(String modelName, AbstractModel model){
        models.put(modelName, model);
    }

    public void unregisterModel(String modelName){
        models.remove(modelName);
    }

    public int getModelCount(){
        return models.size();
    }

    public AbstractModel getModel(String modelName){
        return models.get(modelName);
    }

    public <T> T getModel(String modelName, Class<T> modelType){
        AbstractModel model = getModel(modelName);
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
}
