package io.craigmiller160.contacts5.service;

import android.net.Uri;
import android.provider.ContactsContract;

import io.craigmiller160.contacts5.model.Contact;
import io.craigmiller160.contacts5.model.ContactsStorage;

/**
 * Created by craig on 5/8/16.
 */
public interface ContactsRetrievalService {

    String GROUP_MEMBERSHIP_MIMETYPE = ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE;

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

    void loadAllContacts();

}
