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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
import io.craigmiller160.contacts5.util.ContactsThreadFactory;

import static io.craigmiller160.contacts5.util.ContactsConstants.CONTACTS_IN_GROUP_LIST;
import static io.craigmiller160.contacts5.util.ContactsConstants.CONTACTS_LIST;
import static io.craigmiller160.contacts5.util.ContactsConstants.CONTACTS_MODEL;
import static io.craigmiller160.contacts5.util.ContactsConstants.*;

/**
 * Created by craig on 5/29/16.
 */
public class ContactsRetrievalServiceImpl extends AbstractContactsRetrievalService {

    private static final ExecutorService executor = Executors.newFixedThreadPool(4, new ContactsThreadFactory());
    private static final String TAG = "ContactsRetrievService";

    private static final String ALL_CONTACTS_KEY = "AllContacts";
    private static final String FAV_CONTACTS_KEY = "FavContacts";

    private static final int CONTACT = 101;
    private static final int GROUP = 102;

    private static String getSortOrder(int type, Context context){
        String result = "ASC";
        SharedPreferences prefs = context.getSharedPreferences(CONTACTS_PREFS, Context.MODE_PRIVATE);
        switch(type){
            case CONTACT:
                result = prefs.getString(context.getString(R.string.setting_contact_sort_order),
                        context.getResources().getStringArray(R.array.sort_order_values)[0]);
                break;
            case GROUP:
                result = prefs.getString(context.getString(R.string.setting_group_sort_order),
                        context.getResources().getStringArray(R.array.sort_order_values)[0]);
                break;
        }

        return result;
    }

    private static String getGroupSortString(Context context){
        SharedPreferences prefs = context.getSharedPreferences(CONTACTS_PREFS, Context.MODE_PRIVATE);
        String sortBySetting = prefs.getString(context.getString(R.string.setting_group_sort_by),
                context.getResources().getStringArray(R.array.group_sort_by_values)[0]);
        String sortOrderSetting = prefs.getString(context.getString(R.string.setting_group_sort_order),
                context.getResources().getStringArray(R.array.sort_order_values)[0]);

        if(context.getResources().getStringArray(R.array.group_sort_by_values)[1].equals(sortBySetting)){
            return String.format("%1$s %2$s, %3$s %2$s", GROUP_ACCOUNT_NAME, sortOrderSetting, GROUP_TITLE);
        }
        else{
            return String.format("%1$s %2$s, %3$s %2$s", GROUP_TITLE, sortOrderSetting, GROUP_ACCOUNT_NAME);
        }
    }

    private static Set<String> getAccountsToDisplay(Context context, AccountService accountService){
        SharedPreferences prefs = context.getSharedPreferences(CONTACTS_PREFS, Context.MODE_PRIVATE);
        return prefs.getStringSet(context.getString(R.string.setting_accounts_to_display), accountService.getAllContactAccountNamesSet());
    }

    private static boolean useEmptyGroups(Context context){
        return context.getSharedPreferences(CONTACTS_PREFS, Context.MODE_PRIVATE)
                .getBoolean(context.getString(R.string.setting_empty_group), false);
    }

    private static int isPhonesOnly(Context context){
        SharedPreferences prefs = context.getSharedPreferences(CONTACTS_PREFS, Context.MODE_PRIVATE);
        return prefs.getBoolean(context.getString(R.string.setting_phones_only), true) ? 1 : 0;
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

            Log.d(TAG, "Total contacts in group loaded: " + contacts.size());

            ContactsApp.getApp().modelFactory().getModel(CONTACTS_MODEL).setProperty(CONTACTS_IN_GROUP_LIST, contacts);

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

            List<ContactGroup> groups = new ArrayList<>();
            Set<String> accountsToDisplay = getAccountsToDisplay(context, accountService);
            boolean useEmptyGroups = useEmptyGroups(context);
            Cursor cursor = null;
            try{
                cursor = context.getContentResolver().query(
                        GROUP_URI,
                        new String[]{GROUP_ID, GROUP_TITLE, GROUP_ACCOUNT_NAME, GROUP_COUNT, GROUP_COUNT_PHONES},
                        null, null, getGroupSortString(context)
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

                            if(group.getGroupSize() > 0 || useEmptyGroups){
                                groups.add(group);
                            }
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

            Log.d(TAG, "Total groups loaded: " + groups.size());
            ContactsApp.getApp().modelFactory().getModel(CONTACTS_MODEL).setProperty(GROUPS_LIST, groups);

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
            Log.d(TAG, "Starting load all contacts process");

            Future<Set<Long>> getExclusionsFuture = ContactsRetrievalServiceImpl.executor.submit(new GetExclusionsTask(context, accountService));
            Future<Map<String,List<Contact>>> getAllContactsFuture = ContactsRetrievalServiceImpl.executor.submit(new GetAllContactsTask(context));

            try{
                Set<Long> exclusions = getExclusionsFuture.get();
                Map<String,List<Contact>> results = getAllContactsFuture.get();
                List<Contact> allContacts = results.get(ALL_CONTACTS_KEY);
                List<Contact> favContacts = results.get(FAV_CONTACTS_KEY);

                //Remove all contacts from excluded accounts
                Iterator<Contact> contactIterator = allContacts.iterator();
                while(contactIterator.hasNext()){
                    Contact contact = contactIterator.next();
                    if(exclusions.contains(contact.getId())){
                        contactIterator.remove();
                    }
                }

                //Remove all favorites from excluded accounts. This extra iteration shouldn't add much overhead, as favorites lists are generally pretty small
                Iterator<Contact> favIterator = favContacts.iterator();
                while(favIterator.hasNext()){
                    Contact contact = favIterator.next();
                    if(exclusions.contains(contact.getId())){
                        favIterator.remove();
                    }
                }

                Log.d(TAG, "Total contacts loaded: " + allContacts.size());
                Log.d(TAG, "Favorite contacts loaded: " + favContacts.size());

                ContactsApp.getApp().modelFactory().getModel(CONTACTS_MODEL).setProperty(CONTACTS_LIST, allContacts);
                ContactsApp.getApp().modelFactory().getModel(CONTACTS_MODEL).setProperty(FAVORITES_LIST, favContacts);
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

    private static class GetAllContactsTask implements Callable<Map<String,List<Contact>>>{

        private final Context context;

        public GetAllContactsTask(Context context){
            this.context = context;
        }

        @Override
        public Map<String,List<Contact>> call() throws Exception {
            Log.v(TAG, "Starting get all contacts sub-task");
            List<Contact> allContacts = new ArrayList<>();
            List<Contact> favContacts = new ArrayList<>();
            Cursor cursor = null;
            try{
                cursor = context.getContentResolver().query(
                        CONTACTS_URI,
                        new String[]{CONTACT_DISPLAY_NAME, CONTACT_HAS_PHONE, CONTACT_PHOTO_THUMBNAIL_URI, CONTACT_ID, CONTACT_STARRED},
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

                            allContacts.add(contact);

                            int starred = cursor.getInt(cursor.getColumnIndex(CONTACT_STARRED));
                            if(starred == 1){
                                favContacts.add(contact);
                            }
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

            Map<String,List<Contact>> results = new HashMap<>();
            results.put(ALL_CONTACTS_KEY, allContacts);
            results.put(FAV_CONTACTS_KEY, favContacts);

            return results;
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
                    .getStringSet(context.getString(R.string.setting_accounts_to_display),
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
