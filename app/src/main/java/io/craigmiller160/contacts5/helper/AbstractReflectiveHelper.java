package io.craigmiller160.contacts5.helper;

import java.beans.PropertyChangeEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.craigmiller160.contacts5.MVPException;
import io.craigmiller160.contacts5.model.AbstractModel;
import io.craigmiller160.contacts5.view.MVPView;
import io.craigmiller160.reflection.FindAndInvoke;

/**
 * Created by Craig on 2/14/2016.
 */
public abstract class AbstractReflectiveHelper extends AbstractHelper{

    //TODO document how this implementation is only good for a single matching method across all models

    @Override
    public final Object getModelProperty(String propName, Object...params) throws MVPException{
        Object result = null;
        try{
            result = FindAndInvoke.findAndInvokeMethod(getModels(), "get" + propName, params);
        }
        catch(InvocationTargetException ex){
            throw new MVPException("Failed to get model property: " + propName + " " + Arrays.toString(params), ex.getCause());
        }
        catch(ReflectiveOperationException ex){
            ex.printStackTrace(); //TODO delete this
            throw new MVPException("Failed to get model property: " + propName + " " + Arrays.toString(params), ex);
        }
        return result;
    }

    @Override
    public final void setModelProperty(String propName, Object...values) throws MVPException{
        try{
            FindAndInvoke.findAndInvokeMethod(getModels(), "set" + propName, values);
        }
        catch(InvocationTargetException ex){
            throw new MVPException("Failed to set model property: " + propName + " " + Arrays.toString(values), ex.getCause());
        }
        catch(ReflectiveOperationException ex){
            throw new MVPException("Failed to set model property: " + propName + " " + Arrays.toString(values), ex);
        }
    }
}
