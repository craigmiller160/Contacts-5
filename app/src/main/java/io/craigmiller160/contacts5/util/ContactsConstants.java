package io.craigmiller160.contacts5.util;

import io.craigmiller160.contacts5.R;

/**
 * A simple class for global constant values
 *
 * Created by craig on 5/4/16.
 */
public class ContactsConstants {

    //TODO integrate these constant values into the strings.xml, and create a ResourceBundle type class to access them
    //TODO also make all static service classes independent of needing context params passed to them, by initializing them with context params

    /*
     * Result codes
     */
    public static final int SETTINGS_ACTIVITY_REQUEST = 301;
    public static final int SELECT_CONTACT_REQUEST = 302;
    public static final int NEW_CONTACT_REQUEST = 303;
    public static final int SELECT_GROUP_REQUEST = 304;


    public static final String ADAPTER_FRAGMENT_TAG_PREFIX = "android:switcher:";

    /*
     * String generation methods based on constant values
     */

    public static String getFragmentPageTag(int containerId, int tabIndex){
        return ADAPTER_FRAGMENT_TAG_PREFIX + containerId + ":" + tabIndex;
    }

}
