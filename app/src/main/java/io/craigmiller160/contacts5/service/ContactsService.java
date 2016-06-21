package io.craigmiller160.contacts5.service;

import android.app.Service;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageItemInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;

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
import io.craigmiller160.contacts5.model.AndroidModel;
import io.craigmiller160.contacts5.model.Contact;
import io.craigmiller160.contacts5.model.ContactGroup;
import io.craigmiller160.contacts5.util.AbstractAndroidUtil;
import io.craigmiller160.contacts5.util.AndroidSystemUtil;
import io.craigmiller160.contacts5.util.ContactsThreadFactory;
import io.craigmiller160.contacts5.util.PreferenceHelper;

import static io.craigmiller160.contacts5.service.ContactsQueryConstants.*;
import static io.craigmiller160.contacts5.util.ContactsConstants.*;

/**
 * Created by craig on 6/19/16.
 */
public class ContactsService extends Service{

    //TODO this needs to support its own interruption.
    // TODO It also needs to cancel a running service if a new one is started
    //TODO the service needs to be stopped after each operation is finished

    //TODO reduce numbers of constants used, here and everywhere, and consolidate them all in strings.xml

    private static final String TAG = "ContactsService";
    private static final String ALL_CONTACTS_KEY = "AllContacts";
    private static final String FAV_CONTACTS_KEY = "FavContacts";

    public static final String LOAD_CONTACTS = "LoadContacts";
    public static final String LOAD_GROUPS = "LoadGroups";
    public static final String LOAD_CONTACTS_IN_GROUP = "LoadContactsInGroup";

    private AndroidSystemUtil androidSystemUtil;
    private ExecutorService executor;

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
        //TODO ensure that the service is interrupted
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(androidSystemUtil.permissions().hasReadContactsPermission()){
            Log.d(TAG, "ContactsService start command received. Contacts permissions are available");
            ExecuteQueries executeQueries = new ExecuteQueries(this, intent, startId);
            if(Looper.myLooper() == Looper.getMainLooper()){
                executor.submit(executeQueries);
            }
            else{
                executeQueries.run();
            }
        }
        else{
            Log.e(TAG, "ContactsService start command received. Contacts permissions NOT available");
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

        public ExecuteQueries(ContactsService service, Intent intent, int startId){
            super(service);
            this.service = service;
            this.intent = intent;
            this.startId = startId;
            this.tagid = String.format(Locale.getDefault(), "%s-%03d", TAG, startId);
            this.contactsModel = ContactsApp.getApp().modelFactory().getModel(CONTACTS_MODEL);
        }

        @Override
        public void run() {
            long startTime = System.currentTimeMillis();
            Log.i(tagid, "Service is starting");

            try{
                runService();
            }
            finally{
                service.stopSelf(startId);
            }

            long endTime = System.currentTimeMillis();
            Log.i(tagid, String.format("Service finished. Time: %dms", (endTime - startTime)));
        }

        private void runService(){
            boolean loadContacts = intent.getBooleanExtra(LOAD_CONTACTS, false);
            boolean loadGroups = intent.getBooleanExtra(LOAD_GROUPS, false);
            boolean loadContactsInGroup = intent.getBooleanExtra(LOAD_CONTACTS_IN_GROUP, false);
            long groupId = -1;
            String groupName = "";

            Future<List<Contact>> contactsInGroupQuery = null;
            if(loadContactsInGroup){
                groupId = intent.getLongExtra(SELECTED_GROUP_ID, -1);
                groupName = intent.getStringExtra(SELECTED_GROUP_NAME);
                if(groupId >= 0){
                    Log.d(tagid, String.format("Running query for contacts in group '%s'", groupName));
                    contactsInGroupQuery = service.submit(new ContactsInGroupQuery(getContext(), groupId));
                }
            }

            Future<Map<String,List<Contact>>> allContactsFuture = null;
            Future<Set<Long>> exclusionsFuture = null;
            if(loadContacts){
                Log.d(tagid, "Running query for all contacts");
                Log.d(tagid, "Running query for favorite contacts");
                allContactsFuture = service.submit(new AllContactsQuery(getContext()));
                exclusionsFuture = service.submit(new ExclusionsQuery(getContext()));
            }

            Future<List<ContactGroup>> allGroupsFuture = null;
            if(loadGroups){
                Log.d(tagid, "Running query for all groups");
                allGroupsFuture = service.submit(new AllGroupsQuery(getContext()));
            }

            try{
                if(contactsInGroupQuery != null){
                    List<Contact> contactsInGroup = contactsInGroupQuery.get();
                    Log.d(tagid, String.format("Loaded contacts for group '%s'. Count: %d", groupName, contactsInGroup.size()));
                    contactsModel.setProperty(CONTACTS_IN_GROUP_LIST, contactsInGroup);
                }

                if(allGroupsFuture != null){
                    List<ContactGroup> groups = allGroupsFuture.get();
                    Log.d(tagid, String.format("Loaded all groups. Count: %d", groups.size()));
                    contactsModel.setProperty(GROUPS_LIST, groups);
                }

                if(allContactsFuture != null){
                    Map<String,List<Contact>> results = allContactsFuture.get();
                    List<Contact> contacts = results.get(ALL_CONTACTS_KEY);
                    List<Contact> favorites = results.get(FAV_CONTACTS_KEY);
                    Set<Long> exclusions = exclusionsFuture.get();

                    Iterator<Contact> contactIterator = contacts.iterator();
                    while(contactIterator.hasNext()){
                        Contact contact = contactIterator.next();
                        if(exclusions.contains(contact.getId())){
                            contactIterator.remove();
                        }
                    }

                    Iterator<Contact> favIterator = favorites.iterator();
                    while(favIterator.hasNext()){
                        Contact contact = favIterator.next();
                        if(exclusions.contains(contact.getId())){
                            favIterator.remove();
                        }
                    }

                    Log.d(tagid, String.format("Loaded all contacts. Count: %d", contacts.size()));
                    Log.d(tagid, String.format("Loaded all favorites. Count: %d", favorites.size()));

                    contactsModel.setProperty(CONTACTS_LIST, contacts);
                    contactsModel.setProperty(FAVORITES_LIST, favorites);
                }
            }
            catch(InterruptedException ex){
                Log.e(tagid, "Service was interrupted during execution", ex);
            }
            catch(ExecutionException ex){
                Log.e(tagid, "Service failed during execution", ex);
            }
        }
    }

    private static class AllContactsQuery extends AbstractAndroidUtil implements Callable<Map<String,List<Contact>>> {

        private final PreferenceHelper prefHelper;

        public AllContactsQuery(Context context){
            super(context);
            this.prefHelper = new PreferenceHelper(context);
        }

        @Override
        public Map<String,List<Contact>> call() throws Exception {
            List<Contact> allContacts = new ArrayList<>();
            List<Contact> favContacts = new ArrayList<>();
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

                    while(!cursor.isAfterLast()){
                        int hasPhone = cursor.getInt(cursor.getColumnIndex(COL_CONTACTS_HAS_PHONE));
                        if(prefHelper.isPhonesOnly() == hasPhone){
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

    private static class ExclusionsQuery extends AbstractAndroidUtil implements Callable<Set<Long>>{

        private final PreferenceHelper prefHelper;

        public ExclusionsQuery(Context context){
            super(context);
            this.prefHelper = new PreferenceHelper(context);
        }

        @Override
        public Set<Long> call() throws Exception {
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

    private static class AllGroupsQuery implements Callable<List<ContactGroup>>{

        private final Context context;
        private final PreferenceHelper prefHelper;

        public AllGroupsQuery(Context context){
            this.context = context;
            this.prefHelper = new PreferenceHelper(context);
        }

        @Override
        public List<ContactGroup> call() throws Exception{
            List<ContactGroup> groups = new ArrayList<>();
            Set<String> accountsToDisplay = prefHelper.getAccountsToDisplay();
            boolean useEmptyGroups = prefHelper.useEmptyGroups();
            Cursor cursor = null;
            try{
                cursor = context.getContentResolver().query(
                        URI_GROUPS,
                        new String[]{COL_GROUP_ID, COL_GROUP_TITLE, COL_GROUP_ACCOUNT, COL_GROUP_COUNT, COL_GROUP_COUNT_PHONES},
                        null, null, prefHelper.getGroupSortString()
                );

                if(cursor != null){
                    cursor.moveToFirst();
                    while(!cursor.isAfterLast()){
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

            return groups;
        }
    }

    private static class ContactsInGroupQuery extends AbstractAndroidUtil implements Callable<List<Contact>>{

        private final long groupId;
        private final PreferenceHelper prefHelper;

        public ContactsInGroupQuery(Context context, long groupId){
            super(context);
            this.groupId = groupId;
            this.prefHelper = new PreferenceHelper(context);
        }

        @Override
        public List<Contact> call() throws Exception{
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

                    while(!cursor.isAfterLast()){
                        int hasPhone = cursor.getInt(cursor.getColumnIndex(COL_CONTACTS_HAS_PHONE));
                        if(prefHelper.isPhonesOnly() == hasPhone){
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
}
