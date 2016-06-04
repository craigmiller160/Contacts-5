package io.craigmiller160.contacts5.model;

import android.app.Activity;
import android.content.Context;

import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

import io.craigmiller160.contacts5.util.AbstractAndroidUtil;

/**
 * Created by craig on 6/4/16.
 */
public abstract class ActivityModelManager extends AbstractAndroidUtil{

    private final Map<Class<Activity>,Object> activityModels = new HashMap<>();

    protected ActivityModelManager(Context context) {
        super(context);
    }

    public void registerActivityModel(Class<Activity> activityType, Object model){
        if(!PropertyChangeListener.class.isAssignableFrom(activityType)){
            throw new IllegalArgumentException("Only activities implementing the PropertyChangeListener interface can be added here");
        }

        activityModels.put(activityType, model);
    }
}
