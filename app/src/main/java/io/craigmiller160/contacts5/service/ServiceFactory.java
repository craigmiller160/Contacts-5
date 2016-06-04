package io.craigmiller160.contacts5.service;

import android.content.Context;
import android.util.AndroidRuntimeException;

/**
 * Created by craig on 5/8/16.
 */
public class ServiceFactory {

    private final Context context;

    private final AccountService accountService;
    private final ContactsPrefsService contactsPrefsService;
    private final ContactsRetrievalService contactsRetrievalService;
    private final PermissionsService permissionsService;
    private final ResourceService resourceService;
    private final ContactIconService contactIconService;

    private static ServiceFactory instance;
    private static final Object instanceLock = new Object();

    private ServiceFactory(Context context){
        this.context = context;
        this.accountService = new AccountService(context);
        this.resourceService = new ResourceService(context);
        this.contactsPrefsService = new ContactsPrefsService(context, accountService, resourceService);
        this.contactsRetrievalService = new ContactsRetrievalServiceImpl(context, resourceService, accountService);
        this.permissionsService = new PermissionsService(context);
        this.contactIconService = new ContactIconService(context);
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
        return accountService;
    }

    public PermissionsService getPermissionsService(){
        return permissionsService;
    }

    public ResourceService getResourceService(){
        return resourceService;
    }

    public ContactsPrefsService getContactsPrefsService(){
        return contactsPrefsService;
    }

    public ContactsRetrievalService getContactsRetrievalService(){
        return contactsRetrievalService;
    }

    public ContactIconService getContactIconService(){
        return contactIconService;
    }

}
