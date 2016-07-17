/*
 * Copyright 2016 ShadowAngler <craigmiller160@gmail.com> - All Rights Reserved
 * Proprietary / Confidential
 * Unauthorized copying, use, or redistribution of this file is prohibited
 */

package io.craigmiller160.contacts5.model;

import android.content.Context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import io.craigmiller160.contacts5.IllegalArgumentCtxException;
import io.craigmiller160.contacts5.util.AbstractAndroidUtil;

/**
 * Created by craig on 6/4/16.
 */
public class ModelFactory extends AbstractAndroidUtil{

    private final Map<String,AndroidModel> models = Collections.synchronizedMap(new HashMap<String, AndroidModel>());

    public ModelFactory(Context context) {
        super(context);
    }

    public int getModelCount(){
        return models.size();
    }

    public AndroidModel getModel(String modelName){
        AndroidModel model = null;
        synchronized (models){
            model = models.get(modelName);
            if(model == null){
                model = new AndroidModel(getContext());
                model.setModelName(modelName);
                models.put(modelName, model);
            }
        }
        return model;
    }

    public AndroidModel getModel(int resId){
        return getModel(getString(resId));
    }

    public <T> T getModel(String modelName, Class<T> modelType){
        AndroidModel model = getModel(modelName);
        if(model == null){
            return null;
        }

        if(modelType == null){
            throw new IllegalArgumentCtxException("Model type value cannot be null")
                    .addContextValue("Model Name", modelName);
        }

        if(!modelType.isAssignableFrom(model.getClass())){
            throw new IllegalArgumentCtxException("Invalid return type for this model")
                    .addContextValue("Model Name", modelName)
                    .addContextValue("Model Type", model.getClass().getName())
                    .addContextValue("Specified Return Type", modelType.getName());
        }
        return (T) model;
    }

    public <T> T getModel(int resId, Class<T> modelType){
        return getModel(getString(resId), modelType);
    }
}