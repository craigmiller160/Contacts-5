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
     * Controller names
     */

    public static final String ADD_CONTACT_CONTROLLER = "AddContactController";
    public static final String DISPLAY_SETTINGS_CONTROLLER = "DisplaySettingsController";
    public static final String SELECT_CONTACT_CONTROLLER = "SelectContactController";
    public static final String SELECT_GROUP_CONTROLLER = "SelectGroupController";

    /*
     * Orientation changes
     */
    public static final String STATE_CHANGE = "StateChange";
    public static final int ORIENTATION_CHANGE = 301;
    public static final int RECREATE_CHANGE = 302;

    /*
     * Property names
     */
    public static final String CONTACTS_LIST = "ContactsList";
    public static final String GROUPS_LIST = "GroupsList";
    public static final String FAVORITES_LIST = "FavoritesList";
    public static final String CONTACTS_IN_GROUP_LIST = "ContactsInGroupList";
    public static final String DISPLAYED_FRAGMENT = "DisplayedFragment";
    public static final String SELECTED_GROUP_ID = "SelectedGroupId";
    public static final String SELECTED_GROUP_NAME = "SelectedGroupName";

    /*
     * Model names
     */
    public static final String CONTACTS_MODEL = "ContactsModel";
    public static final String FRAGMENT_MODEL = "FragmentModel";

    /*
     * Result codes
     */
    public static final int SETTINGS_ACTIVITY_REQUEST = 301;
    public static final int SELECT_CONTACT_REQUEST = 302;
    public static final int NEW_CONTACT_REQUEST = 303;
    public static final int SELECT_GROUP_REQUEST = 304;

    /*
     * Fragment Tags
     */
    public static final String TABS_FRAGMENT_TAG = "TabsFragment";
    public static final String NO_TABS_FRAGMENT_TAG = "NoTabsFragment";
    public static final String ADAPTER_FRAGMENT_TAG_PREFIX = "android:switcher:";

    /*
     * String generation methods based on constant values
     */

    public static String getFragmentPageTag(int containerId, int tabIndex){
        return ADAPTER_FRAGMENT_TAG_PREFIX + containerId + ":" + tabIndex;
    }

}
