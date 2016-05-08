package io.craigmiller160.contacts5.controller;

import android.app.Activity;
import android.content.Context;

/**
 * Created by craig on 5/4/16.
 */
public class AbstractAndroidController {

    private final Context context;

    protected AbstractAndroidController(Context context){
        this.context = context;
    }

    protected final Context getContext(){
        return context;
    }

}
