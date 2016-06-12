package io.craigmiller160.contacts5.service;

import android.net.Uri;
import android.provider.ContactsContract;

/**
 * Created by craig on 5/8/16.
 */
public interface ContactsRetrievalService {

    String GROUP_MEMBERSHIP_MIMETYPE = ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE;
    String EMAIL_MIMETYPE = ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE;
    String PHONE_MIMETYPE = ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE;
    String PHOTO_MIMETYPE = ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE;
    String STRUCTURED_NAME_MIMETYPE = ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE;

    String CONTACTS_ENTITY_CONTENT_DIRECTORY = ContactsContract.Contacts.Entity.CONTENT_DIRECTORY;

    String DATA_ID_COLUMN = ContactsContract.Data._ID;
    String DATA_MIMETYPE_COLUMN = ContactsContract.Data.MIMETYPE;
    String DATA_CONTACT_ID_COLUMN = ContactsContract.Data.CONTACT_ID;
    String DATA_HAS_PHONE_COLUMN = ContactsContract.Data.HAS_PHONE_NUMBER;
    String DATA_DISPLAY_NAME_COLUMN = ContactsContract.Data.DISPLAY_NAME;
    String DATA_GROUP_ID_COLUMN = ContactsContract.Data.DATA1;

    Uri CONTACTS_URI = ContactsContract.Contacts.CONTENT_URI;
    Uri DATA_URI = ContactsContract.Data.CONTENT_URI;
    Uri RAW_CONTACTS_URI = ContactsContract.RawContacts.CONTENT_URI;
    Uri GROUP_URI = ContactsContract.Groups.CONTENT_SUMMARY_URI;

    String RAW_CONTACT_NAME = ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY;
    String RAW_CONTACT_ID = ContactsContract.RawContacts._ID;
    String RAW_CONTACT_CONTACT_ID = ContactsContract.RawContacts.CONTACT_ID;
    String RAW_CONTACT_ACCOUNT_NAME = ContactsContract.RawContacts.ACCOUNT_NAME;

    String GROUP_TITLE = ContactsContract.Groups.TITLE;
    String GROUP_ID = ContactsContract.Groups._ID;
    String GROUP_ACCOUNT_NAME = ContactsContract.Groups.ACCOUNT_NAME;
    String GROUP_COUNT = ContactsContract.Groups.SUMMARY_COUNT;
    String GROUP_COUNT_PHONES = ContactsContract.Groups.SUMMARY_WITH_PHONES;

    String DATA_GROUP_CONTACT_ID = ContactsContract.CommonDataKinds.GroupMembership.CONTACT_ID;
    String DATA_GROUP_GROUP_ID = ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID;
    String DATA_GROUP_CONTACT_NAME = ContactsContract.CommonDataKinds.GroupMembership.DISPLAY_NAME;
    String DATA_GROUP_CONTACT_THUMB_PHOTO_URI = ContactsContract.CommonDataKinds.GroupMembership.PHOTO_THUMBNAIL_URI;

    String CONTACT_DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
    String CONTACT_PHOTO_THUMBNAIL_URI = ContactsContract.Contacts.PHOTO_THUMBNAIL_URI;
    String CONTACT_HAS_PHONE = ContactsContract.Contacts.HAS_PHONE_NUMBER;
    String CONTACT_ID = ContactsContract.Contacts._ID;
    String CONTACT_STARRED = ContactsContract.Contacts.STARRED;
    String CONTACT_ENTITY_CONTENT_DIRECTORY = ContactsContract.Contacts.Entity.CONTENT_DIRECTORY;


    String LIMIT_CLAUSE = "limit";
    String OFFSET_CLAUSE = "offset";

    void loadAllContacts();

    void loadAllGroups();

    void loadAllContactsInGroup(long groupId);

}
