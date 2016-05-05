package io.craigmiller160.contacts5.old.controller;

import android.app.Activity;

import io.craigmiller160.contacts5.old.helper.Helper;

/**
 * Created by Craig on 2/17/2016.
 */
public abstract class AbstractAndroidController extends AbstractController{

    private Activity activity;

    protected AbstractAndroidController(){}

    protected AbstractAndroidController(Helper helper){
        super(helper);
    }

    protected AbstractAndroidController(Activity activity){
        this.activity = activity;
    }

    protected AbstractAndroidController(Activity activity, Helper helper){
        super(helper);
        this.activity = activity;
    }

    public void setActivity(Activity activity){
        this.activity = activity;
    }

    protected final Activity getActivity(){
        return activity;
    }

}
