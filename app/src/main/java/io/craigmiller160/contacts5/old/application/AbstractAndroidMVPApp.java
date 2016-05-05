package io.craigmiller160.contacts5.old.application;

import android.app.Activity;
import android.app.Application;
import android.util.Log;
import android.view.View;

import java.util.Arrays;

import io.craigmiller160.contacts5.old.MVPException;
import io.craigmiller160.contacts5.old.controller.AbstractAndroidController;
import io.craigmiller160.contacts5.old.controller.AbstractController;
import io.craigmiller160.contacts5.old.helper.Helper;
import io.craigmiller160.contacts5.old.helper.HelperBuilder;
import io.craigmiller160.contacts5.old.view.MVPView;

/**
 * Created by Craig on 2/17/2016.
 */
public abstract class AbstractAndroidMVPApp extends Application{

    private static final String TAG = "AbstractMVPApp";

    private Helper helper;

    @Override
    public void onCreate(){
        super.onCreate();
        buildHelper();
    }

    protected void buildHelper(){
        this.helper = createHelperBuilder().buildNewHelper();
    }

    protected abstract HelperBuilder createHelperBuilder();

    public Helper getHelper(){
        return helper;
    }

    public AbstractController getController(String controllerName, Activity activity){
        AbstractAndroidController controller = (AbstractAndroidController) helper.getController(controllerName);
        controller.setActivity(activity);
        return controller;
    }

    public Object getModelProperty(String propName, Object...params){
        Object result = null;
        try{
            result = helper.getModelProperty(propName, params);
        }
        catch(MVPException ex){
            Log.e(TAG, "Unable to get property. Name: " + propName + " Params: " + Arrays.toString(params), ex);
        }
        return result;
    }

    public void setModelProperty(String propName, Object...values){
        try{
            helper.setModelProperty(propName, values);
        }
        catch(MVPException ex){
            Log.e(TAG, "Unable to set property. Name: " + propName + " Values: " + Arrays.toString(values), ex);
        }
    }

    public View.OnClickListener getOnClickController(String controllerName, Activity activity){
        AbstractController controller = getController(controllerName, activity);
        if(controller != null && controller instanceof View.OnClickListener){
            return (View.OnClickListener) controller;
        }
        return null;
    }

    public void addView(MVPView view){
        helper.addView(view);
    }

    public void removeView(MVPView view){
        helper.removeView(view);
    }

}
