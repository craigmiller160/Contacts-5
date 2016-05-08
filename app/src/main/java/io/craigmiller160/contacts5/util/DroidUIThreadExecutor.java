package io.craigmiller160.contacts5.util;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import io.craigmiller160.locus.concurrent.UIThreadExecutor;
import io.craigmiller160.utils.reflect.ExceptionHandler;

/**
 * Created by craig on 5/8/16.
 */
public class DroidUIThreadExecutor implements UIThreadExecutor {

    @Override
    public void executeOnUIThread(Runnable runnable) {
        if(Looper.myLooper() == Looper.getMainLooper()){
            runnable.run();
        }
        else{
            new Handler(Looper.getMainLooper()).post(runnable);
        }
    }

    @Override
    public <T> T executeOnUIThreadWithResult(Callable<T> callable) {
        T result = null;
        try{
            if(Looper.myLooper() == Looper.getMainLooper()){
                result = callable.call();
            }
            else{
                FutureTask<T> future = new FutureTask<>(callable);
                new Handler(Looper.getMainLooper()).post(future);
                result = future.get();
            }
        }
        catch(Exception ex){
            ExceptionHandler.parseAndRethrowException(ex);
        }

        return result;
    }
}
