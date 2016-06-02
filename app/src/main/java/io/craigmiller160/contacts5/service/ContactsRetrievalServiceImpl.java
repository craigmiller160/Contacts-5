package io.craigmiller160.contacts5.service;

import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import io.craigmiller160.contacts5.R;
import io.craigmiller160.contacts5.model.Contact;
import io.craigmiller160.contacts5.model.ContactGroup;
import io.craigmiller160.contacts5.model.ContactsDataCallback;

/**
 * Created by craig on 5/29/16.
 */
public class ContactsRetrievalServiceImpl extends AbstractContactsRetrievalService {

    //TODO launder the execution exceptions

    private static final ExecutorService executor = Executors.newFixedThreadPool(4);
    private static final String TAG = "ContactsRetrievService";

    private static final int CONTACT = 101;
    private static final int GROUP = 102;

    private static String getSortOrder(int type, Context context){
        String result = "ASC"; //TODO this default may not ultimately be necessary
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        switch(type){
            case CONTACT:
                result = prefs.getString(context.getString(R.string.sort_order_prop),
                        context.getResources().getStringArray(R.array.sort_order_values)[0]);
                break;
            case GROUP:
                //TODO group sort order setting???
                break;
        }

        return result;
    }

    private static Set<String> getAccountsToDisplay(Context context, AccountService accountService){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getStringSet(context.getString(R.string.accounts_to_display_prop), accountService.getAllContactAccountNamesSet());
    }

    private static int isPhonesOnly(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(context.getString(R.string.phones_only_prop), true) ? 1 : 0;
    }

    public ContactsRetrievalServiceImpl(Context context, ResourceService resources, AccountService accountService) {
        super(context, resources, accountService);
    }

    @Override
    public void loadAllContacts(ContactsDataCallback callback){
        if(Looper.myLooper() == Looper.getMainLooper()){
            executor.submit(new ExecuteAllContactsQueriesTask(context, resources, accountService, callback));
        }
        else{
            new ExecuteAllContactsQueriesTask(context, resources, accountService, callback).run();
        }
    }

    @Override
    public void loadAllGroups(ContactsDataCallback callback){
        if(Looper.myLooper() == Looper.getMainLooper()){
            executor.submit(new ExecuteAllGroupsQueryTask(context, resources, accountService, callback));
        }
        else{
            new ExecuteAllGroupsQueryTask(context, resources, accountService, callback);
        }
    }

    private static class ExecuteAllGroupsQueryTask implements Runnable{

        private final Context context;
        private final ResourceService resources;
        private final AccountService accountService;
        private final ContactsDataCallback callback;

        public ExecuteAllGroupsQueryTask(Context context, ResourceService resources,
                                         AccountService accountService, ContactsDataCallback callback){
            this.context = context;
            this.resources = resources;
            this.accountService = accountService;
            this.callback = callback;
        }

        @Override
        public void run() {
            long start = System.currentTimeMillis();
            Log.d(TAG, "Starting load groups process");
            Future<List<ContactGroup>> getGroupsFuture = ContactsRetrievalServiceImpl.executor.submit(new GetGroupsTask(context, resources, accountService));
            try{
                final List<ContactGroup> groups = getGroupsFuture.get();
                Log.d(TAG, "Total groups loaded: " + groups.size());

                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.setGroupsList(groups);
                    }
                });
            }
            catch(InterruptedException ex){
                Log.e(TAG, "Error while retrieving groups", ex);
            }
            catch(ExecutionException ex){
                Log.e(TAG, "Error while retrieving groups", ex.getCause());
            }

            long end = System.currentTimeMillis();
            Log.d(TAG, "Finished loading all groups. Time: " + (end - start) + "ms");
        }
    }

    private static class ExecuteAllContactsQueriesTask implements Runnable{

        private final Context context;
        private final ResourceService resources;
        private final AccountService accountService;
        private final ContactsDataCallback callback;

        public ExecuteAllContactsQueriesTask(Context context, ResourceService resources,
                                             AccountService accountService, ContactsDataCallback callback){
            this.context = context;
            this.resources = resources;
            this.accountService = accountService;
            this.callback = callback;
        }

        @Override
        public void run() {
            long start = System.currentTimeMillis();
            Log.d(TAG, "Starting load contacts process");

            Future<Set<Long>> getExclusionsFuture = ContactsRetrievalServiceImpl.executor.submit(new GetExclusionsTask(context, resources, accountService));
            Future<List<Contact>> getAllContactsFuture = ContactsRetrievalServiceImpl.executor.submit(new GetAllContactsTask(context, resources));

            try{
                Set<Long> exclusions = getExclusionsFuture.get();
                final List<Contact> contacts = getAllContactsFuture.get();

                Iterator<Contact> contactIterator = contacts.iterator();
                while(contactIterator.hasNext()){
                    Contact contact = contactIterator.next();
                    if(exclusions.contains(contact.getId())){
                        contactIterator.remove();
                    }
                }

                Log.d(TAG, "Total contacts loaded: " + contacts.size());


                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.setContactsList(contacts);
                    }
                });
            }
            catch(InterruptedException ex){
                Log.e(TAG, "Error while retrieving contacts", ex);
            }
            catch(ExecutionException ex){
                Log.e(TAG, "Error while retrieving contacts", ex.getCause());
            }

            long end = System.currentTimeMillis();
            Log.d(TAG, "Finished loading all contacts. Total time: " + (end - start) + "ms");
        }
    }

    private static class GetAllContactsTask implements Callable<List<Contact>>{

        private final Context context;
        private final ResourceService resources;

        public GetAllContactsTask(Context context, ResourceService resources){
            this.context = context;
            this.resources = resources;
        }

        @Override
        public List<Contact> call() throws Exception {
            Log.v(TAG, "Starting get all contacts sub-task");
            List<Contact> contacts = new ArrayList<>();
            Cursor cursor = null;
            try{
                cursor = context.getContentResolver().query(
                        CONTACTS_URI,
                        new String[]{CONTACT_DISPLAY_NAME, CONTACT_HAS_PHONE, CONTACT_PHOTO_THUMBNAIL_URI, CONTACT_ID},
                        null, null, CONTACT_DISPLAY_NAME + " " + getSortOrder(CONTACT, context)
                );

                if(cursor != null){
                    cursor.moveToFirst();

                    while(!cursor.isAfterLast()){
                        int hasPhone = cursor.getInt(cursor.getColumnIndex(CONTACT_HAS_PHONE));
                        if(isPhonesOnly(context) == hasPhone){
                            //TODO get the phone thumbnail URI here
                            long contactId = cursor.getLong(cursor.getColumnIndex(CONTACT_ID));
                            String displayName = cursor.getString(cursor.getColumnIndex(CONTACT_DISPLAY_NAME));
                            Uri contactUri = ContentUris.withAppendedId(CONTACTS_URI, contactId);

                            Contact contact = new Contact();
                            contact.setDisplayName(displayName);
                            contact.setUri(contactUri);
                            contact.setId(contactId);

                            contacts.add(contact);
                        }

                        cursor.moveToNext();
                    }
                }
            }
            finally{
                if(cursor != null){
                    cursor.close();
                }
            }

            return contacts;
        }
    }

    private static class GetGroupsTask implements Callable<List<ContactGroup>> {

        private final Context context;
        private final ResourceService resources;
        private final AccountService accountService;

        public GetGroupsTask(Context context, ResourceService resources, AccountService accountService){
            this.context = context;
            this.resources = resources;
            this.accountService = accountService;
        }

        @Override
        public List<ContactGroup> call() throws Exception {
            Log.v(TAG, "Starting get groups subtask");
            List<ContactGroup> groups = new ArrayList<>();
            Set<String> accountsToDisplay = getAccountsToDisplay(context, accountService);
            Cursor cursor = null;
            try{
                cursor = context.getContentResolver().query(
                        GROUP_URI,
                        new String[]{GROUP_ID, GROUP_TITLE, GROUP_ACCOUNT_NAME, GROUP_COUNT, GROUP_COUNT_PHONES},
                        null, null, GROUP_TITLE + " " + getSortOrder(GROUP, context)
                );

                if(cursor != null){
                    Log.v(TAG, "Total number of group query rows: " + cursor.getCount());

                    cursor.moveToFirst();
                    while(!cursor.isAfterLast()){
                        String accountName = cursor.getString(cursor.getColumnIndex(GROUP_ACCOUNT_NAME));
                        if(accountsToDisplay.contains(accountName)){
                            ContactGroup group = new ContactGroup();
                            group.setGroupId(cursor.getLong(cursor.getColumnIndex(GROUP_ID)));
                            group.setGroupName(cursor.getString(cursor.getColumnIndex(GROUP_TITLE)));
                            group.setAccountName(cursor.getString(cursor.getColumnIndex(GROUP_ACCOUNT_NAME)));

                            if(isPhonesOnly(context) == 1){
                                group.setGroupSize(cursor.getInt(cursor.getColumnIndex(GROUP_COUNT_PHONES)));
                            }
                            else{
                                group.setGroupSize(cursor.getInt(cursor.getColumnIndex(GROUP_COUNT)));
                            }

                            groups.add(group);
                        }

                        cursor.moveToNext();
                    }
                }
            }
            finally {
                if(cursor != null){
                    cursor.close();
                }
            }

            return groups;
        }
    }

    private static class GetExclusionsTask implements Callable<Set<Long>>{

        private final Context context;
        private final ResourceService resources;
        private final AccountService accountService;

        public GetExclusionsTask(Context context, ResourceService resources, AccountService accountService){
            this.context = context;
            this.resources = resources;
            this.accountService = accountService;
        }

        @Override
        public Set<Long> call() throws Exception {
            Log.v(TAG, "Starting get exclusions subtask");
            Set<Long> contactsToExclude = new HashSet<>();
            Cursor cursor = null;
            Set<String> accountsToDisplay = PreferenceManager.getDefaultSharedPreferences(context)
                    .getStringSet(context.getString(R.string.accounts_to_display_prop),
                            accountService.getAllContactAccountNamesSet());

            try{
                cursor = context.getContentResolver().query(
                        RAW_CONTACTS_URI,
                        new String[]{RAW_CONTACT_ID, RAW_CONTACT_CONTACT_ID, RAW_CONTACT_ACCOUNT_NAME},
                        null, null, null, null
                );

                if(cursor != null){
                    cursor.moveToFirst();
                    while(!cursor.isAfterLast()){
                        String accountName = cursor.getString(cursor.getColumnIndex(RAW_CONTACT_ACCOUNT_NAME));
                        if(!accountsToDisplay.contains(accountName)){
                            contactsToExclude.add(cursor.getLong(cursor.getColumnIndex(RAW_CONTACT_CONTACT_ID)));
                        }

                        cursor.moveToNext();
                    }
                }
            }
            finally{
                if(cursor != null){
                    cursor.close();
                }
            }

            return contactsToExclude;
        }

    }
}
