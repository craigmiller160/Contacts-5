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

    private static final int ALL_CONTACTS = 101;
    private static final int CONTACTS_IN_GROUP = 102;

    private static String getContactSortString(Context context, int type){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String sortBy = prefs.getString(context.getString(R.string.setting_contact_sort_by_key),
                context.getString(R.string.array_contact_sort_by_first));
        String sortOrder = prefs.getString(context.getString(R.string.setting_contact_sort_order_key),
                context.getString(R.string.array_sort_order_asc));

        String displayNameColumn = type == ALL_CONTACTS ? COL_CONTACTS_CONTACT_NAME : COL_DATA_GROUP_CONTACT_NAME;
        String displayNameAltColumn = type == ALL_CONTACTS ? COL_CONTACTS_CONTACT_NAME_ALT : COL_DATA_GROUP_CONTACT_NAME_ALT;

        String sort = context.getString(R.string.array_sort_order_asc).equals(sortOrder) ? "ASC" : "DESC";

        if(context.getString(R.string.array_contact_sort_by_first).equals(sortBy)){
            return String.format("%1$s %2$s", displayNameColumn, sort);
        }
        else{
            return String.format("%1$s %2$s", displayNameAltColumn, sort);
        }
    }

    private static String getGroupSortString(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String sortBy = prefs.getString(context.getString(R.string.setting_group_sort_by_key),
                context.getString(R.string.array_group_sort_by_group));
        String sortOrder = prefs.getString(context.getString(R.string.setting_group_sort_order_key),
                context.getString(R.string.array_sort_order_asc));

        String sort = context.getString(R.string.array_sort_order_asc).equals(sortOrder) ? "ASC" : "DESC";

        if(context.getString(R.string.array_group_sort_by_group).equals(sortBy)){
            return String.format("%1$s %2$s, %3$s %2$s", COL_GROUP_TITLE, sort, COL_GROUP_ACCOUNT);
        }
        else{
            return String.format("%1$s %2$s, %3$s %2$s", COL_GROUP_ACCOUNT, sort, COL_GROUP_TITLE);
        }
    }

    private static Set<String> getAccountsToDisplay(Context context, AccountService accountService){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getStringSet(context.getString(R.string.setting_accounts_to_display_key), accountService.getAllContactAccountNamesSet());
    }

    private static boolean useEmptyGroups(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(context.getString(R.string.setting_group_empty_key), false);
    }

    private static int isPhonesOnly(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(context.getString(R.string.setting_phones_only_key), true) ? 1 : 0;
    }

    private static boolean isFirstNameLastName(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String nameFormat = prefs.getString(context.getString(R.string.setting_contact_name_format_key),
                context.getString(R.string.array_name_format_first_last));
        return context.getString(R.string.array_name_format_first_last).equals(nameFormat);
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
                        URI_DATA,
                        new String[]{
                                COL_DATA_GROUP_GROUP_ID,
                                COL_DATA_GROUP_CONTACT_ID,
                                COL_DATA_GROUP_CONTACT_NAME,
                                COL_DATA_GROUP_CONTACT_NAME_ALT,
                                COL_DATA_MIMETYPE,
                                COL_CONTACTS_HAS_PHONE,
                                COL_DATA_GROUP_CONTACT_PHOTO_URI
                        },
                        COL_DATA_GROUP_GROUP_ID + " = ? and " + COL_DATA_MIMETYPE + " = ?",
                        new String[]{"" + groupId, MIMETYPE_GROUP_MEMBERSHIP},
                        getContactSortString(context, CONTACTS_IN_GROUP)
                );

                if(cursor != null){
                    cursor.moveToFirst();

                    while(!cursor.isAfterLast()){
                        int hasPhone = cursor.getInt(cursor.getColumnIndex(COL_CONTACTS_HAS_PHONE));
                        if(isPhonesOnly(context) == hasPhone){
                            long contactId = cursor.getLong(cursor.getColumnIndex(COL_DATA_GROUP_CONTACT_ID));
                            int nameColumnIndex = isFirstNameLastName(context) ? cursor.getColumnIndex(COL_DATA_GROUP_CONTACT_NAME) :
                                    cursor.getColumnIndex(COL_DATA_GROUP_CONTACT_NAME_ALT);
                            String displayName = cursor.getString(nameColumnIndex);
                            Uri contactUri = ContentUris.withAppendedId(URI_CONTACTS, contactId);
                            String photoUriString = cursor.getString(cursor.getColumnIndex(COL_DATA_GROUP_CONTACT_PHOTO_URI));
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
                        URI_GROUPS,
                        new String[]{COL_GROUP_ID, COL_GROUP_TITLE, COL_GROUP_ACCOUNT, COL_GROUP_COUNT, COL_GROUP_COUNT_PHONES},
                        null, null, getGroupSortString(context)
                );

                if(cursor != null){
                    Log.v(TAG, "Total number of group query rows: " + cursor.getCount());
                    cursor.moveToFirst();
                    while(!cursor.isAfterLast()){
                        String accountName = cursor.getString(cursor.getColumnIndex(COL_GROUP_ACCOUNT));
                        if(accountsToDisplay.contains(accountName)){
                            ContactGroup group = new ContactGroup();
                            group.setGroupId(cursor.getLong(cursor.getColumnIndex(COL_GROUP_ID)));
                            group.setGroupName(cursor.getString(cursor.getColumnIndex(COL_GROUP_TITLE)));
                            group.setAccountName(cursor.getString(cursor.getColumnIndex(COL_GROUP_ACCOUNT)));

                            if(isPhonesOnly(context) == 1){
                                group.setGroupSize(cursor.getInt(cursor.getColumnIndex(COL_GROUP_COUNT_PHONES)));
                            }
                            else{
                                group.setGroupSize(cursor.getInt(cursor.getColumnIndex(COL_GROUP_COUNT)));
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
                        URI_CONTACTS,
                        new String[]{COL_CONTACTS_CONTACT_NAME, COL_CONTACTS_CONTACT_NAME_ALT, COL_CONTACTS_HAS_PHONE, COL_CONTACTS_CONTACT_PHOTO_URI, COL_CONTACTS_ID, COL_CONTACTS_STARRED},
                        null, null, getContactSortString(context, ALL_CONTACTS)
                );

                if(cursor != null){
                    cursor.moveToFirst();

                    while(!cursor.isAfterLast()){
                        int hasPhone = cursor.getInt(cursor.getColumnIndex(COL_CONTACTS_HAS_PHONE));
                        if(isPhonesOnly(context) == hasPhone){
                            long contactId = cursor.getLong(cursor.getColumnIndex(COL_CONTACTS_ID));
                            int displayNameIndex = isFirstNameLastName(context) ? cursor.getColumnIndex(COL_CONTACTS_CONTACT_NAME) : cursor.getColumnIndex(COL_CONTACTS_CONTACT_NAME_ALT);
                            String displayName = cursor.getString(displayNameIndex);
                            Uri contactUri = ContentUris.withAppendedId(URI_CONTACTS, contactId);
                            String photoUriString = cursor.getString(cursor.getColumnIndex(COL_CONTACTS_CONTACT_PHOTO_URI));
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

                            int starred = cursor.getInt(cursor.getColumnIndex(COL_CONTACTS_STARRED));
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
                    .getStringSet(context.getString(R.string.setting_accounts_to_display_key),
                            accountService.getAllContactAccountNamesSet());

            try{
                cursor = context.getContentResolver().query(
                        URI_RAW_CONTACTS,
                        new String[]{COL_RAW_ID, COL_RAW_CONTACT_ID, COL_RAW_ACCOUNT},
                        null, null, null, null
                );

                if(cursor != null){
                    cursor.moveToFirst();
                    while(!cursor.isAfterLast()){
                        String accountName = cursor.getString(cursor.getColumnIndex(COL_RAW_ACCOUNT));
                        if(!accountsToDisplay.contains(accountName)){
                            contactsToExclude.add(cursor.getLong(cursor.getColumnIndex(COL_RAW_CONTACT_ID)));
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
