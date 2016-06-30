package io.craigmiller160.contacts5.controller;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

import io.craigmiller160.contacts5.IllegalArgumentCtxException;
import io.craigmiller160.contacts5.util.AbstractAndroidUtil;

/**
 * Created by craig on 6/4/16.
 */
public abstract class AbstractAndroidController extends AbstractAndroidUtil{

    private Map<String,Object> args = new HashMap<>();

    protected AbstractAndroidController(Context context) {
        this(context, null);
    }

    protected AbstractAndroidController(Context context, Map<String,Object> args){
        super(context);
        setArgs(args);
    }

    public void setArgs(Map<String,Object> args){
        this.args = args != null ? args : this.args;
    }

    public void addArg(String key, Object value){
        args.put(key, value);
    }

    public void addArg(int resId, Object value){
        args.put(getString(resId), value);
    }

    public Map<String,Object> getArgs(){
        return args;
    }

    public Object getArg(String key){
        return args.get(key);
    }

    public Object getArg(int resId){
        return args.get(getString(resId));
    }

    public <T> T getArg(String key, Class<T> returnType){
        Object value = getArg(key);
        if(value == null){
            return null;
        }

        if(returnType == null){
            throw new IllegalArgumentCtxException("Return type value cannot be null")
                    .addContextValue("Arg Key", key);
        }

        if(!returnType.isAssignableFrom(value.getClass())){
            throw new IllegalArgumentCtxException("Invalid type for argument value")
                    .addContextValue("Arg Key", key)
                    .addContextValue("Arg Value Type", value.getClass().getName())
                    .addContextValue("Specified Return Type", returnType.getName());
        }
        return (T) value;
    }

    public <T> T getArg(int resId, Class<T> returnType){
        return getArg(getString(resId), returnType);
    }
}
