package io.craigmiller160.contacts5.fragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.AndroidRuntimeException;
import android.widget.ArrayAdapter;

import io.craigmiller160.contacts5.adapter.ContactsArrayAdapter;
import io.craigmiller160.contacts5.adapter.GroupsArrayAdapter;
import io.craigmiller160.contacts5.model.Contact;
import io.craigmiller160.contacts5.model.ContactGroup;
import io.craigmiller160.contacts5.util.AbstractAndroidUtil;

import static io.craigmiller160.contacts5.util.ContactsConstants.*;

/**
 * Created by craig on 6/5/16.
 */
public class ContactsFragmentFactory extends AbstractAndroidUtil{

    private static ContactsFragmentFactory instance;
    private static final Object instanceLock = new Object();

    protected ContactsFragmentFactory(Context context) {
        super(context);
    }

    public Fragment createAllContactsFragment(){
        return new AllContactsFragment();
    }

    public Fragment createContactsInGroupFragment(){
        return new ContactsInGroupFragment();
    }

    public Fragment createAllGroupsFragment(){
        return new AllGroupsFragment();
    }

    public static void initialize(Context context){
        if(instance == null){
            synchronized (instanceLock){
                if(instance == null){
                    instance = new ContactsFragmentFactory(context);
                }
                else{
                    throw new AndroidRuntimeException("ModelFactory can only be initialized once");
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

    public static ContactsFragmentFactory getInstance(){
        if(instance == null){
            synchronized (instanceLock){
                if(instance == null){
                    throw new AndroidRuntimeException("ModelFactory is not initialized");
                }
            }
        }
        return instance;
    }

    public static class AllContactsFragment extends AbstractContactsFragment<Contact>{

        @Override
        protected ArrayAdapter<Contact> getArrayAdapter() {
            return new ContactsArrayAdapter(getContext(), CONTACTS_LIST);
        }
    }

    public static class ContactsInGroupFragment extends AbstractContactsFragment<Contact>{

        @Override
        protected ArrayAdapter<Contact> getArrayAdapter() {
            return new ContactsArrayAdapter(getContext(), CONTACTS_IN_GROUP_LIST);
        }
    }

    public static class AllGroupsFragment extends AbstractContactsFragment<ContactGroup>{

        @Override
        protected ArrayAdapter<ContactGroup> getArrayAdapter() {
            return new GroupsArrayAdapter(getContext());
        }
    }
}
