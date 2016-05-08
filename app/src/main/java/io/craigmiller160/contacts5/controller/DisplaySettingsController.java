package io.craigmiller160.contacts5.controller;

import android.content.Context;
import android.preference.Preference;
import android.util.Log;

import io.craigmiller160.contacts5.R;
import io.craigmiller160.contacts5.service.ResourceService;
import io.craigmiller160.contacts5.service.ServiceFactory;
import io.craigmiller160.locus.Locus;
import io.craigmiller160.locus.annotations.LController;

import static io.craigmiller160.contacts5.util.ContactsConstants.*;

/**
 * Created by craig on 5/7/16.
 */
@LController(name = DISPLAY_SETTINGS_CONTROLLER)
public class DisplaySettingsController extends AbstractAndroidController implements Preference.OnPreferenceChangeListener {

    private static final String TAG = "DisplaySettingsContrllr";
    private final ResourceService resources;

    public DisplaySettingsController(Context context) {
        super(context);
        this.resources = ServiceFactory.getInstance().getResourceService();
    }

    //TODO put validation here for the values

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String key = preference.getKey();
        Log.d(TAG, "Preference changed: Key: " + key + " New Value: " + newValue);
        if(key.equals(resources.getString(R.string.accounts_to_display_prop))){
            Locus.model.setValue(resources.getString(R.string.accounts_to_display_prop), newValue);
            return true;
        }
        else if(key.equals(resources.getString(R.string.sort_order_prop))){
            Locus.model.setValue(resources.getString(R.string.sort_order_prop), newValue);
            return true;
        }
        else if(key.equals(resources.getString(R.string.sort_by_prop))){
            Locus.model.setValue(resources.getString(R.string.sort_by_prop), newValue);
            return true;
        }
        else if(key.equals(resources.getString(R.string.name_format_prop))){
            Locus.model.setValue(resources.getString(R.string.name_format_prop), newValue);
            return true;
        }
        else if(key.equals(resources.getString(R.string.phones_only_prop))){
            Locus.model.setValue(resources.getString(R.string.phones_only_prop), newValue);
            return true;
        }

        return false;
    }
}
