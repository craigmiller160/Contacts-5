/*
 * Copyright 2016 ShadowAngler <craigmiller160@gmail.com> - All Rights Reserved
 * Proprietary / Confidential
 * Unauthorized copying, use, or redistribution of this file is prohibited
 */

package io.craigmiller160.contacts5.util;

import android.content.Context;

/**
 * A simple class for global constant values
 *
 * Created by craig on 5/4/16.
 */
public class ContactsConstants {

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

    public static String getFullDebugPath(Context context){
        return context.getExternalFilesDir(null) != null ? context.getExternalFilesDir(null).getAbsolutePath() : "null";
    }

}
