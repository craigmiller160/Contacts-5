package io.craigmiller160.contacts5.util;

/**
 * A simple class for global constant values
 *
 * Created by craig on 5/4/16.
 */
public class ContactsConstants {

    //TODO integrate these constant values into the strings.xml, and create a ResourceBundle type class to access them
    //TODO also make all static service classes independent of needing context params passed to them, by initializing them with context params

    /*
     * Controller names
     */

    public static final String ADD_CONTACT_CONTROLLER = "AddContactController";
    public static final String DISPLAY_SETTINGS_CONTROLLER = "DisplaySettingsController";

    /*
     * Result codes
     */

    public static final int SETTINGS_ACTIVITY_ID = 301;
    public static final int CONTACT_ACTION_VIEW_ID = 302;
    public static final int NEW_CONTACT_VIEW_ID = 303;

}
