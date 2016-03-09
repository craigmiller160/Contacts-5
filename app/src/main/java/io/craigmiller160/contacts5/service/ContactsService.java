package io.craigmiller160.contacts5.service;

import android.content.Context;
import android.net.Uri;

import io.craigmiller160.contacts5.model.Contact;
import io.craigmiller160.contacts5.model.ContactsStorage;

/**
 * Created by Craig on 2/22/2016.
 */
public interface ContactsService {

    ContactsStorage getAllContacts(Context context);

    Contact getContact(Context context, Uri uri);

}
