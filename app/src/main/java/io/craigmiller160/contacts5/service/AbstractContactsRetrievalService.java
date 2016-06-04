package io.craigmiller160.contacts5.service;

import android.content.Context;

import io.craigmiller160.contacts5.util.AbstractAndroidUtil;

/**
 * Created by craig on 5/8/16.
 */
public abstract class AbstractContactsRetrievalService extends AbstractAndroidUtil implements ContactsRetrievalService {

    private final AccountService accountService;

    protected AbstractContactsRetrievalService(Context context, AccountService accountService){
        super(context);
        this.accountService = accountService;
    }

    public AccountService getAccountService(){
        return accountService;
    }

}
