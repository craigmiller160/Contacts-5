package io.craigmiller160.contacts5.old.controller;

import android.app.Activity;
import android.content.Intent;
import android.view.MenuItem;

import io.craigmiller160.contacts5.old.helper.Helper;

/**
 * Created by Craig on 2/17/2016.
 */
public abstract class AbstractActivityController extends AbstractAndroidController{

    //TODO document how methods are empty so they can be selectively overridden by subclasses that need to use them.

    public AbstractActivityController(){}

    public AbstractActivityController(Helper helper) {
        super(helper);
    }

    public AbstractActivityController(Activity activity, Activity activity1) {
        super(activity);
    }

    public AbstractActivityController(Activity activity, Helper helper, Activity activity1) {
        super(activity, helper);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){

    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }
}
