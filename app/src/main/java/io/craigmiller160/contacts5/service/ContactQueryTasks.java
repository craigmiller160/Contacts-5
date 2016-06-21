package io.craigmiller160.contacts5.service;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
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
import java.util.concurrent.Future;

import io.craigmiller160.contacts5.ContactsApp;
import io.craigmiller160.contacts5.model.AndroidModel;
import io.craigmiller160.contacts5.model.Contact;
import io.craigmiller160.contacts5.model.ContactGroup;
import io.craigmiller160.contacts5.util.AbstractAndroidUtil;
import io.craigmiller160.contacts5.util.PreferenceHelper;

import static io.craigmiller160.contacts5.service.ContactsQueryConstants.COL_CONTACTS_CONTACT_NAME;
import static io.craigmiller160.contacts5.service.ContactsQueryConstants.COL_CONTACTS_CONTACT_NAME_ALT;
import static io.craigmiller160.contacts5.service.ContactsQueryConstants.COL_CONTACTS_HAS_PHONE;
import static io.craigmiller160.contacts5.service.ContactsQueryConstants.COL_CONTACTS_ID;
import static io.craigmiller160.contacts5.service.ContactsQueryConstants.COL_CONTACTS_STARRED;
import static io.craigmiller160.contacts5.service.ContactsQueryConstants.COL_DATA_GROUP_CONTACT_ID;
import static io.craigmiller160.contacts5.service.ContactsQueryConstants.COL_DATA_GROUP_CONTACT_NAME;
import static io.craigmiller160.contacts5.service.ContactsQueryConstants.COL_DATA_GROUP_CONTACT_NAME_ALT;
import static io.craigmiller160.contacts5.service.ContactsQueryConstants.COL_DATA_GROUP_GROUP_ID;
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
import static io.craigmiller160.contacts5.util.ContactsConstants.CONTACTS_IN_GROUP_LIST;
import static io.craigmiller160.contacts5.util.ContactsConstants.CONTACTS_LIST;
import static io.craigmiller160.contacts5.util.ContactsConstants.CONTACTS_MODEL;
import static io.craigmiller160.contacts5.util.ContactsConstants.FAVORITES_LIST;
import static io.craigmiller160.contacts5.util.ContactsConstants.GROUPS_LIST;
import static io.craigmiller160.contacts5.util.ContactsConstants.SELECTED_GROUP_ID;
import static io.craigmiller160.contacts5.util.ContactsConstants.SELECTED_GROUP_NAME;

/**
 * Created by craig on 6/21/16.
 */
public class ContactQueryTasks {

    private static final String ALL_CONTACTS_KEY = "AllContacts";
    private static final String FAV_CONTACTS_KEY = "FavContacts";

    public static final String LOAD_CONTACTS = "LoadContacts";
    public static final String LOAD_GROUPS = "LoadGroups";
    public static final String LOAD_CONTACTS_IN_GROUP = "LoadContactsInGroup";

    public static class ExecuteQueries extends AbstractAndroidUtil implements Runnable{

        private final ContactsService service;
        private final Intent intent;
        private final AndroidModel contactsModel;
        private final int startId;
        private final String tagid;

        public ExecuteQueries(ContactsService service, Intent intent, int startId, String tag){
            super(service);
            this.service = service;
            this.intent = intent;
            this.startId = startId;
            this.tagid = String.format(Locale.getDefault(), "%s-%03d", tag, startId);
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
                    contactsInGroupQuery = service.submit(new ContactsInGroupQuery(getContext(), groupId, tagid));
                }
            }

            Future<Map<String,List<Contact>>> allContactsFuture = null;
            Future<Set<Long>> exclusionsFuture = null;
            if(loadContacts){
                Log.d(tagid, "Running query for all contacts");
                Log.d(tagid, "Running query for favorite contacts");
                allContactsFuture = service.submit(new AllContactsQuery(getContext(), tagid));
                exclusionsFuture = service.submit(new ExclusionsQuery(getContext(), tagid));
            }

            Future<List<ContactGroup>> allGroupsFuture = null;
            if(loadGroups){
                Log.d(tagid, "Running query for all groups");
                allGroupsFuture = service.submit(new AllGroupsQuery(getContext(), tagid));
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

    public static class AllContactsQuery extends AbstractAndroidUtil implements Callable<Map<String,List<Contact>>> {

        private final PreferenceHelper prefHelper;
        private final String tagid;

        public AllContactsQuery(Context context, String tagid){
            super(context);
            this.prefHelper = new PreferenceHelper(context);
            this.tagid = tagid;
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
                        if(Thread.currentThread().isInterrupted()){
                            Log.e(tagid, "All Contacts query was interrupted");
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

            Map<String,List<Contact>> results = new HashMap<>();
            results.put(ALL_CONTACTS_KEY, allContacts);
            results.put(FAV_CONTACTS_KEY, favContacts);

            return results;
        }
    }

    public static class ExclusionsQuery extends AbstractAndroidUtil implements Callable<Set<Long>>{

        private final PreferenceHelper prefHelper;
        private final String tagid;

        public ExclusionsQuery(Context context, String tagid){
            super(context);
            this.prefHelper = new PreferenceHelper(context);
            this.tagid = tagid;
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
                        if(Thread.currentThread().isInterrupted()){
                            Log.e(tagid, "Exclusions query was interrupted");
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

            return contactsToExclude;
        }

    }

    public static class AllGroupsQuery extends AbstractAndroidUtil implements Callable<List<ContactGroup>>{

        private final PreferenceHelper prefHelper;
        private final String tagid;

        public AllGroupsQuery(Context context, String tagid){
            super(context);
            this.prefHelper = new PreferenceHelper(context);
            this.tagid = tagid;
        }

        @Override
        public List<ContactGroup> call() throws Exception{
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
                    while(!cursor.isAfterLast()){
                        if(Thread.currentThread().isInterrupted()){
                            Log.e(tagid, "All Groups query was interrupted");
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

            return groups;
        }
    }

    public static class ContactsInGroupQuery extends AbstractAndroidUtil implements Callable<List<Contact>>{

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
                        if(Thread.currentThread().isInterrupted()){
                            Log.e(tagid, "ContactsInGroup query was interrupted");
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

            return contacts;
        }
    }

}
