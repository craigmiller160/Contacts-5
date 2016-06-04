package io.craigmiller160.contacts5.controller;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

import io.craigmiller160.contacts5.util.AbstractAndroidUtil;

/**
 * Created by craig on 6/4/16.
 */
public abstract class AbstractAndroidController extends AbstractAndroidUtil{

    private Map<String,Object> args = new HashMap<>();

    protected AbstractAndroidController(Context context) {
        super(context);
    }

    public void setArgs(Map<String,Object> args){
        this.args = args != null ? args : this.args;
    }

    public Map<String,Object> getArgs(){
        return args;
    }
}
