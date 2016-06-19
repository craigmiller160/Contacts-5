package io.craigmiller160.contacts5.service;

import android.net.Uri;
import android.provider.ContactsContract;

/**
 * Created by craig on 5/8/16.
 */
public interface ContactsRetrievalService {

    String MIMETYPE_GROUP_MEMBERSHIP = ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE;
    String MIMETYPE_EMAIL = ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE;
    String MIMETYPE_PHONE = ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE;
    String MIMETYPE_PHOTO = ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE;
    String MIMETYPE_STRUCTURED_NAME = ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE;

    String COL_DATA_ID = ContactsContract.Data._ID;
    String COL_DATA_MIMETYPE = ContactsContract.Data.MIMETYPE;
    String COL_DATA_CONTACT_ID = ContactsContract.Data.CONTACT_ID;
    String COL_DATA_HAS_PHONE = ContactsContract.Data.HAS_PHONE_NUMBER;
    String COL_DATA_DISPLAY_NAME = ContactsContract.Data.DISPLAY_NAME;
    String COL_DATA_GROUP_ID = ContactsContract.Data.DATA1;

    Uri URI_CONTACTS = ContactsContract.Contacts.CONTENT_URI;
    Uri URI_DATA = ContactsContract.Data.CONTENT_URI;
    Uri URI_RAW_CONTACTS = ContactsContract.RawContacts.CONTENT_URI;
    Uri URI_GROUPS = ContactsContract.Groups.CONTENT_SUMMARY_URI;

    String COL_RAW_CONTACT_NAME = ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY;
    String COL_RAW_ID = ContactsContract.RawContacts._ID;
    String COL_RAW_CONTACT_ID = ContactsContract.RawContacts.CONTACT_ID;
    String COL_RAW_ACCOUNT = ContactsContract.RawContacts.ACCOUNT_NAME;

    String COL_GROUP_TITLE = ContactsContract.Groups.TITLE;
    String COL_GROUP_ID = ContactsContract.Groups._ID;
    String COL_GROUP_ACCOUNT = ContactsContract.Groups.ACCOUNT_NAME;
    String COL_GROUP_COUNT = ContactsContract.Groups.SUMMARY_COUNT;
    String COL_GROUP_COUNT_PHONES = ContactsContract.Groups.SUMMARY_WITH_PHONES;

    String COL_DATA_GROUP_CONTACT_ID = ContactsContract.CommonDataKinds.GroupMembership.CONTACT_ID;
    String COL_DATA_GROUP_GROUP_ID = ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID;
    String COL_DATA_GROUP_CONTACT_NAME = ContactsContract.CommonDataKinds.GroupMembership.DISPLAY_NAME;
    String COL_DATA_GROUP_CONTACT_NAME_ALT = ContactsContract.CommonDataKinds.GroupMembership.DISPLAY_NAME_ALTERNATIVE;
    String COL_DATA_GROUP_CONTACT_PHOTO_URI = ContactsContract.CommonDataKinds.GroupMembership.PHOTO_THUMBNAIL_URI;

    String COL_CONTACTS_CONTACT_NAME = ContactsContract.Contacts.DISPLAY_NAME;
    String COL_CONTACTS_CONTACT_NAME_ALT = ContactsContract.Contacts.DISPLAY_NAME_ALTERNATIVE;
    String COL_CONTACTS_CONTACT_PHOTO_URI = ContactsContract.Contacts.PHOTO_THUMBNAIL_URI;
    String COL_CONTACTS_HAS_PHONE = ContactsContract.Contacts.HAS_PHONE_NUMBER;
    String COL_CONTACTS_ID = ContactsContract.Contacts._ID;
    String COL_CONTACTS_STARRED = ContactsContract.Contacts.STARRED;

    void loadAllContacts();

    void loadAllGroups();

    void loadAllContactsInGroup(long groupId);

}
