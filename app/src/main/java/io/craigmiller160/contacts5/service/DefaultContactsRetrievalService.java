package io.craigmiller160.contacts5.service;

import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.AndroidRuntimeException;
import android.util.Log;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import io.craigmiller160.contacts5.R;
import io.craigmiller160.contacts5.model.Contact;
import io.craigmiller160.contacts5.model.ContactsStorage;
import io.craigmiller160.contacts5.util.ContactsThreadFactory;

/**
 * Created by craig on 5/8/16.
 */
public class DefaultContactsRetrievalService extends AbstractContactsRetrievalService{

    //TODO add logging
    //TODO make it all able to support interruption
    //TODO think about having a thread-safe way to access the context

    private static final String[] exclusionProjections = {
            RAW_CONTACT_ID,
            RAW_CONTACT_CONTACT_ID,
            RAW_CONTACT_ACCOUNT_NAME
    };

    private static final String TAG = "ContactsRetrieveService";

    private static final ExecutorService executor = Executors.newCachedThreadPool();

    public DefaultContactsRetrievalService(Context context, ResourceService resources, AccountService accountService) {
        super(context, resources, accountService);
    }

    @Override
    public void loadAllContacts() {
        System.out.println("START TIME: " + System.currentTimeMillis());
        if(Looper.myLooper() == Looper.getMainLooper()){
            System.out.println("ON MAIN LOOPER"); //TODO delete this
            executor.submit(new ExecuteAllContactsQueriesTask(context, resources, accountService));

        }
        else{
            new ExecuteAllContactsQueriesTask(context, resources, accountService).run();
        }
    }

    private static class ExecuteAllContactsQueriesTask implements Runnable{

        private final Context context;
        private final ResourceService resources;
        private final AccountService accountService;

        public ExecuteAllContactsQueriesTask(Context context, ResourceService resources, AccountService accountService){
            this.context = context;
            this.resources = resources;
            this.accountService = accountService;
        }

        @Override
        public void run() {
            Future<ContactsStorage> getAllContactsFuture = DefaultContactsRetrievalService.executor.submit(new GetAllContactsTask(context, resources));
            Future<Set<Long>> getExclusionsFuture = DefaultContactsRetrievalService.executor.submit(new GetExclusionsTask(context, resources, accountService));

            //Thread.currentThread().setUncaughtExceptionHandler(new ContactsThreadFactory.ContactsUncaughtExceptionHandler());

            try{
                ContactsStorage storage = getAllContactsFuture.get();
                Set<Long> exclusionIds = getExclusionsFuture.get();

                System.out.println("FUTURES DONE WAITING. COUNT1: " + storage.getContactCount() + " COUNT2: " + exclusionIds.size() + " TIME: " + System.currentTimeMillis());

                //TODO need to get all groups... maybe... review ContactsStorage


                int i = 0;
                while(i < storage.getContactCount()){
                    if(exclusionIds.contains(storage.getContact(i).getId())){
                        storage.removeContact(i);
                    }
                    else{
                        i++;
                    }
                }

                System.out.println("REMOVAL DONE : " + System.currentTimeMillis());
                //TODO need to replace this part    Locus.model.setValue("ContactsStorage", storage);

                System.out.println("END TIME: " + System.currentTimeMillis());
            }
            catch(InterruptedException ex){
                Log.e(TAG, "Contacts retrieval was interrupted before it could be completed", ex);
                //TODO include better handling of this
                throw new AndroidRuntimeException("Contacts retrieval was interrupted before it could be completed", ex);
            }
            catch(ExecutionException ex){
                Log.e(TAG, "Contacts retrieval failed due to an exception", ex.getCause());
                //TODO include better handling of this
                throw new AndroidRuntimeException("Contacts retrieval failed due to an exception", ex.getCause());
            }

            System.out.println("FINAL LINE IN THE METHOD");
        }
    }

    private static class GetAllContactsTask implements Callable<ContactsStorage>{

        private final Context context;
        private final ResourceService resources;

        public GetAllContactsTask(Context context, ResourceService resources){
            this.context = context;
            this.resources = resources;
        }

        @Override
        public ContactsStorage call() throws Exception {
            ContactsStorage storage = new ContactsStorage();

            Cursor cursor = null;
            try{
                cursor = context.getContentResolver().query(
                        DATA_URI,
                        null,
                        DATA_MIMETYPE_COLUMN + " = ?",
                        new String[]{GROUP_MEMBERSHIP_MIMETYPE},
                        DATA_CONTACT_ID_COLUMN + " ASC"

                );

                System.out.println("CONTACTS QUERY DONE: " + System.currentTimeMillis() + " SIZE: " + cursor.getCount());

                if(cursor != null){
                    cursor.moveToFirst();

                    long contactId = -1;
                    Contact contact = null;
                    while(!cursor.isAfterLast()){
                        if(PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(R.string.phones_only_prop), true)){
                            if(cursor.getInt(cursor.getColumnIndex(DATA_HAS_PHONE_COLUMN)) < 1){
                                if(!cursor.moveToNext()){
                                    break;
                                }
                                else{
                                    continue;
                                }
                            }
                        }

                        long newId = cursor.getLong(cursor.getColumnIndex(DATA_CONTACT_ID_COLUMN));
                        if(newId != contactId){
                            contactId = newId;
                            String displayName = cursor.getString(cursor.getColumnIndex(DATA_DISPLAY_NAME_COLUMN));
                            Uri contactUri = ContentUris.withAppendedId(CONTACTS_URI, contactId);
                            contactUri = Uri.withAppendedPath(contactUri, CONTACTS_ENTITY_CONTENT_DIRECTORY);

                            if(contact != null){
                                storage.addContact(contact);
                            }

                            contact = new Contact();
                            contact.setId(contactId);
                            contact.setDisplayName(displayName);
                            contact.setUri(contactUri);
                        }

                        //TODO need to get accounts and groups from separate queries. Only 3 queries, not too bad

                        //TODO this method is high performing, plus makes it easy to get photo and first/last name

                        //TODO data stuff goes here
                        String mimeType = cursor.getString(cursor.getColumnIndex(DATA_MIMETYPE_COLUMN));
                        if(mimeType.equals(GROUP_MEMBERSHIP_MIMETYPE)){
                            long groupId = cursor.getLong(cursor.getColumnIndex(DATA_GROUP_ID_COLUMN));
                            contact.addGroupId(groupId);
                        }

                        if(!cursor.moveToNext()){
                            break;
                        }

                    }

                    storage.addContact(contact);
                }
            }
            finally{
                if(cursor != null){
                    cursor.close();
                }
            }

            System.out.println("CONTACTS ITERATION DONE: " + System.currentTimeMillis());

            return storage;
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
            Set<Long> contactsToExclude = new HashSet<>();
            Cursor exclusionCursor = null;
            Set<String> accountsToDisplay = PreferenceManager.getDefaultSharedPreferences(context)
                    .getStringSet(context.getString(R.string.accounts_to_display_prop),
                            accountService.getAllContactAccountNamesSet());

            try{
                exclusionCursor = context.getContentResolver().query(
                        RAW_CONTACTS_URI,
                        exclusionProjections,
                        null, null, null, null
                );

                if(exclusionCursor != null){
                    exclusionCursor.moveToFirst();
                    while(!exclusionCursor.isAfterLast()){
                        String accountName = exclusionCursor.getString(exclusionCursor.getColumnIndex(RAW_CONTACT_ACCOUNT_NAME));
                        if(!accountsToDisplay.contains(accountName)){
                            contactsToExclude.add(exclusionCursor.getLong(exclusionCursor.getColumnIndex(RAW_CONTACT_CONTACT_ID)));
                        }

                        exclusionCursor.moveToNext();
                    }
                }
            }
            finally{
                if(exclusionCursor != null){
                    exclusionCursor.close();
                }
            }

            return contactsToExclude;
        }

    }
}
