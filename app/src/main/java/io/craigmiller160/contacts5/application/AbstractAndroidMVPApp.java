package io.craigmiller160.contacts5.application;

import android.app.Activity;
import android.app.Application;
import android.view.View;

import io.craigmiller160.contacts5.controller.AbstractAndroidController;
import io.craigmiller160.contacts5.controller.AbstractController;
import io.craigmiller160.contacts5.helper.Helper;
import io.craigmiller160.contacts5.helper.HelperBuilder;
import io.craigmiller160.contacts5.view.MVPView;

/**
 * Created by Craig on 2/17/2016.
 */
public abstract class AbstractAndroidMVPApp extends Application{

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

    public AbstractController getController(String controllerName, Activity activity){
        AbstractAndroidController controller = (AbstractAndroidController) helper.getController(controllerName);
        controller.setActivity(activity);
        return controller;
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
