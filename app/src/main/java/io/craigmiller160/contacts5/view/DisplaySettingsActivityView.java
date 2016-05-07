package io.craigmiller160.contacts5.view;

import android.app.Activity;
import android.view.Menu;

/**
 * Created by craig on 5/7/16.
 */
public class DisplaySettingsActivityView extends AndroidActivityView{

    private static final String TAG = "DisplaySettingsActivity";

    public DisplaySettingsActivityView(Activity activity) {
        super(activity);
    }

    @Override
    protected void onCreate() {

    }

    @Override
    protected int getViewResourceId() {
        return 0;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return false;
    }
}
