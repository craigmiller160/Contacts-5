package io.craigmiller160.contacts5.controller;

import io.craigmiller160.contacts5.helper.Helper;

/**
 * Created by Craig on 2/13/2016.
 */
public abstract class AbstractController {

    private Helper helper;

    protected AbstractController(Helper helper){
        this.helper = helper;
    }

    protected void setModelProperty(String propName, Object...values) throws Exception{
        helper.setModelProperty(propName,values);
    }

    protected Object getModelProperty(String propName, Object...params) throws Exception{
        return helper.getModelProperty(propName, params);
    }

}
