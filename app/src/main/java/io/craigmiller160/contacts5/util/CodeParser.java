package io.craigmiller160.contacts5.util;

import android.app.Activity;

import io.craigmiller160.contacts5.IllegalArgumentCtxException;

import static io.craigmiller160.contacts5.util.ContactsConstants.*;

/**
 * Created by craig on 6/12/16.
 */
public class CodeParser {

    private static final String DISPLAY_SETTINGS = "DisplaySettingsActivity";
    private static final String SELECT_CONTACT = "SelectContact-ActionView";
    private static final String NEW_CONTACT = "NewContact-ActionInsert";

    private static final String OK = "ResultOk";
    private static final String CANCELED = "ResultCanceled";

    public static String parseRequestCode(int requestCode){
        switch(requestCode){
            case SETTINGS_ACTIVITY_REQUEST:
                return DISPLAY_SETTINGS;
            case SELECT_CONTACT_REQUEST:
                return SELECT_CONTACT;
            case NEW_CONTACT_REQUEST:
                return NEW_CONTACT;
            default:
                throw new IllegalArgumentCtxException("Unknown request code provided, cannot parse")
                        .addContextValue("Request Code", requestCode);
        }
    }

    public static String parseResultCode(int resultCode){
        switch(resultCode){
            case Activity.RESULT_OK:
                return OK;
            case Activity.RESULT_CANCELED:
                return CANCELED;
            default:
                throw new IllegalArgumentCtxException("Unknown result code provided, cannot parse")
                        .addContextValue("Result Code", resultCode);
        }
    }

}
