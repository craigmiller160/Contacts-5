package io.craigmiller160.contacts5.helper;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import io.craigmiller160.contacts5.MVPException;
import io.craigmiller160.utils.reflect.FindAndInvoke;

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
