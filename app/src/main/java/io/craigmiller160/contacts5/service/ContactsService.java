package io.craigmiller160.contacts5.service;

import android.app.Service;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import io.craigmiller160.contacts5.ContactsApp;
import io.craigmiller160.contacts5.R;
import io.craigmiller160.contacts5.log.Logger;
import io.craigmiller160.contacts5.model.AndroidModel;
import io.craigmiller160.contacts5.model.Contact;
import io.craigmiller160.contacts5.model.ContactGroup;
import io.craigmiller160.contacts5.util.AbstractAndroidUtil;
import io.craigmiller160.contacts5.util.AndroidSystemUtil;
import io.craigmiller160.contacts5.util.ContactsThreadFactory;
import io.craigmiller160.contacts5.util.PreferenceHelper;

import static io.craigmiller160.contacts5.service.ContactsQueryConstants.*;
import static io.craigmiller160.contacts5.service.ContactsQueryConstants.COL_CONTACTS_CONTACT_NAME_ALT;
import static io.craigmiller160.contacts5.service.ContactsQueryConstants.COL_CONTACTS_HAS_PHONE;
import static io.craigmiller160.contacts5.service.ContactsQueryConstants.COL_CONTACTS_ID;
import static io.craigmiller160.contacts5.service.ContactsQueryConstants.COL_CONTACTS_STARRED;
import static io.craigmiller160.contacts5.service.ContactsQueryConstants.COL_DATA_CONTACT_ID;
import static io.craigmiller160.contacts5.service.ContactsQueryConstants.COL_DATA_DISPLAY_NAME;
import static io.craigmiller160.contacts5.service.ContactsQueryConstants.COL_DATA_GROUP_CONTACT_ID;
import static io.craigmiller160.contacts5.service.ContactsQueryConstants.COL_DATA_GROUP_CONTACT_NAME;
import static io.craigmiller160.contacts5.service.ContactsQueryConstants.COL_DATA_GROUP_CONTACT_NAME_ALT;
import static io.craigmiller160.contacts5.service.ContactsQueryConstants.COL_DATA_GROUP_GROUP_ID;
import static io.craigmiller160.contacts5.service.ContactsQueryConstants.COL_DATA_HAS_PHONE;
import static io.craigmiller160.contacts5.service.ContactsQueryConstants.COL_DATA_MIMETYPE;
import static io.craigmiller160.contacts5.service.ContactsQueryConstants.COL_GROUP_ACCOUNT;
import static io.craigmiller160.contacts5.service.ContactsQueryConstants.COL_GROUP_COUNT;
import static io.craigmiller160.contacts5.service.ContactsQueryConstants.COL_GROUP_COUNT_PHONES;
import static io.craigmiller160.contacts5.service.ContactsQueryConstants.COL_GROUP_ID;
import static io.craigmiller160.contacts5.service.ContactsQueryConstants.COL_GROUP_TITLE;
import static io.craigmiller160.contacts5.service.ContactsQueryConstants.COL_RAW_ACCOUNT;
import static io.craigmiller160.contacts5.service.ContactsQueryConstants.COL_RAW_CONTACT_ID;
import static io.craigmiller160.contacts5.service.ContactsQueryConstants.COL_RAW_ID;
import static io.craigmiller160.contacts5.service.ContactsQueryConstants.MIMETYPE_GROUP_MEMBERSHIP;
import static io.craigmiller160.contacts5.service.ContactsQueryConstants.URI_CONTACTS;
import static io.craigmiller160.contacts5.service.ContactsQueryConstants.URI_DATA;
import static io.craigmiller160.contacts5.service.ContactsQueryConstants.URI_GROUPS;
import static io.craigmiller160.contacts5.service.ContactsQueryConstants.URI_RAW_CONTACTS;

/**
 * Created by craig on 6/19/16.
 */
public class ContactsService extends Service{

    private static final String TAG = "ContactsService";
    private static final Logger logger = Logger.newLogger(TAG);

    public static final String LOAD_CONTACTS = "LoadContacts";
    public static final String LOAD_GROUPS = "LoadGroups";
    public static final String LOAD_CONTACTS_IN_GROUP = "LoadContactsInGroup";

    private AndroidSystemUtil androidSystemUtil;
    private ExecutorService executor;
    private Future<?> queries;

    @Override
    public void onCreate() {
        super.onCreate();
        androidSystemUtil = new AndroidSystemUtil(this);
        executor = Executors.newFixedThreadPool(5, new ContactsThreadFactory());
    }

    public void submit(Runnable task){
        executor.submit(task);
    }

    public <V> Future<V> submit(Callable<V> task){
        return executor.submit(task);
    }

    @Override
    public void onDestroy() {
        executor.shutdownNow();
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(androidSystemUtil.permissions().hasReadContactsPermission()){
            String tagid = String.format(Locale.getDefault(), "%s-%03d", TAG, startId);
            logger.d(tagid, "Start command received. Contacts permissions are available");
            if(queries != null && !queries.isDone()){
                queries.cancel(true);
                logger.e(tagid, "ContactsService already running, terminating before starting new operation");
            }

            ExecuteQueries executeQueries = new ExecuteQueries(this, intent, startId, tagid);
            if(Looper.myLooper() == Looper.getMainLooper()){
                queries = executor.submit(executeQueries);
            }
            else{
                executeQueries.run();
            }
        }
        else{
            logger.e(TAG, "ContactsService start command received. Contacts permissions NOT available");
        }

        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        //No binding being done here
        return null;
    }

    private static class ExecuteQueries extends AbstractAndroidUtil implements Runnable{

        private final ContactsService service;
        private final Intent intent;
        private final AndroidModel contactsModel;
        private final int startId;
        private final String tagid;

        private Future<List<Contact>> contactsInGroupQuery;
        Future<Map<String,List<Contact>>> allContactsQuery;
        Future<Set<Long>> exclusionsQuery;
        Future<List<ContactGroup>> allGroupsQuery;

        public ExecuteQueries(ContactsService service, Intent intent, int startId, String tagid){
            super(service);
            this.service = service;
            this.intent = intent;
            this.startId = startId;
            this.tagid = tagid;
            this.contactsModel = ContactsApp.getApp().modelFactory().getModel(R.string.model_contacts);
        }

        @Override
        public void run() {
            long startTime = System.currentTimeMillis();
            logger.i(tagid, "Service is starting");

            try{
                runService();
                long endTime = System.currentTimeMillis();
                logger.i(tagid, String.format("Service finished. Time: %dms", (endTime - startTime)));
            }
            catch(InterruptedException ex){
                logger.e(tagid, "Service was interrupted during execution", ex);
            }
            catch(ExecutionException ex){
                logger.e(tagid, "Service failed during execution", ex);
            }
            finally{
                service.stopSelf(startId);
            }
        }

        private void interruptTasks(){
            if(contactsInGroupQuery != null){
                contactsInGroupQuery.cancel(true);
                contactsInGroupQuery = null;
            }

            if(allContactsQuery != null){
                allContactsQuery.cancel(true);
                allContactsQuery = null;
            }

            if(exclusionsQuery != null){
                exclusionsQuery.cancel(true);
                exclusionsQuery = null;
            }

            if(allGroupsQuery != null){
                allGroupsQuery.cancel(true);
                allGroupsQuery = null;
            }
        }

        private void runService() throws InterruptedException, ExecutionException{
            boolean loadContacts = intent.getBooleanExtra(LOAD_CONTACTS, false);
            boolean loadGroups = intent.getBooleanExtra(LOAD_GROUPS, false);
            boolean loadContactsInGroup = intent.getBooleanExtra(LOAD_CONTACTS_IN_GROUP, false);
            long groupId = -1;
            String groupName = "";

                /*
                 * Start the requested tasks
                 */

            if(Thread.currentThread().isInterrupted()){
                throw new InterruptedException();
            }

            if(loadContactsInGroup){
                groupId = intent.getLongExtra(getString(R.string.prop_selected_group_id), -1);
                groupName = intent.getStringExtra(getString(R.string.prop_selected_group_name));
                if(groupId >= 0){
                    logger.d(tagid, String.format("Running query for contacts in group '%s'", groupName));
                    contactsInGroupQuery = service.submit(new ContactsInGroupQuery(getContext(), groupId, tagid));
                }
            }

            if(Thread.currentThread().isInterrupted()){
                interruptTasks();
                throw new InterruptedException();
            }

            if(loadContacts){
                logger.d(tagid, "Running query for all contacts");
                logger.d(tagid, "Running query for favorite contacts");
                allContactsQuery = service.submit(new AllContactsQuery(getContext(), tagid));
                exclusionsQuery = service.submit(new ExclusionsQuery(getContext(), tagid));
            }

            if(Thread.currentThread().isInterrupted()){
                interruptTasks();
                throw new InterruptedException();
            }

            if(loadGroups){
                logger.d(tagid, "Running query for all groups");
                allGroupsQuery = service.submit(new AllGroupsQuery(getContext(), tagid));
            }

            if(Thread.currentThread().isInterrupted()){
                interruptTasks();
                throw new InterruptedException();
            }

                /*
                 * Retrieve the results from the tasks
                 */

            if(contactsInGroupQuery != null){
                List<Contact> contactsInGroup = contactsInGroupQuery.get();
                logger.d(tagid, String.format("Loaded contacts for group '%s'. Count: %d", groupName, contactsInGroup.size()));
                contactsModel.setProperty(R.string.prop_contacts_in_group_list, contactsInGroup);
            }

            if(Thread.currentThread().isInterrupted()){
                interruptTasks();
                throw new InterruptedException();
            }

            if(allGroupsQuery != null){
                List<ContactGroup> groups = allGroupsQuery.get();
                logger.d(tagid, String.format("Loaded all groups. Count: %d", groups.size()));
                contactsModel.setProperty(R.string.prop_groups_list, groups);
            }

            if(Thread.currentThread().isInterrupted()){
                interruptTasks();
                throw new InterruptedException();
            }

            if(allContactsQuery != null){
                Map<String,List<Contact>> results = allContactsQuery.get();
                List<Contact> contacts = results.get(getString(R.string.prop_contacts_list));
                List<Contact> favorites = results.get(getString(R.string.prop_favorites_list));
                Set<Long> exclusions = exclusionsQuery.get();
                logger.v(tagid, String.format("Exclusion IDs loaded: " + exclusions.size()));

                Iterator<Contact> contactIterator = contacts.iterator();
                while(contactIterator.hasNext()){
                    if(Thread.currentThread().isInterrupted()){
                        throw new InterruptedException();
                    }
                    Contact contact = contactIterator.next();
                    if(exclusions.contains(contact.getId())){
                        contactIterator.remove();
                    }
                }

                Iterator<Contact> favIterator = favorites.iterator();
                while(favIterator.hasNext()){
                    if(Thread.currentThread().isInterrupted()){
                        throw new InterruptedException();
                    }
                    Contact contact = favIterator.next();
                    if(exclusions.contains(contact.getId())){
                        favIterator.remove();
                    }
                }

                logger.d(tagid, String.format("Loaded all contacts. Count: %d", contacts.size()));
                logger.d(tagid, String.format("Loaded all favorites. Count: %d", favorites.size()));

                contactsModel.setProperty(R.string.prop_contacts_list, contacts);
                contactsModel.setProperty(R.string.prop_favorites_list, favorites);
            }

        }
    }

    private static class AllContactsQuery extends AbstractAndroidUtil implements Callable<Map<String,List<Contact>>> {

        private final PreferenceHelper prefHelper;
        private final String tagid;

        public AllContactsQuery(Context context, String tagid){
            super(context);
            this.prefHelper = new PreferenceHelper(context);
            this.tagid = tagid;
        }

        @Override
        public Map<String,List<Contact>> call() throws Exception {
            logger.v(tagid, "Starting AllContactsQuery");
            List<Contact> allContacts = new ArrayList<>();
            List<Contact> favContacts = new ArrayList<>();

            String retrievalMethod = prefHelper.getRetrievalMethod();

            if(retrievalMethod.equals(getString(R.string.array_new_retrieval_raw))){
                logger.v(tagid, "Using rawMethod to retrieval AllContacts");
//                rawMethod(allContacts, favContacts);
            }
            else if(retrievalMethod.equals(getString(R.string.array_new_retrieval_data_group))){
                logger.v(tagid, "Using dataGroupMethod to retrieval AllContacts");
                dataWithGroupMimeMethod(allContacts, favContacts);
            }
            else if(retrievalMethod.equals(getString(R.string.array_new_retrieval_data))){
                logger.v(tagid, "Using dataMethod to retrieval AllContacts");
                dataMethod(allContacts, favContacts);
            }
            else if(retrievalMethod.equals(getString(R.string.array_new_retrieval_contacts))){
                logger.v(tagid, "Using contactsMethod to retrieval AllContacts");
                contactsMethod(allContacts, favContacts);
            }

            Map<String,List<Contact>> results = new HashMap<>();
            results.put(getString(R.string.prop_contacts_list), allContacts);
            results.put(getString(R.string.prop_favorites_list), favContacts);

            logger.v(tagid, "Finishing AllContactsQuery");

            return results;
        }

        private void rawMethod(List<Contact> allContacts, List<Contact> favContacts) throws InterruptedException{
            Cursor cursor = null;
            try{
                cursor = getContext().getContentResolver().query(
                        URI_RAW_CONTACTS,
                        new String[]{COL_RAW_CONTACT_NAME, COL_CONTACTS_CONTACT_NAME_ALT,
                                COL_CONTACTS_HAS_PHONE, COL_CONTACTS_ID, COL_CONTACTS_STARRED},
                        null, null, prefHelper.getContactSortString(PreferenceHelper.ALL_CONTACTS)
                );

                if(cursor != null){
                    cursor.moveToFirst();
                    logger.v(tagid, "AllContacts cursor contains " + cursor.getCount() + " records");

                    while(!cursor.isAfterLast()){
                        if(Thread.currentThread().isInterrupted()){
                            logger.e(tagid, "All Contacts query was interrupted");
                            throw new InterruptedException();
                        }

                        int hasPhone = cursor.getInt(cursor.getColumnIndex(COL_CONTACTS_HAS_PHONE));
                        if(prefHelper.isPhonesOnly() == 1 && hasPhone != 1){
                            cursor.moveToNext();
                            continue;
                        }

                        long contactId = cursor.getLong(cursor.getColumnIndex(COL_CONTACTS_ID));
                        int displayNameIndex = prefHelper.isFirstNameLastName() ? cursor.getColumnIndex(COL_CONTACTS_CONTACT_NAME) : cursor.getColumnIndex(COL_CONTACTS_CONTACT_NAME_ALT);
                        String displayName = cursor.getString(displayNameIndex);
                        Uri contactUri = ContentUris.withAppendedId(URI_CONTACTS, contactId);

                        Contact contact = new Contact();
                        contact.setDisplayName(displayName);
                        contact.setUri(contactUri);
                        contact.setId(contactId);

                        allContacts.add(contact);

                        int starred = cursor.getInt(cursor.getColumnIndex(COL_CONTACTS_STARRED));
                        if(starred == 1){
                            favContacts.add(contact);
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
        }

        private void dataMethod(List<Contact> allContacts, List<Contact> favContacts) throws InterruptedException{
            Cursor cursor = null;
            try{
                cursor = getContext().getContentResolver().query(
                        URI_DATA,
                        new String[] {COL_DATA_CONTACT_ID, COL_DATA_DISPLAY_NAME, COL_CONTACTS_CONTACT_NAME_ALT,
                                COL_DATA_HAS_PHONE, COL_CONTACTS_STARRED},
                        COL_DATA_MIMETYPE + " = ?",
                        new String[]{MIMETYPE_STRUCTURED_NAME},
                        prefHelper.getContactSortString(PreferenceHelper.ALL_CONTACTS)
                );

                if(cursor != null){
                    cursor.moveToFirst();
                    logger.v(tagid, "AllContacts cursor contains " + cursor.getCount() + " records");

                    while(!cursor.isAfterLast()){
                        if(Thread.currentThread().isInterrupted()){
                            logger.e(tagid, "All Contacts query was interrupted");
                            throw new InterruptedException();
                        }

                        int hasPhone = cursor.getInt(cursor.getColumnIndex(COL_DATA_HAS_PHONE));
                        if(prefHelper.isPhonesOnly() == 1 && hasPhone != 1){
                            cursor.moveToNext();
                            continue;
                        }

                        long contactId = cursor.getLong(cursor.getColumnIndex(COL_DATA_CONTACT_ID));
                        int displayNameIndex = prefHelper.isFirstNameLastName() ? cursor.getColumnIndex(COL_DATA_DISPLAY_NAME) : cursor.getColumnIndex(COL_CONTACTS_CONTACT_NAME_ALT);
                        String displayName = cursor.getString(displayNameIndex);
                        Uri contactUri = ContentUris.withAppendedId(URI_CONTACTS, contactId);

                        Contact contact = new Contact();
                        contact.setDisplayName(displayName);
                        contact.setUri(contactUri);
                        contact.setId(contactId);

                        allContacts.add(contact);

                        int starred = cursor.getInt(cursor.getColumnIndex(COL_CONTACTS_STARRED));
                        if(starred == 1){
                            favContacts.add(contact);
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
        }

        private void dataWithGroupMimeMethod(List<Contact> allContacts, List<Contact> favContacts) throws InterruptedException{
            Cursor cursor = null;
            try{
                cursor = getContext().getContentResolver().query(
                        URI_DATA,
                        new String[] {COL_DATA_CONTACT_ID, COL_DATA_DISPLAY_NAME, COL_CONTACTS_CONTACT_NAME_ALT,
                                COL_DATA_HAS_PHONE, COL_CONTACTS_STARRED, COL_DATA_MIMETYPE},
                        COL_DATA_MIMETYPE + " = ? or " + COL_DATA_MIMETYPE + " = ?",
                        new String[]{MIMETYPE_STRUCTURED_NAME, MIMETYPE_GROUP_MEMBERSHIP},
                        prefHelper.getContactSortString(PreferenceHelper.ALL_CONTACTS)
                );

                if(cursor != null){
                    cursor.moveToFirst();
                    logger.v(tagid, "AllContacts cursor contains " + cursor.getCount() + " records");

                    while(!cursor.isAfterLast()){
                        if(Thread.currentThread().isInterrupted()){
                            logger.e(tagid, "All Contacts query was interrupted");
                            throw new InterruptedException();
                        }

                        int hasPhone = cursor.getInt(cursor.getColumnIndex(COL_DATA_HAS_PHONE));
                        if(prefHelper.isPhonesOnly() == 1 && hasPhone != 1){
                            cursor.moveToNext();
                            continue;
                        }

                        long contactId = cursor.getLong(cursor.getColumnIndex(COL_DATA_CONTACT_ID));
                        int displayNameIndex = prefHelper.isFirstNameLastName() ? cursor.getColumnIndex(COL_DATA_DISPLAY_NAME) : cursor.getColumnIndex(COL_CONTACTS_CONTACT_NAME_ALT);
                        String displayName = cursor.getString(displayNameIndex);
                        Uri contactUri = ContentUris.withAppendedId(URI_CONTACTS, contactId);

                        Contact contact = new Contact();
                        contact.setDisplayName(displayName);
                        contact.setUri(contactUri);
                        contact.setId(contactId);

                        allContacts.add(contact);

                        int starred = cursor.getInt(cursor.getColumnIndex(COL_CONTACTS_STARRED));
                        if(starred == 1){
                            favContacts.add(contact);
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
        }

        private void contactsMethod(List<Contact> allContacts, List<Contact> favContacts) throws InterruptedException{
            Cursor cursor = null;
            try{
                cursor = getContext().getContentResolver().query(
                        URI_CONTACTS,
                        new String[]{COL_CONTACTS_CONTACT_NAME, COL_CONTACTS_CONTACT_NAME_ALT,
                                COL_CONTACTS_HAS_PHONE, COL_CONTACTS_ID, COL_CONTACTS_STARRED},
                        null, null, prefHelper.getContactSortString(PreferenceHelper.ALL_CONTACTS)
                );

                if(cursor != null){
                    cursor.moveToFirst();
                    logger.v(tagid, "AllContacts cursor contains " + cursor.getCount() + " records");

                    while(!cursor.isAfterLast()){
                        if(Thread.currentThread().isInterrupted()){
                            logger.e(tagid, "All Contacts query was interrupted");
                            throw new InterruptedException();
                        }

                        int hasPhone = cursor.getInt(cursor.getColumnIndex(COL_CONTACTS_HAS_PHONE));
                        if(prefHelper.isPhonesOnly() == 1 && hasPhone != 1){
                            cursor.moveToNext();
                            continue;
                        }

                        long contactId = cursor.getLong(cursor.getColumnIndex(COL_CONTACTS_ID));
                        int displayNameIndex = prefHelper.isFirstNameLastName() ? cursor.getColumnIndex(COL_CONTACTS_CONTACT_NAME) : cursor.getColumnIndex(COL_CONTACTS_CONTACT_NAME_ALT);
                        String displayName = cursor.getString(displayNameIndex);
                        Uri contactUri = ContentUris.withAppendedId(URI_CONTACTS, contactId);

                        Contact contact = new Contact();
                        contact.setDisplayName(displayName);
                        contact.setUri(contactUri);
                        contact.setId(contactId);

                        allContacts.add(contact);

                        int starred = cursor.getInt(cursor.getColumnIndex(COL_CONTACTS_STARRED));
                        if(starred == 1){
                            favContacts.add(contact);
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
        }
    }

    private static class ExclusionsQuery extends AbstractAndroidUtil implements Callable<Set<Long>>{

        private final PreferenceHelper prefHelper;
        private final String tagid;

        public ExclusionsQuery(Context context, String tagid){
            super(context);
            this.prefHelper = new PreferenceHelper(context);
            this.tagid = tagid;
        }

        @Override
        public Set<Long> call() throws Exception {
            logger.v(tagid, "Starting ExclusionsQuery");
            Set<Long> contactsToExclude = new HashSet<>();
            Cursor cursor = null;
            Set<String> accountsToDisplay = prefHelper.getAccountsToDisplay();

            try{
                cursor = getContext().getContentResolver().query(
                        URI_RAW_CONTACTS,
                        new String[]{COL_RAW_ID, COL_RAW_CONTACT_ID, COL_RAW_ACCOUNT},
                        null, null, null, null
                );

                if(cursor != null){
                    cursor.moveToFirst();
                    logger.v(tagid, "Exclusions cursor contains " + cursor.getCount() + " records");

                    while(!cursor.isAfterLast()){
                        if(Thread.currentThread().isInterrupted()){
                            logger.e(tagid, "Exclusions query was interrupted");
                            throw new InterruptedException();
                        }

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

            logger.v(tagid, "Finishing ExclusionsQuery");

            return contactsToExclude;
        }

    }

    private static class AllGroupsQuery extends AbstractAndroidUtil implements Callable<List<ContactGroup>>{

        private final PreferenceHelper prefHelper;
        private final String tagid;

        public AllGroupsQuery(Context context, String tagid){
            super(context);
            this.prefHelper = new PreferenceHelper(context);
            this.tagid = tagid;
        }

        @Override
        public List<ContactGroup> call() throws Exception{
            logger.v(tagid, "Starting AllGroupsQuery");
            List<ContactGroup> groups = new ArrayList<>();
            Set<String> accountsToDisplay = prefHelper.getAccountsToDisplay();
            boolean useEmptyGroups = prefHelper.useEmptyGroups();
            Cursor cursor = null;
            try{
                cursor = getContext().getContentResolver().query(
                        URI_GROUPS,
                        new String[]{COL_GROUP_ID, COL_GROUP_TITLE, COL_GROUP_ACCOUNT, COL_GROUP_COUNT, COL_GROUP_COUNT_PHONES},
                        null, null, prefHelper.getGroupSortString()
                );

                if(cursor != null){
                    cursor.moveToFirst();
                    logger.v(tagid, "AllGroups cursor contains " + cursor.getCount() + " records");

                    while(!cursor.isAfterLast()){
                        if(Thread.currentThread().isInterrupted()){
                            logger.e(tagid, "All Groups query was interrupted");
                            throw new InterruptedException();
                        }

                        String accountName = cursor.getString(cursor.getColumnIndex(COL_GROUP_ACCOUNT));
                        if(accountsToDisplay.contains(accountName)){
                            ContactGroup group = new ContactGroup();
                            group.setGroupId(cursor.getLong(cursor.getColumnIndex(COL_GROUP_ID)));
                            group.setGroupName(cursor.getString(cursor.getColumnIndex(COL_GROUP_TITLE)));
                            group.setAccountName(cursor.getString(cursor.getColumnIndex(COL_GROUP_ACCOUNT)));

                            if(prefHelper.isPhonesOnly() == 1){
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

            logger.v(tagid, "Finishing AllGroupsQuery");

            return groups;
        }
    }

    private static class ContactsInGroupQuery extends AbstractAndroidUtil implements Callable<List<Contact>>{

        private final String tagid;
        private final long groupId;
        private final PreferenceHelper prefHelper;

        public ContactsInGroupQuery(Context context, long groupId, String tagid){
            super(context);
            this.groupId = groupId;
            this.prefHelper = new PreferenceHelper(context);
            this.tagid = tagid;
        }

        @Override
        public List<Contact> call() throws Exception{
            logger.v(tagid, "Starting ContactsInGroupQuery");
            List<Contact> contacts = new ArrayList<>();
            Cursor cursor = null;
            try{
                cursor = getContext().getContentResolver().query(
                        URI_DATA,
                        new String[]{
                                COL_DATA_GROUP_GROUP_ID,
                                COL_DATA_GROUP_CONTACT_ID,
                                COL_DATA_GROUP_CONTACT_NAME,
                                COL_DATA_GROUP_CONTACT_NAME_ALT,
                                COL_DATA_MIMETYPE,
                                COL_CONTACTS_HAS_PHONE
                        },
                        COL_DATA_GROUP_GROUP_ID + " = ? and " + COL_DATA_MIMETYPE + " = ?",
                        new String[]{"" + groupId, MIMETYPE_GROUP_MEMBERSHIP},
                        prefHelper.getContactSortString(PreferenceHelper.CONTACTS_IN_GROUP)
                );

                if(cursor != null){
                    cursor.moveToFirst();
                    logger.v(tagid, "ContactsInGroup cursor contains " + cursor.getCount() + " records");

                    while(!cursor.isAfterLast()){
                        if(Thread.currentThread().isInterrupted()){
                            logger.e(tagid, "ContactsInGroup query was interrupted");
                            throw new InterruptedException("ContactsInGroup query was interrupted");
                        }

                        int hasPhone = cursor.getInt(cursor.getColumnIndex(COL_CONTACTS_HAS_PHONE));
                        if(prefHelper.isPhonesOnly() == 1 && hasPhone != 1){
                            cursor.moveToNext();
                            continue;
                        }

                        long contactId = cursor.getLong(cursor.getColumnIndex(COL_DATA_GROUP_CONTACT_ID));
                        int nameColumnIndex = prefHelper.isFirstNameLastName() ? cursor.getColumnIndex(COL_DATA_GROUP_CONTACT_NAME) :
                                cursor.getColumnIndex(COL_DATA_GROUP_CONTACT_NAME_ALT);
                        String displayName = cursor.getString(nameColumnIndex);
                        Uri contactUri = ContentUris.withAppendedId(URI_CONTACTS, contactId);

                        Contact contact = new Contact();
                        contact.setDisplayName(displayName);
                        contact.setUri(contactUri);
                        contact.setId(contactId);

                        contacts.add(contact);

                        cursor.moveToNext();
                    }
                }
            }
            finally{
                if(cursor != null){
                    cursor.close();
                }
            }

            logger.v(tagid, "Finishing ContactsInGroupQuery");

            return contacts;
        }
    }
}
