package io.craigmiller160.contacts5.controller;

import android.app.Activity;

/**
 * Created by craig on 5/4/16.
 */
public class AbstractAndroidController {

    private final Activity activity;

    protected AbstractAndroidController(Activity activity){
        this.activity = activity;
    }

    protected final Activity getActivity(){
        return activity;
    }

}
