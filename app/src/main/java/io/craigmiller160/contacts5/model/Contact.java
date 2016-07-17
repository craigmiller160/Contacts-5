/*
 * Copyright 2016 ShadowAngler <craigmiller160@gmail.com> - All Rights Reserved
 * Proprietary / Confidential
 * Unauthorized copying, use, or redistribution of this file is prohibited
 */

package io.craigmiller160.contacts5.model;

import android.net.Uri;

import java.io.Serializable;

/**
 * Created by Craig on 1/20/2016.
 */
public class Contact implements Comparable<Contact>, Serializable{

    private long id;
    private String displayName;
    private String uri;

    public Contact(){}

    public Contact(long id, String displayName){
        this.id = id;
        this.displayName = displayName;
    }

    public long getId(){
        return id;
    }

    public void setId(long id){
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Uri getUri() {
        return Uri.parse(uri);
    }

    public void setUri(Uri uri) {
        this.uri = uri.toString();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Contact contact = (Contact) o;

        if (id != contact.id) return false;
        if (displayName != null ? !displayName.equals(contact.displayName) : contact.displayName != null)
            return false;
        return uri != null ? uri.equals(contact.uri) : contact.uri == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (displayName != null ? displayName.hashCode() : 0);
        result = 31 * result + (uri != null ? uri.hashCode() : 0);
        return result;
    }

    @Override
    public String toString(){
        return displayName;
    }

    @Override
    public int compareTo(Contact another) {
        return displayName.compareToIgnoreCase(another.displayName);
    }

}
