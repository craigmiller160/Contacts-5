/*
 * Copyright 2016 ShadowAngler <craigmiller160@gmail.com> - All Rights Reserved
 * Proprietary / Confidential
 * Unauthorized copying, use, or redistribution of this file is prohibited
 */

package io.craigmiller160.contacts5.service;

import android.net.Uri;
import android.provider.ContactsContract;

/**
 * Created by craig on 6/19/16.
 */
public class ContactsQueryConstants {

    public static final String MIMETYPE_GROUP_MEMBERSHIP = ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE;
    public static final String MIMETYPE_EMAIL = ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE;
    public static final String MIMETYPE_PHONE = ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE;
    public static final String MIMETYPE_PHOTO = ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE;
    public static final String MIMETYPE_STRUCTURED_NAME = ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE;

    public static final String COL_DATA_ID = ContactsContract.Data._ID;
    public static final String COL_DATA_MIMETYPE = ContactsContract.Data.MIMETYPE;
    public static final String COL_DATA_CONTACT_ID = ContactsContract.Data.CONTACT_ID;
    public static final String COL_DATA_HAS_PHONE = ContactsContract.Data.HAS_PHONE_NUMBER;
    public static final String COL_DATA_DISPLAY_NAME = ContactsContract.Data.DISPLAY_NAME;
    public static final String COL_DATA_GROUP_ID = ContactsContract.Data.DATA1;

    public static final Uri URI_CONTACTS = ContactsContract.Contacts.CONTENT_URI;
    public static final Uri URI_DATA = ContactsContract.Data.CONTENT_URI;
    public static final Uri URI_RAW_CONTACTS = ContactsContract.RawContacts.CONTENT_URI;
    public static final Uri URI_GROUPS = ContactsContract.Groups.CONTENT_SUMMARY_URI;

    public static final String COL_RAW_CONTACT_NAME = ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY;
    public static final String COL_RAW_ID = ContactsContract.RawContacts._ID;
    public static final String COL_RAW_CONTACT_ID = ContactsContract.RawContacts.CONTACT_ID;
    public static final String COL_RAW_ACCOUNT = ContactsContract.RawContacts.ACCOUNT_NAME;

    public static final String COL_GROUP_TITLE = ContactsContract.Groups.TITLE;
    public static final String COL_GROUP_ID = ContactsContract.Groups._ID;
    public static final String COL_GROUP_ACCOUNT = ContactsContract.Groups.ACCOUNT_NAME;
    public static final String COL_GROUP_COUNT = ContactsContract.Groups.SUMMARY_COUNT;
    public static final String COL_GROUP_COUNT_PHONES = ContactsContract.Groups.SUMMARY_WITH_PHONES;

    public static final String COL_DATA_GROUP_CONTACT_ID = ContactsContract.CommonDataKinds.GroupMembership.CONTACT_ID;
    public static final String COL_DATA_GROUP_GROUP_ID = ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID;
    public static final String COL_DATA_GROUP_CONTACT_NAME = ContactsContract.CommonDataKinds.GroupMembership.DISPLAY_NAME;
    public static final String COL_DATA_GROUP_CONTACT_NAME_ALT = ContactsContract.CommonDataKinds.GroupMembership.DISPLAY_NAME_ALTERNATIVE;
    public static final String COL_DATA_GROUP_CONTACT_PHOTO_URI = ContactsContract.CommonDataKinds.GroupMembership.PHOTO_THUMBNAIL_URI;

    public static final String COL_CONTACTS_CONTACT_NAME = ContactsContract.Contacts.DISPLAY_NAME;
    public static final String COL_CONTACTS_CONTACT_NAME_ALT = ContactsContract.Contacts.DISPLAY_NAME_ALTERNATIVE;
    public static final String COL_CONTACTS_CONTACT_PHOTO_URI = ContactsContract.Contacts.PHOTO_THUMBNAIL_URI;
    public static final String COL_CONTACTS_HAS_PHONE = ContactsContract.Contacts.HAS_PHONE_NUMBER;
    public static final String COL_CONTACTS_ID = ContactsContract.Contacts._ID;
    public static final String COL_CONTACTS_STARRED = ContactsContract.Contacts.STARRED;

}
