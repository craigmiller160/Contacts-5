package io.craigmiller160.contacts5.util;

import android.content.Context;
import android.content.res.Resources;

/**
 * Created by craig on 6/4/16.
 */
public abstract class AbstractAndroidUtil {

    private final Context context;

    protected AbstractAndroidUtil(Context context){
        if(context == null){
            throw new IllegalArgumentException("Context cannot be null");
        }
        this.context = context;
    }

    public Context getContext(){
        return context;
    }

    public Resources getResources(){
        return context.getResources();
    }

    public String getString(int resId){
        return context.getString(resId);
    }

}
