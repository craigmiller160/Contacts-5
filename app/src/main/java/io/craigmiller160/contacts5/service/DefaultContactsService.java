package io.craigmiller160.contacts5.service;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.craigmiller160.contacts5.model.Contact;
import io.craigmiller160.contacts5.model.ContactGroup;
import io.craigmiller160.contacts5.model.ContactsStorage;
import io.craigmiller160.contacts5.model.collection.SortedList;

/**
 * Created by Craig on 2/22/2016.
 */
public class DefaultContactsService implements ContactsService {

    private static final String TAG = "DefaultContactsService";

    private static final String CONTACT_NAME_COLUMN = ContactsContract.Contacts.DISPLAY_NAME;
    private static final String CONTACT_ID_COLUMN = ContactsContract.Contacts._ID;
    private static final String CONTACT_HAS_PHONE_COLUMN = ContactsContract.Contacts.HAS_PHONE_NUMBER;
    private static final String CONTACT_DISPLAY_NAME_COLUMN = ContactsContract.Contacts.DISPLAY_NAME;

    private static final String ENTITY_ACCOUNT_NAME_COLUMN = ContactsContract.Contacts.Entity.ACCOUNT_NAME;
    private static final String ENTITY_CONTACT_NAME_COLUMN = ContactsContract.Contacts.Entity.DISPLAY_NAME;
    private static final String ENTITY_CONTACT_ID_COLUMN = ContactsContract.Contacts.Entity.CONTACT_ID;
    private static final String ENTITY_HAS_PHONE_NUMBER_COLUMN = ContactsContract.Contacts.Entity.HAS_PHONE_NUMBER;

    private static final String ENTITY_CONTENT_DIRECTORY = ContactsContract.Contacts.Entity.CONTENT_DIRECTORY;

    private static final String ENTITY_DATA_MIMETYPE = ContactsContract.Contacts.Entity.MIMETYPE;

    private static final Uri CONTACTS_URI = ContactsContract.Contacts.CONTENT_URI;
    private static final Uri DATA_URI = ContactsContract.Data.CONTENT_URI;
    private static final Uri RAW_CONTACTS_URI = ContactsContract.RawContacts.CONTENT_URI;
    private static final Uri GROUP_URI = ContactsContract.Groups.CONTENT_SUMMARY_URI;

    private static final String RAW_CONTACT_NAME = ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY;
    private static final String RAW_CONTACT_ID = ContactsContract.RawContacts._ID;
    private static final String RAW_CONTACT_CONTACT_ID = ContactsContract.RawContacts.CONTACT_ID;
    private static final String RAW_CONTACT_ACCOUNT_NAME = ContactsContract.RawContacts.ACCOUNT_NAME;

    private static final String GROUP_TITLE = ContactsContract.Groups.TITLE;
    private static final String GROUP_ID = ContactsContract.Groups._ID;
    private static final String GROUP_ACCOUNT_NAME = ContactsContract.Groups.ACCOUNT_NAME; //TODO filter by account

    private static final String DATA_ID_COLUMN = ContactsContract.Data._ID;
    private static final String DATA_MIMETYPE_COLUMN = ContactsContract.Data.MIMETYPE;
    private static final String DATA_CONTACT_ID_COLUMN = ContactsContract.Data.CONTACT_ID;
    private static final String DATA_HAS_PHONE_COLUMN = ContactsContract.Data.HAS_PHONE_NUMBER;
    private static final String DATA_DISPLAY_NAME = ContactsContract.Data.DISPLAY_NAME;
    private static final String DATA_GROUP_ID = ContactsContract.Data.DATA1;


    private ContactsPreferences prefs = ContactsPreferences.getInstance();
    private ExecutorService executor = Executors.newFixedThreadPool(2);

    private String getSortOrder(){
        return prefs.getSortOrder();
    }

    //TODO ultimately, this must be multi-threaded

    @Override
    public ContactsStorage getAllContacts(Context context) {
        Log.d(TAG, "Contacts Retrieval Running"); //TODO change this log entry
//        ContactsStorage storage = new ContactsStorage(Comparators.getContactComparator(), Comparators.getGroupComparator());
//        Cursor allContactsCursor = null;
//        try{
//            allContactsCursor = getAllContactsCursor(context);
//            if(allContactsCursor != null){
//                Log.d(TAG, "AllContacts cursor retrieved. Row count: " + allContactsCursor.getCount());
//                Log.d(TAG, "Filtering contacts. Only Phones: " + prefs.getPhonesOnly() + " Accounts: " + prefs.getAccountsToDisplay().toString());
//                allContactsCursor.moveToFirst();
//                while(!allContactsCursor.isAfterLast()){
//                    long contactId = allContactsCursor.getLong(allContactsCursor.getColumnIndex(CONTACT_ID_COLUMN));
//                    Uri contactUri = getContactEntityUri(contactId);
//                    Contact contact = getContactEntity(context, contactUri);
//                    if(contact != null){
//                        storage.addContact(contact);
//                    }
//
//                    if(!allContactsCursor.moveToNext()){
//                        break;
//                    }
//                }
//            }
//        }
//        finally {
//            if (allContactsCursor != null) {
//                allContactsCursor.close();
//            }
//        }
//
//        Log.i(TAG, "Contacts loaded into storage. # of Contacts: " + storage.getContactCount());
//
//        return storage;

        return dataTest(context);
    }

    private Uri getContactEntityUri(long contactId){
        Uri contactUri = ContentUris.withAppendedId(CONTACTS_URI, contactId);
        return Uri.withAppendedPath(contactUri, ENTITY_CONTENT_DIRECTORY);
    }

    private Cursor getContactEntityCursor(Context context, Uri contactEntityUri){

        return context.getContentResolver().query(
                contactEntityUri,
                getProjectionsForEntity(),
                null,
                null,
                null
        );
    }

    private Cursor getAllContactsCursor(Context context){
        return context.getContentResolver().query(
                CONTACTS_URI,
                getProjectionsForAllContacts(),
                null,
                null,
                CONTACT_NAME_COLUMN + " " + getSortOrder()
        );
    }

    private String[] getProjectionsForAllContacts(){
        return new String[]{
                CONTACT_ID_COLUMN,
                CONTACT_NAME_COLUMN
        };
    }

    private String[] getProjectionsForEntity(){
        return new String[]{
                ENTITY_ACCOUNT_NAME_COLUMN,
                ENTITY_CONTACT_NAME_COLUMN,
                ENTITY_CONTACT_ID_COLUMN,
                ENTITY_HAS_PHONE_NUMBER_COLUMN,
                ENTITY_DATA_MIMETYPE
        };
    }

    @Override
    public Contact getContact(Context context, Uri contactUri) {
        Log.d(TAG, "Retrieving contact. Uri: " + contactUri.toString());
        Contact contact = getContactEntity(context, contactUri);
        if(contact != null){
            Log.i(TAG, "Contact retrieved. Name: " + contact.getDisplayName());
        }
        else{
            Log.w(TAG, "Unable to retrieve contact");
        }

        return contact;
    }

    private Contact getContactEntity(Context context, Uri contactUri){
        Contact contact = null;
        Cursor contactEntityCursor = null;
        try{
            contactEntityCursor = getContactEntityCursor(context, contactUri);
            if(contactEntityCursor != null){
                contactEntityCursor.moveToFirst();
                String accountName = contactEntityCursor.getString(contactEntityCursor.getColumnIndex(ENTITY_ACCOUNT_NAME_COLUMN));
                if(!prefs.getAccountsToDisplay().contains(accountName)){
                    return null;
                }

                if(prefs.getPhonesOnly()){
                    int hasPhoneNumber = contactEntityCursor.getInt(contactEntityCursor.getColumnIndex(ENTITY_HAS_PHONE_NUMBER_COLUMN));
                    if(hasPhoneNumber <= 0){
                        return null;
                    }
                }


                contact = new Contact();
                contact.setAccountName(accountName);
                contact.setUri(contactUri);

                long contactId = contactEntityCursor.getLong(contactEntityCursor.getColumnIndex(ENTITY_CONTACT_ID_COLUMN));
                contact.setId(contactId);



                String displayName = contactEntityCursor.getString(contactEntityCursor.getColumnIndex(ENTITY_CONTACT_NAME_COLUMN));
                contact.setDisplayName(displayName);

                //TODO need group info
                //TODO need first/last name
            }
        }
        finally{
            if(contactEntityCursor != null){
                contactEntityCursor.close();
            }
        }

        return contact;
    }

    private ContactsStorage dataTest(Context context){
        ContactsStorage storage = new ContactsStorage();

        Cursor dataCursor = null;
        try{
            dataCursor = getDataCursor(context);
            if(dataCursor != null){
                Log.d(TAG, "AllContacts cursor retrieved. Row count: " + dataCursor.getCount());
                Log.d(TAG, "Filtering contacts. Only Phones: " + prefs.getPhonesOnly() + " Accounts: " + prefs.getAccountsToDisplay().toString());
                dataCursor.moveToFirst();

                int groupCount = 0; //TODO delete this
                long contactId = -1;
                Contact contact = null;
                while(!dataCursor.isAfterLast()){
                    if(prefs.getPhonesOnly()){
                        if(dataCursor.getInt(dataCursor.getColumnIndex(CONTACT_HAS_PHONE_COLUMN)) < 1){
                            if(!dataCursor.moveToNext()){
                                break;
                            }
                            else{
                                continue;
                            }
                        }
                    }

                    long newId = dataCursor.getLong(dataCursor.getColumnIndex(DATA_CONTACT_ID_COLUMN));
                    if(newId != contactId){
                        contactId = newId;
                        String displayName = dataCursor.getString(dataCursor.getColumnIndex(CONTACT_DISPLAY_NAME_COLUMN));
                        Uri contactUri = getContactEntityUri(contactId);

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
                    String mimeType = dataCursor.getString(dataCursor.getColumnIndex(DATA_MIMETYPE_COLUMN));
                    if(mimeType.equals(ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE)){
                        long groupId = dataCursor.getLong(dataCursor.getColumnIndex(DATA_GROUP_ID));
                        contact.addGroupId(groupId);
                    }

                    if(!dataCursor.moveToNext()){
                        break;
                    }

                }

                storage.addContact(contact);
            }
        }
        finally{
            if(dataCursor != null){
                dataCursor.close();
            }
        }

        //TODO multi-thread these other operations

        Set<Long> idsToExclude = getContactIdsToExclude(context);
        SortedList<ContactGroup> groups = getAllGroups(context);
        storage.addAllGroups(groups);

        int i = 0;
        while(i < storage.getContactCount()){
            if(idsToExclude.contains(storage.getContact(i).getId())){
                storage.removeContact(i);
            }
            else{
                i++;
            }
        }

        Log.d(TAG, "Retrieval done. Size: " + storage.getContactCount()); //TODO change this log entry

        return storage;
    }

    private SortedList<ContactGroup> getAllGroups(Context context){

        SortedList<ContactGroup> groupList = null;

        Cursor groupCursor = null;
        try{
            groupCursor = getAllGroupsCursor(context);
            if(groupCursor != null){
                Log.d(TAG, "Getting a list of all groups for selected accounts. Size: " + groupCursor.getCount());
                groupList = new SortedList<>();

                groupCursor.moveToFirst();
                while(!groupCursor.isAfterLast()){
                    String accountName = groupCursor.getString(groupCursor.getColumnIndex(GROUP_ACCOUNT_NAME));
                    if(prefs.getAccountsToDisplay().contains(accountName)){
                        ContactGroup group = new ContactGroup();
                        group.setGroupId(groupCursor.getLong(groupCursor.getColumnIndex(GROUP_ID)));
                        group.setGroupName(groupCursor.getString(groupCursor.getColumnIndex(GROUP_TITLE)));
                        group.setAccountName(groupCursor.getString(groupCursor.getColumnIndex(GROUP_ACCOUNT_NAME)));

                        groupList.add(group);
                    }

                    if(!groupCursor.moveToNext()){
                        break;
                    }
                }
            }
        }
        finally{
            if(groupCursor != null){
                groupCursor.close();
            }
        }

        return groupList;
    }

    private Set<Long> getContactIdsToExclude(Context context){
        Set<Long> contactsToExclude = new HashSet<>();
        Cursor rawContactCursor = null;
        try{
            rawContactCursor = getRawContactCursor(context);
            if(rawContactCursor != null){
                rawContactCursor.moveToFirst();
                while(!rawContactCursor.isAfterLast()){
                    String accountName = rawContactCursor.getString(rawContactCursor.getColumnIndex(RAW_CONTACT_ACCOUNT_NAME));
                    if(!prefs.getAccountsToDisplay().contains(accountName)){
                        contactsToExclude.add(rawContactCursor.getLong(rawContactCursor.getColumnIndex(RAW_CONTACT_CONTACT_ID)));
                    }

                    if(!rawContactCursor.moveToNext()){
                        break;
                    }
                }
                Log.d(TAG, "Retrieved raw contact IDs from accounts to exclude from final list. Size: " + contactsToExclude.size());
            }
        }
        finally{
            if(rawContactCursor != null){
                rawContactCursor.close();
            }
        }

        return contactsToExclude;
    }

    private String[] getProjectionsForAllGroups(){
        return new String[]{
                GROUP_ID,
                GROUP_TITLE,
                GROUP_ACCOUNT_NAME
        };
    }

    private Cursor getAllGroupsCursor(Context context){
        return context.getContentResolver().query(
                GROUP_URI,
                getProjectionsForAllGroups(),
                null,
                null,
                GROUP_TITLE + " " + "ASC"
        );
    }

    private String[] getProjectionsForRawContacts(){
        return new String[]{
                RAW_CONTACT_ID,
                RAW_CONTACT_CONTACT_ID,
                RAW_CONTACT_ACCOUNT_NAME,
        };
    }

    private Cursor getRawContactCursor(Context context){
        return context.getContentResolver().query(
                RAW_CONTACTS_URI,
                getProjectionsForRawContacts(),
                null,
                null,
                null,
                null
        );
    }

    private Cursor getDataCursor(Context context){
        return context.getContentResolver().query(
                DATA_URI,
                null,
                null,
                null,
                DATA_CONTACT_ID_COLUMN + " ASC"
        );
    }

    private String[] getProjectionsForData(){
        return new String[]{
                DATA_ID_COLUMN,
                DATA_MIMETYPE_COLUMN,
                DATA_CONTACT_ID_COLUMN,
                CONTACT_HAS_PHONE_COLUMN,
                CONTACT_DISPLAY_NAME_COLUMN,
        };
    }
}