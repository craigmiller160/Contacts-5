package io.craigmiller160.contacts5.service;

import android.content.Context;

/**
 * Created by craig on 5/8/16.
 */
public abstract class AbstractContactsRetrievalService implements ContactsRetrievalService {

    protected final Context context;
    protected final ResourceService resources;
    protected final AccountService accountService;

    protected AbstractContactsRetrievalService(Context context, ResourceService resources, AccountService accountService){
        this.context = context;
        this.resources = resources;
        this.accountService = accountService;
    }

}
