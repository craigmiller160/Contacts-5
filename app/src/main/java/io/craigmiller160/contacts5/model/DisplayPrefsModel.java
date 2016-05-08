package io.craigmiller160.contacts5.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import io.craigmiller160.locus.annotations.LModel;

/**
 * A property model for the display
 * preferences of the application.
 *
 * Created by craig on 5/4/16.
 */
@LModel
public class DisplayPrefsModel {

    private Set<String> accountsToDisplay;
    private String sortOrder;
    private String sortBy;
    private String nameFormat;
    private boolean phonesOnly;

    public synchronized Set<String> getAccountsToDisplay() {
        return accountsToDisplay != null ? Collections.unmodifiableSet(accountsToDisplay) : null;
    }

    public synchronized void setAccountsToDisplay(Set<String> accountsToDisplay) {
        this.accountsToDisplay = accountsToDisplay;
    }

    public synchronized void setAccountsToDisplay(String[] accountsToDisplay){
        this.accountsToDisplay = accountsToDisplay != null ? new HashSet<>(Arrays.asList(accountsToDisplay)) : null;
    }

    public synchronized String getSortOrder() {
        return sortOrder;
    }

    public synchronized void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public synchronized String getSortBy() {
        return sortBy;
    }

    public synchronized void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public synchronized String getNameFormat() {
        return nameFormat;
    }

    public synchronized void setNameFormat(String nameFormat) {
        this.nameFormat = nameFormat;
    }

    public synchronized boolean isPhonesOnly() {
        return phonesOnly;
    }

    public synchronized void setPhonesOnly(boolean phonesOnly) {
        this.phonesOnly = phonesOnly;
    }

    public synchronized boolean addAccountsToDisplay(String s) {
        return accountsToDisplay.add(s);
    }

    public synchronized boolean removeAccountsToDisplay(Object o) {
        return accountsToDisplay.remove(o);
    }
}
