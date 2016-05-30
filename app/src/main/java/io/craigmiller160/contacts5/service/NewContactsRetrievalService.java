package io.craigmiller160.contacts5.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import io.craigmiller160.contacts5.R;
import io.craigmiller160.contacts5.model.Contact;
import io.craigmiller160.contacts5.model.ContactGroup;
import io.craigmiller160.contacts5.model.ContactsHolder;

/**
 * Created by craig on 5/26/16.
 */
public class NewContactsRetrievalService /*extends AbstractContactsRetrievalService*/{

    //TODO prevent SQL injection by using appendQueryParameter API

//    private static final ExecutorService executor = Executors.newFixedThreadPool(12);
//    private static final String TAG = "ContactsRetrievService";
//
//    private static final int CONTACT = 101;
//    private static final int GROUP = 102;
//
//    private static final long SUB_TASK_SIZE = 1000;
//
//    private static String getSortOrder(int type, Context context){
//        String result = "ASC"; //TODO this default may not ultimately be necessary
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
//        switch(type){
//            case CONTACT:
//                result = prefs.getString(context.getString(R.string.sort_order_prop),
//                        context.getResources().getStringArray(R.array.sort_order_values)[0]);
//                break;
//            case GROUP:
//                //TODO group sort order setting???
//                break;
//        }
//
//        return result;
//    }
//
//    private static Set<String> getAccountsToDisplay(Context context, AccountService accountService){
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
//        return prefs.getStringSet(context.getString(R.string.accounts_to_display_prop), accountService.getAllContactAccountNamesSet());
//    }
//
//    public NewContactsRetrievalService(Context context, ResourceService resources, AccountService accountService) {
//        super(context, resources, accountService);
//    }
//
//    /*
//     * How this works
//     *
//     * 1) Get the count of all rows
//     * 2) Chop it up by 1000 rows per thread until reaching that limit
//     * 3) Re-assemble at the end
//     */
//
//    @Override
//    public void loadAllContacts(){
//        ContactsHolder holder = new ContactsHolder();
//        if(Looper.myLooper() == Looper.getMainLooper()){
//            executor.submit(new ExecuteAllContactsQueriesTask(context, resources, accountService, holder));
//        }
//        else{
//            new ExecuteAllContactsQueriesTask(context, resources, accountService, holder).run();
//        }
//
//        //TODO need to figure out a way to have this service put the contacts list in the right place without having to return a value
//    }
//
//    private static class ExecuteAllContactsQueriesTask implements Runnable{
//
//        private final Context context;
//        private final ResourceService resources;
//        private final AccountService accountService;
//        private final ContactsHolder holder;
//
//        public ExecuteAllContactsQueriesTask(Context context, ResourceService resources,
//                                             AccountService accountService, ContactsHolder holder){
//            this.context = context;
//            this.resources = resources;
//            this.accountService = accountService;
//            this.holder = holder;
//        }
//
//        @Override
//        public void run() {
//            Future<Set<Long>> getExclusionsFuture = NewContactsRetrievalService.executor.submit(new GetExclusionsTask(context, resources, accountService));
//            Future<List<ContactGroup>> getGroupsFuture = NewContactsRetrievalService.executor.submit(new GetGroupsTask(context, resources, accountService));
//            Future<List<Contact>> getAllContactsFuture = NewContactsRetrievalService.executor.submit(new GetAllContactsTask(context, resources));
//
//            //TODO finish this
//        }
//    }
//
//    private static class GetAllContactsTask implements Callable<List<Contact>>{
//
//        private final Context context;
//        private final ResourceService resources;
//
//        public GetAllContactsTask(Context context, ResourceService resources){
//            this.context = context;
//            this.resources = resources;
//        }
//
//        @Override
//        public List<Contact> call() throws Exception {
//            Cursor cursor = null;
//            try{
//                cursor = context.getContentResolver().query(
//                        DATA_URI,
//                        null,
//                        DATA_MIMETYPE_COLUMN + " in (?, ?, ?, ?)",
//                        new String[]{EMAIL_MIMETYPE, PHONE_MIMETYPE, PHOTO_MIMETYPE, STRUCTURED_NAME_MIMETYPE},
//                        DATA_CONTACT_ID_COLUMN + " ASC"
//
//                );
//
//                if(cursor != null){
//                    long total = cursor.getCount();
//                    Log.v(TAG, "Total number of data query rows: " + total);
//                    long taskCount = (total / SUB_TASK_SIZE) + (total % SUB_TASK_SIZE == 0 ? 0 : 1);
//                    Log.v(TAG, "Total number of data query subtasks: " + taskCount + " Task Size: " + SUB_TASK_SIZE);
//
//                    long offset = 0;
//                    List<Future<List<Contact>>> contactFutures = new ArrayList<>();
//                    long start = System.currentTimeMillis();
//                    for(int i = 0; i < taskCount; i++){
//                        Future<List<Contact>> contactFuture = executor.submit(new GetContactsSubTask(context, resources, offset, SUB_TASK_SIZE));
//                        offset += SUB_TASK_SIZE;
//                        contactFutures.add(contactFuture);
//                    }
//
//                    for(Future<List<Contact>> future : contactFutures){
//                        future.get();
//                    }
//
//                    long end = System.currentTimeMillis();
//                    System.out.println("TOTAL TIME: " + (end - start)); //TODO delete this
//
//                    //TODO wait for completion, then parse subtasks. Do them in order
//                }
//            }
//            finally{
//                if(cursor != null){
//                    cursor.close();
//                }
//            }
//
//
//            return null; //TODO return something
//        }
//    }
//
//    private static class GetContactsSubTask implements Callable<List<Contact>>{
//
//        private final Context context;
//        private final ResourceService resourceService;
//        private final long offset;
//        private final long limit;
//
//        public GetContactsSubTask(Context context, ResourceService resourceService, long offset, long limit) {
//            this.context = context;
//            this.resourceService = resourceService;
//            this.offset = offset;
//            this.limit = limit;
//        }
//
//        @Override
//        public List<Contact> call() throws Exception {
//            Cursor cursor = null;
//            try{
//                cursor = context.getContentResolver().query(
//                        DATA_URI.buildUpon().encodedQuery("limit=" + offset + "," + limit).build(),
//                        null,
//                        DATA_MIMETYPE_COLUMN + " in (?, ?, ?, ?)",
//                        new String[]{EMAIL_MIMETYPE, PHONE_MIMETYPE, PHOTO_MIMETYPE, STRUCTURED_NAME_MIMETYPE},
//                        DATA_CONTACT_ID_COLUMN + " " + getSortOrder(CONTACT, context)
//                );
//
//                if(cursor != null){
//                    cursor.moveToFirst();
//                    while(!cursor.isAfterLast()){
//                        String foo = "foo"; //TODO delete this
//
//                        cursor.moveToNext();
//                    }
//                }
//            }
//            finally{
//                if(cursor != null){
//                    cursor.close();
//                }
//            }
//
//
//
//            return null;//TODO return something
//        }
//    }
//
//    private static class GetGroupsTask implements Callable<List<ContactGroup>>{
//
//        private final Context context;
//        private final ResourceService resources;
//        private final AccountService accountService;
//
//        public GetGroupsTask(Context context, ResourceService resources, AccountService accountService){
//            this.context = context;
//            this.resources = resources;
//            this.accountService = accountService;
//        }
//
//        @Override
//        public List<ContactGroup> call() throws Exception {
//            List<ContactGroup> groups = new ArrayList<>();
//            Set<String> accountsToDisplay = getAccountsToDisplay(context, accountService);
//            Cursor cursor = null;
//            try{
//                cursor = context.getContentResolver().query(
//                        GROUP_URI,
//                        new String[]{GROUP_ID, GROUP_TITLE, GROUP_ACCOUNT_NAME},
//                        null, null, GROUP_TITLE + " " + getSortOrder(GROUP, context)
//                );
//
//                if(cursor != null){
//                    Log.v(TAG, "Total number of group query rows: " + cursor.getCount());
//
//                    cursor.moveToFirst();
//                    while(!cursor.isAfterLast()){
//                        String accountName = cursor.getString(cursor.getColumnIndex(GROUP_ACCOUNT_NAME));
//                        if(accountsToDisplay.contains(accountName)){
//                            ContactGroup group = new ContactGroup();
//                            group.setGroupId(cursor.getLong(cursor.getColumnIndex(GROUP_ID)));
//                            group.setGroupName(cursor.getString(cursor.getColumnIndex(GROUP_TITLE)));
//                            group.setAccountName(cursor.getString(cursor.getColumnIndex(GROUP_ACCOUNT_NAME)));
//
//                            groups.add(group);
//                        }
//
//                        cursor.moveToNext();
//                    }
//                }
//            }
//            finally {
//                if(cursor != null){
//                    cursor.close();
//                }
//            }
//
//            return groups;
//        }
//    }
//
//    private static class GetExclusionsTask implements Callable<Set<Long>>{
//
//        private final Context context;
//        private final ResourceService resources;
//        private final AccountService accountService;
//
//        public GetExclusionsTask(Context context, ResourceService resources, AccountService accountService){
//            this.context = context;
//            this.resources = resources;
//            this.accountService = accountService;
//        }
//
//        @Override
//        public Set<Long> call() throws Exception {
//            Set<Long> contactsToExclude = new HashSet<>();
//            Cursor cursor = null;
//            Set<String> accountsToDisplay = PreferenceManager.getDefaultSharedPreferences(context)
//                    .getStringSet(context.getString(R.string.accounts_to_display_prop),
//                            accountService.getAllContactAccountNamesSet());
//
//            try{
//                cursor = context.getContentResolver().query(
//                        RAW_CONTACTS_URI,
//                        new String[]{RAW_CONTACT_ID, RAW_CONTACT_CONTACT_ID, RAW_CONTACT_ACCOUNT_NAME},
//                        null, null, null, null
//                );
//
//                if(cursor != null){
//                    Log.v(TAG, "Total number of exclusion query rows: " + cursor.getCount());
//
//                    cursor.moveToFirst();
//                    while(!cursor.isAfterLast()){
//                        String accountName = cursor.getString(cursor.getColumnIndex(RAW_CONTACT_ACCOUNT_NAME));
//                        if(!accountsToDisplay.contains(accountName)){
//                            contactsToExclude.add(cursor.getLong(cursor.getColumnIndex(RAW_CONTACT_CONTACT_ID)));
//                        }
//
//                        cursor.moveToNext();
//                    }
//                }
//            }
//            finally{
//                if(cursor != null){
//                    cursor.close();
//                }
//            }
//
//            return contactsToExclude;
//        }
//
//    }
}
