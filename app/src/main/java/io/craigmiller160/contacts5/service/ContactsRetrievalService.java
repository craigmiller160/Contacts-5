package io.craigmiller160.contacts5.service;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.craigmiller160.contacts5.R;
import io.craigmiller160.contacts5.model.Contact;
import io.craigmiller160.contacts5.model.ContactGroup;

/**
 * Created by Craig on 1/20/2016.
 */
public class ContactsRetrievalService {

    private ContactsPreferences displaySettings = ContactsPreferences.getInstance();

    private static final String TAG = "ContactsRetrieveService";

    private static final String CONTACT_NAME = ContactsContract.Contacts.DISPLAY_NAME;
    private static final String CONTACT_ID = ContactsContract.Contacts._ID;
    private static final String PHOTO_ID = ContactsContract.Contacts.PHOTO_ID;
    private static final String PHOTO_THUMBNAIL = ContactsContract.Contacts.PHOTO_THUMBNAIL_URI;
    private static final String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;
    private static final String CONTACT_LOOKUP_KEY = ContactsContract.Contacts.LOOKUP_KEY;

    private static final String RAW_CONTACT_NAME = ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY;
    private static final String RAW_CONTACT_ID = ContactsContract.RawContacts._ID;
    private static final String RAW_CONTACT_CONTACT_ID = ContactsContract.RawContacts.CONTACT_ID;
    private static final String RAW_CONTACT_ACCOUNT_NAME = ContactsContract.RawContacts.ACCOUNT_NAME;

    private static final String ENTITY_ACCOUNT_NAME = ContactsContract.Contacts.Entity.ACCOUNT_NAME;
    private static final String ENTITY_CONTACT_NAME = ContactsContract.Contacts.Entity.DISPLAY_NAME;
    private static final String ENTITY_CONTACT_ID = ContactsContract.Contacts.Entity.CONTACT_ID;
    private static final String ENTITY_LOOKUP_KEY = ContactsContract.Contacts.Entity.LOOKUP_KEY;
    private static final String ENTITY_HAS_PHONE_NUMBER = ContactsContract.Contacts.Entity.HAS_PHONE_NUMBER;
    private static final String ENTITY_CONTENT_DIRECTORY = ContactsContract.Contacts.Entity.CONTENT_DIRECTORY;

    private static final String GROUP_TITLE = ContactsContract.Groups.TITLE;
    private static final String GROUP_COUNT_ALL = ContactsContract.Groups.SUMMARY_COUNT;
    private static final String GROUP_COUNT_WITH_PHONES = ContactsContract.Groups.SUMMARY_WITH_PHONES;
    private static final String GROUP_ID = ContactsContract.Groups._ID;
    private static final String GROUP_ACCOUNT_NAME = ContactsContract.Groups.ACCOUNT_NAME; //TODO filter by account

    private static final String GROUP_MEMBER_GROUP_ID = ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID;
    private static final String GROUP_MEMBER_CONTACT_ID = ContactsContract.CommonDataKinds.GroupMembership.CONTACT_ID;
    private static final String GROUP_MEMBER_CONTACT_NAME = ContactsContract.CommonDataKinds.GroupMembership.DISPLAY_NAME;

    private static final Uri RAW_CONTACTS_URI = ContactsContract.RawContacts.CONTENT_URI;
    private static final Uri CONTACTS_URI = ContactsContract.Contacts.CONTENT_URI;
    private static final Uri CONTACTS_PROFILE_URI = ContactsContract.Profile.CONTENT_URI;
    private static final Uri GROUP_CONTENT_URI = ContactsContract.Groups.CONTENT_SUMMARY_URI;
    private static final Uri DATA_CONTENT_URI = ContactsContract.Data.CONTENT_URI;

    private String getSortOrder(){
        return displaySettings.getSortOrder();
    }

    public Contact getContact(Context context, long contactId){
        Contact contact = null;

        Cursor contactCursor = null;
        try{
            contactCursor = getSingleContactCursor(context, contactId);
            if(contactCursor != null && contactCursor.getCount() > 0){
                contactCursor.moveToFirst();

                String accountName = contactCursor.getString(contactCursor.getColumnIndex(ENTITY_ACCOUNT_NAME));
                if(ContactsPreferences.getInstance().getAccountsToDisplay().contains(accountName)){
                    contact = new Contact();
                    contact.setId(contactId);
                    contact.setDisplayName(contactCursor.getString(contactCursor.getColumnIndex(ENTITY_CONTACT_NAME)));
                }
            }
        }
        finally{
            if(contactCursor != null){
                contactCursor.close();
            }
        }

        if(contact != null){
            Log.i(TAG, "Retrieved info for updated contact");
        }
        else{
            Log.i(TAG, "Retrieved that contact was deleted");
        }

        return contact;
    }

    private Cursor getSingleContactCursor(Context context, long contactId){
        Uri contactUri = ContentUris.withAppendedId(CONTACTS_URI, contactId);
        Uri contactEntityUri = Uri.withAppendedPath(contactUri, ENTITY_CONTENT_DIRECTORY);
        return context.getContentResolver().query(
                contactEntityUri,
                getProjectionsForSingleContact(),
                null,
                null,
                null
        );
    }

    private String[] getProjectionsForSingleContact(){
        return new String[] {
                ENTITY_CONTACT_ID,
                ENTITY_CONTACT_NAME,
                ENTITY_ACCOUNT_NAME,
                ENTITY_HAS_PHONE_NUMBER
        };
    }

    private Cursor getAllContactsCursor(Context context){
        return context.getContentResolver().query(
                CONTACTS_URI,
                getProjectionsForAllContacts(),
                null,
                null,
                CONTACT_NAME + " " + getSortOrder()
        );
    }

    private String[] getProjectionsForAllContacts(){
        String[] projections = new String[3];

        projections[0] = CONTACT_ID;
        projections[1] = CONTACT_NAME;
        projections[2] = HAS_PHONE_NUMBER;

        return projections;
    }

    private Map<Long,Contact> getContactsFromContactsTable(Context context){
        Map<Long,Contact> contactsMap = new HashMap<>();
        Cursor contactCursor = null;
        try{
            contactCursor = getAllContactsCursor(context);

            if(contactCursor != null){

                contactCursor.moveToFirst();
                while(!contactCursor.isAfterLast()){

                    Contact contact = loadContactFromCursor(contactCursor, context);
                    if(contact != null){
                        contactsMap.put(contact.getId(), contact);
                    }

                    if(!contactCursor.moveToNext()){
                        break;
                    }
                }
                Log.d(TAG, "Retrieved list of contacts from all accounts. Size: " + contactsMap.size());
            }
        }
        finally{
            if(contactCursor != null){
                contactCursor.close();
            }
        }

        return contactsMap;
    }

    private Set<Long> getContactIdsToExclude(Context context){
        Set<Long> contactsToExclude = new HashSet<>();
        Cursor rawContactCursor = null;
        try{
            rawContactCursor = getRawContactCursor3(context);
            if(rawContactCursor != null){
                rawContactCursor.moveToFirst();
                while(!rawContactCursor.isAfterLast()){
                    String accountName = rawContactCursor.getString(rawContactCursor.getColumnIndex(RAW_CONTACT_ACCOUNT_NAME));
                    if(!displaySettings.getAccountsToDisplay().contains(accountName)){
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

    public List<Contact> getAllContacts(Context context){
        //TODO if this works, do some serious multi-threading so that both cursors can run simultaneously
        List<Contact> contactsList = null;

        Map<Long,Contact> contactsMap = getContactsFromContactsTable(context);
        Set<Long> contactsToExclude = getContactIdsToExclude(context);

        if(contactsMap.size() > 0){
            for(Long id : contactsToExclude){
                contactsMap.remove(id);
            }

            contactsList = new ArrayList<>(contactsMap.values());
            Log.i(TAG, "Retrieved final list of all contacts, filtered by excluded IDs. Size: " + contactsList.size());

            if(getSortOrder().equals(context.getResources().getStringArray(R.array.sort_order_values)[0])){
                Collections.sort(contactsList, new ContactDisplayNameAscComparator());
            }
            else{
                Collections.sort(contactsList, new ContactDisplayNameDescComparator());
            }
        }

        return contactsList;
    }

    private String[] getProjectionsForRawContacts(){
        String[] projections = new String[3];

        projections[0] = RAW_CONTACT_ID;
        projections[1] = RAW_CONTACT_CONTACT_ID;
        projections[2] = RAW_CONTACT_ACCOUNT_NAME;

        return projections;
    }

    private Cursor getRawContactCursor3(Context context){
        return context.getContentResolver().query(
                RAW_CONTACTS_URI,
                getProjectionsForRawContacts(),
                null,
                null,
                null,
                null
        );
    }

    private Contact loadContactFromCursor(Cursor contactCursor, Context context){
        if(displaySettings.getPhonesOnly()){
            int hasPhone = contactCursor.getInt(contactCursor.getColumnIndex(HAS_PHONE_NUMBER));
            if(hasPhone != 1){
                return null;
            }
        }

        Contact contact = new Contact();
        contact.setId(contactCursor.getLong(contactCursor.getColumnIndex(CONTACT_ID)));
        contact.setDisplayName(contactCursor.getString(contactCursor.getColumnIndex(CONTACT_NAME)));

        return contact;
    }

    public List<Contact> getAllContactsInGroup(Context context, long groupId){

        List<Contact> contactsList = null;
        Cursor cursor = null;
        try{
            cursor = getCursorForAllContactsInGroup(context, groupId);

            if(cursor != null){
                contactsList = new ArrayList<>();

                cursor.moveToFirst();
                while(!cursor.isAfterLast()){
                    Contact contact = new Contact();
                    contact.setId(cursor.getLong(cursor.getColumnIndex(GROUP_MEMBER_CONTACT_ID)));
                    contact.setDisplayName(cursor.getString(cursor.getColumnIndex(GROUP_MEMBER_CONTACT_NAME)));

                    contactsList.add(contact);

                    if(!cursor.moveToNext()){
                        break;
                    }
                }
                Log.i(TAG, "Retrieved a list of all contacts in group. Group: " + groupId + " Size: " + contactsList.size());
            }
        }
        finally{
            if(cursor != null){
                cursor.close();
            }
        }

        return contactsList;
    }

    private Cursor getCursorForAllContactsInGroup(Context context, long groupId){
        return context.getContentResolver().query(
                DATA_CONTENT_URI,
                getProjectionsForAllContactsInGroup(),
                GROUP_MEMBER_GROUP_ID + "=" + groupId,
                null,
                GROUP_MEMBER_CONTACT_NAME + " " + getSortOrder()
        );
    }

    private String[] getProjectionsForAllContactsInGroup(){
        String[] projections = new String[3];

        projections[0] = GROUP_MEMBER_GROUP_ID;
        projections[1] = GROUP_MEMBER_CONTACT_ID;
        projections[2] = GROUP_MEMBER_CONTACT_NAME;

        return projections;
    }

    public List<ContactGroup> getAllGroups(Context context){
        Log.d(TAG, "Getting a list of all groups for selected accounts");
        List<ContactGroup> groupList = null;

        Cursor groupCursor = null;
        try{
            groupCursor = getAllGroupsCursor(context);
            if(groupCursor != null){
                groupList = new ArrayList<>();

                groupCursor.moveToFirst();
                while(!groupCursor.isAfterLast()){
                    String accountName = groupCursor.getString(groupCursor.getColumnIndex(GROUP_ACCOUNT_NAME));
                    if(displaySettings.getAccountsToDisplay().contains(accountName)){
                        ContactGroup group = new ContactGroup();
                        group.setGroupId(groupCursor.getLong(groupCursor.getColumnIndex(GROUP_ID)));
                        group.setGroupName(groupCursor.getString(groupCursor.getColumnIndex(GROUP_TITLE)));
                        group.setAccountName(groupCursor.getString(groupCursor.getColumnIndex(GROUP_ACCOUNT_NAME)));

                        if(displaySettings.getPhonesOnly()){
                            group.setGroupSize(groupCursor.getInt(groupCursor.getColumnIndex(GROUP_COUNT_WITH_PHONES)));
                        }
                        else{
                            group.setGroupSize(groupCursor.getInt(groupCursor.getColumnIndex(GROUP_COUNT_ALL)));
                        }

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

    private String[] getProjectionsForAllGroups(){
        String[] projections = new String[4];

        projections[0] = GROUP_ID;
        projections[1] = GROUP_TITLE;
        if(displaySettings.getPhonesOnly()){
            projections[2] = GROUP_COUNT_WITH_PHONES;
        }
        else{
            projections[2] = GROUP_COUNT_ALL;
        }
        projections[3] = GROUP_ACCOUNT_NAME;

        return projections;
    }

    private Cursor getAllGroupsCursor(Context context){
        return context.getContentResolver().query(
                GROUP_CONTENT_URI,
                getProjectionsForAllGroups(),
                null,
                null,
                GROUP_TITLE + " " + "ASC"
        );
    }

    private class ContactDisplayNameAscComparator implements Comparator<Contact> {

        @Override
        public int compare(Contact c1, Contact c2) {
            return c1.getDisplayName().compareToIgnoreCase(c2.getDisplayName());
        }
    }

    private class ContactDisplayNameDescComparator implements Comparator<Contact> {

        @Override
        public int compare(Contact c1, Contact c2) {
            return c1.getDisplayName().compareToIgnoreCase(c2.getDisplayName()) * -1;
        }
    }

}
