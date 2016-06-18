package io.craigmiller160.contacts5.service;

import android.content.Context;

/**
 * Created by craig on 5/8/16.
 */
public class ServiceFactory {

    private final Context context;

    private final AccountService accountService;
    private final ContactsRetrievalService contactsRetrievalService;
    private final PermissionsService permissionsService;

    private static ServiceFactory instance;
    private static final Object instanceLock = new Object();

    public ServiceFactory(Context context){
        this.context = context;
        this.accountService = new AccountService(context);
        this.contactsRetrievalService = new ContactsRetrievalServiceImpl(context, accountService);
        this.permissionsService = new PermissionsService(context);
    }

    public AccountService getAccountService(){
        return accountService;
    }

    public PermissionsService getPermissionsService(){
        return permissionsService;
    }

    public ContactsRetrievalService getContactsRetrievalService(){
        return contactsRetrievalService;
    }

}
