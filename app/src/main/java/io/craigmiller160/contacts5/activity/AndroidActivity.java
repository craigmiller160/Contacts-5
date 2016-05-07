package io.craigmiller160.contacts5.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.View;

import io.craigmiller160.contacts5.view.AndroidActivityView;

/**
 * Created by craig on 5/4/16.
 */
public abstract class AndroidActivity extends AppCompatActivity {

    private AndroidActivityView androidActivityView;

    protected abstract AndroidActivityView getAndroidView();

    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        this.androidActivityView = getAndroidView();
    }

    @Override
    public final boolean onCreateOptionsMenu(Menu menu){
        if(androidActivityView != null){
            return androidActivityView.onCreateOptionsMenu(menu) &&
                    super.onCreateOptionsMenu(menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public final boolean onPrepareOptionsMenu(Menu menu){
        if(androidActivityView != null){
            return androidActivityView.onPrepareOptionsMenu(menu) &&
                    super.onPrepareOptionsMenu(menu);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public final void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo){
        if(androidActivityView != null){
            androidActivityView.onCreateContextMenu(menu, view, menuInfo);
        }
        super.onCreateContextMenu(menu, view, menuInfo);
    }

    @Override
    public final boolean onCreatePanelMenu(int featureId, Menu menu){
        if(androidActivityView != null){
            return androidActivityView.onCreatePanelMenu(featureId, menu) &&
                    super.onCreatePanelMenu(featureId, menu);
        }
        return super.onCreatePanelMenu(featureId, menu);
    }

    @Override
    public final boolean onPreparePanel(int featureId, View view, Menu menu){
        if(androidActivityView != null){
            return androidActivityView.onPreparePanel(featureId, view, menu) &&
                    super.onPreparePanel(featureId, view, menu);
        }
        return super.onPreparePanel(featureId, view, menu);
    }

}
