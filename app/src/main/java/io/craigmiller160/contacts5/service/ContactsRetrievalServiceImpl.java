package io.craigmiller160.contacts5.service;

import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;

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

import io.craigmiller160.contacts5.ContactsApp;
import io.craigmiller160.contacts5.R;
import io.craigmiller160.contacts5.model.Contact;
import io.craigmiller160.contacts5.model.ContactGroup;

import static io.craigmiller160.contacts5.util.ContactsConstants.*;

/**
 * Created by craig on 5/29/16.
 */
public class ContactsRetrievalServiceImpl extends AbstractContactsRetrievalService {

    //TODO launder the execution exceptions
    //TODO clean it up, only the all contacts task needs threads called from within threads
    //TODO unify all contacts and contacts in group tasks

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

    public ContactsRetrievalServiceImpl(Context context, AccountService accountService) {
        super(context, accountService);
    }

    @Override
    public void loadAllContacts(){
        if(Looper.myLooper() == Looper.getMainLooper()){
            executor.submit(new ExecuteAllContactsQueriesTask(getContext(), getAccountService()));
        }
        else{
            new ExecuteAllContactsQueriesTask(getContext(), getAccountService()).run();
        }
    }

    @Override
    public void loadAllGroups(){
        if(Looper.myLooper() == Looper.getMainLooper()){
            executor.submit(new ExecuteAllGroupsQueryTask(getContext(), getAccountService()));
        }
        else{
            new ExecuteAllGroupsQueryTask(getContext(), getAccountService()).run();
        }
    }

    @Override
    public void loadAllContactsInGroup(long groupId){
        if(Looper.myLooper() == Looper.getMainLooper()){
            executor.submit(new ExecuteContactsInGroupQueryTask(getContext(), groupId));
        }
        else{
            new ExecuteContactsInGroupQueryTask(getContext(), groupId).run();
        }
    }

    private static class ExecuteContactsInGroupQueryTask implements Runnable{

        private final Context context;
        private final long groupId;

        public ExecuteContactsInGroupQueryTask(Context context, long groupId){
            this.context = context;
            this.groupId = groupId;
        }

        @Override
        public void run() {
            long start = System.currentTimeMillis();
            Log.d(TAG, "Starting load contacts in group process");
            Future<List<Contact>> getContactsInGroupFuture = ContactsRetrievalServiceImpl.executor.submit(new GetContactsInGroupTask(context, groupId));

            try{
                final List<Contact> contacts = getContactsInGroupFuture.get();
                Log.d(TAG, "Total contacts in group loaded: " + contacts.size());

                ContactsApp.getApp().modelFactory().getModel(CONTACTS_MODEL).setProperty(CONTACTS_IN_GROUP_LIST, contacts);
            }
            catch(InterruptedException ex){
                Log.e(TAG, "Error while retrieving contacts in group", ex);
            }
            catch(ExecutionException ex){
                Log.e(TAG, "Error while retrieving contacts in group", ex.getCause());
            }

            long end = System.currentTimeMillis();
            Log.d(TAG, "Finished load contacts in group process. Time: " + (end - start) + "ms");
        }
    }

    private static class ExecuteAllGroupsQueryTask implements Runnable{

        private final Context context;
        private final AccountService accountService;

        public ExecuteAllGroupsQueryTask(Context context, AccountService accountService){
            this.context = context;
            this.accountService = accountService;
        }

        @Override
        public void run() {
            long start = System.currentTimeMillis();
            Log.d(TAG, "Starting load groups process");
            Future<List<ContactGroup>> getGroupsFuture = ContactsRetrievalServiceImpl.executor.submit(new GetGroupsTask(context, accountService));
            try{
                final List<ContactGroup> groups = getGroupsFuture.get();
                Log.d(TAG, "Total groups loaded: " + groups.size());

                ContactsApp.getApp().modelFactory().getModel(CONTACTS_MODEL).setProperty(GROUPS_LIST, groups);
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
        private final AccountService accountService;

        public ExecuteAllContactsQueriesTask(Context context, AccountService accountService){
            this.context = context;
            this.accountService = accountService;
        }

        @Override
        public void run() {
            long start = System.currentTimeMillis();
            Log.d(TAG, "Starting load contacts process");

            Future<Set<Long>> getExclusionsFuture = ContactsRetrievalServiceImpl.executor.submit(new GetExclusionsTask(context, accountService));
            Future<List<Contact>> getAllContactsFuture = ContactsRetrievalServiceImpl.executor.submit(new GetAllContactsTask(context));

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

                ContactsApp.getApp().modelFactory().getModel(CONTACTS_MODEL).setProperty(CONTACTS_LIST, contacts);
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

    private static class GetContactsInGroupTask implements Callable<List<Contact>>{

        private final Context context;
        private final long groupId;

        public GetContactsInGroupTask(Context context, long groupId){
            this.context = context;
            this.groupId = groupId;
        }

        @Override
        public List<Contact> call() throws Exception {
            Log.v(TAG, "Starting get contacts in group sub-task.");
            List<Contact> contacts = new ArrayList<>();
            Cursor cursor = null;
            try{
                cursor = context.getContentResolver().query(
                        DATA_URI,
                        new String[]{
                                DATA_GROUP_GROUP_ID,
                                DATA_GROUP_CONTACT_ID,
                                DATA_GROUP_CONTACT_NAME,
                                DATA_MIMETYPE_COLUMN,
                                CONTACT_HAS_PHONE,
                                DATA_GROUP_CONTACT_THUMB_PHOTO_URI
                        },
                        DATA_GROUP_GROUP_ID + " = ? and " + DATA_MIMETYPE_COLUMN + " = ?",
                        new String[]{"" + groupId, GROUP_MEMBERSHIP_MIMETYPE},
                        DATA_GROUP_CONTACT_NAME + " " + getSortOrder(CONTACT, context)
                );

                if(cursor != null){
                    cursor.moveToFirst();

                    while(!cursor.isAfterLast()){
                        int hasPhone = cursor.getInt(cursor.getColumnIndex(CONTACT_HAS_PHONE));
                        if(isPhonesOnly(context) == hasPhone){
                            long contactId = cursor.getLong(cursor.getColumnIndex(DATA_GROUP_CONTACT_ID));
                            String displayName = cursor.getString(cursor.getColumnIndex(DATA_GROUP_CONTACT_NAME));
                            Uri contactUri = ContentUris.withAppendedId(CONTACTS_URI, contactId);
                            String photoUriString = cursor.getString(cursor.getColumnIndex(DATA_GROUP_CONTACT_THUMB_PHOTO_URI));
                            Uri photoUri = null;
                            if(photoUriString != null && !photoUriString.isEmpty()){
                                photoUri = Uri.parse(photoUriString);
                            }

                            Contact contact = new Contact();
                            contact.setDisplayName(displayName);
                            contact.setUri(contactUri);
                            contact.setId(contactId);
                            contact.setPhotoUri(photoUri);

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

    private static class GetAllContactsTask implements Callable<List<Contact>>{

        private final Context context;

        public GetAllContactsTask(Context context){
            this.context = context;
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
                            long contactId = cursor.getLong(cursor.getColumnIndex(CONTACT_ID));
                            String displayName = cursor.getString(cursor.getColumnIndex(CONTACT_DISPLAY_NAME));
                            Uri contactUri = ContentUris.withAppendedId(CONTACTS_URI, contactId);
                            String photoUriString = cursor.getString(cursor.getColumnIndex(CONTACT_PHOTO_THUMBNAIL_URI));
                            Uri photoUri = null;
                            if(photoUriString != null && !photoUriString.isEmpty()){
                                photoUri = Uri.parse(photoUriString);
                            }

                            Contact contact = new Contact();
                            contact.setDisplayName(displayName);
                            contact.setUri(contactUri);
                            contact.setId(contactId);
                            contact.setPhotoUri(photoUri);

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
        private final AccountService accountService;

        public GetGroupsTask(Context context, AccountService accountService){
            this.context = context;
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
        private final AccountService accountService;

        public GetExclusionsTask(Context context, AccountService accountService){
            this.context = context;
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
