package io.craigmiller160.contacts5.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import io.craigmiller160.contacts5.application.AbstractAndroidMVPApp;
import io.craigmiller160.contacts5.controller.AbstractActivityController;

/**
 * Created by Craig on 2/17/2016.
 */
public abstract class AbstractMVPActivity extends AppCompatActivity{

    private AbstractActivityController activityController;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        activityController = (AbstractActivityController) getMVPApplication()
                .getController(getActivityControllerName(), this);
    }

    protected final AbstractAndroidMVPApp getMVPApplication(){
        return (AbstractAndroidMVPApp) getApplicationContext();
    }

    protected abstract String getActivityControllerName();

    @Override
    protected final void onActivityResult(int requestCode, int resultCode, Intent data){
        activityController.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public final void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        activityController.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public final boolean onOptionsItemSelected(MenuItem item) {
        return activityController.onOptionsItemSelected(item);
    }

}
