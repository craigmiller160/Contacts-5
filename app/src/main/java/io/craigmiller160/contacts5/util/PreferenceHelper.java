package io.craigmiller160.contacts5.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Set;

import io.craigmiller160.contacts5.R;

import static io.craigmiller160.contacts5.service.ContactsQueryConstants.COL_CONTACTS_CONTACT_NAME;
import static io.craigmiller160.contacts5.service.ContactsQueryConstants.COL_CONTACTS_CONTACT_NAME_ALT;
import static io.craigmiller160.contacts5.service.ContactsQueryConstants.COL_DATA_GROUP_CONTACT_NAME;
import static io.craigmiller160.contacts5.service.ContactsQueryConstants.COL_DATA_GROUP_CONTACT_NAME_ALT;
import static io.craigmiller160.contacts5.service.ContactsQueryConstants.COL_GROUP_ACCOUNT;
import static io.craigmiller160.contacts5.service.ContactsQueryConstants.COL_GROUP_TITLE;

/**
 * Created by craig on 6/19/16.
 */
public class PreferenceHelper extends AbstractAndroidUtil{

    public static final int ALL_CONTACTS = 101;
    public static final int CONTACTS_IN_GROUP = 102;

    private final SharedPreferences prefs;
    private final AndroidSystemUtil androidSystemUtil;

    public PreferenceHelper(Context context) {
        super(context);
        this.prefs = PreferenceManager.getDefaultSharedPreferences(context);
        this.androidSystemUtil = new AndroidSystemUtil(context);
    }

    public String getContactSortString(int type){
        String sortBy = prefs.getString(getString(R.string.setting_contact_sort_by_key),
                getString(R.string.array_contact_sort_by_first));
        String sortOrder = prefs.getString(getString(R.string.setting_contact_sort_order_key),
                getString(R.string.array_sort_order_asc));

        String displayNameColumn = type == ALL_CONTACTS ? COL_CONTACTS_CONTACT_NAME : COL_DATA_GROUP_CONTACT_NAME;
        String displayNameAltColumn = type == ALL_CONTACTS ? COL_CONTACTS_CONTACT_NAME_ALT : COL_DATA_GROUP_CONTACT_NAME_ALT;

        String sort = getString(R.string.array_sort_order_asc).equals(sortOrder) ? "ASC" : "DESC";

        if(getString(R.string.array_contact_sort_by_first).equals(sortBy)){
            return String.format("lower(%1$s) %2$s", displayNameColumn, sort);
        }
        else{
            return String.format("lower(%1$s) %2$s", displayNameAltColumn, sort);
        }
    }

    public String getGroupSortString(){
        String sortBy = prefs.getString(getString(R.string.setting_group_sort_by_key),
                getString(R.string.array_group_sort_by_group));
        String sortOrder = prefs.getString(getString(R.string.setting_group_sort_order_key),
                getString(R.string.array_sort_order_asc));

        String sort = getString(R.string.array_sort_order_asc).equals(sortOrder) ? "ASC" : "DESC";

        if(getString(R.string.array_group_sort_by_group).equals(sortBy)){
            return String.format("lower(%1$s) %2$s, lower(%3$s) %2$s", COL_GROUP_TITLE, sort, COL_GROUP_ACCOUNT);
        }
        else{
            return String.format("lower(%1$s) %2$s, lower(%3$s) %2$s", COL_GROUP_ACCOUNT, sort, COL_GROUP_TITLE);
        }
    }

    public Set<String> getAccountsToDisplay(){
        return prefs.getStringSet(getString(R.string.setting_accounts_to_display_key),
                androidSystemUtil.accounts().getAllContactAccountNamesSet());
    }

    public boolean useEmptyGroups(){
        return prefs.getBoolean(getString(R.string.setting_group_empty_key), false);
    }

    public int isPhonesOnly(){
        return prefs.getBoolean(getString(R.string.setting_phones_only_key), true) ? 1 : 0;
    }

    public boolean isFirstNameLastName(){
        String nameFormat = prefs.getString(getString(R.string.setting_contact_name_format_key),
                getString(R.string.array_name_format_first_last));
        return getString(R.string.array_name_format_first_last).equals(nameFormat);
    }
}
