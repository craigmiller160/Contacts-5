package io.craigmiller160.contacts5.service;

import android.content.Context;
import android.util.AndroidRuntimeException;

/**
 * Created by craig on 5/8/16.
 */
public class ServiceFactory {

    private Context context;

    private static ServiceFactory instance;
    private static final Object instanceLock = new Object();

    private ServiceFactory(Context context){
        this.context = context;
    }

    public static void initialize(Context context){
        if(instance == null){
            synchronized (instanceLock){
                if(instance == null){
                    instance = new ServiceFactory(context);
                }
                else{
                    throw new AndroidRuntimeException("ServiceFactory can only be initialized once");
                }
            }
        }
        else{
            throw new AndroidRuntimeException("ServiceFactory can only be initialized once");
        }
    }

    public static boolean isInitialized(){
        synchronized (instanceLock){
            return instance != null;
        }
    }

    public static ServiceFactory getInstance(){
        if(instance == null){
            synchronized (instanceLock){
                if(instance == null){
                    throw new AndroidRuntimeException("ServiceFacotry is not initialized");
                }
            }
        }
        return instance;
    }

    public AccountService getAccountService(){
        return new AccountService(context);
    }

    public PermissionsService getPermissionsService(){
        return new PermissionsService(context);
    }

    public ResourceService getResourceService(){
        return new ResourceService(context);
    }

    public ContactsPrefsService getContactsPrefsService(){
        return new ContactsPrefsService(context, getAccountService());
    }

}
