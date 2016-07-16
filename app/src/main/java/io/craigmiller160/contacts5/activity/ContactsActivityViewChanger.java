package io.craigmiller160.contacts5.activity;

import android.content.Context;
import android.content.res.ObbInfo;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import io.craigmiller160.contacts5.ContactsApp;
import io.craigmiller160.contacts5.IllegalArgumentCtxException;
import io.craigmiller160.contacts5.R;
import io.craigmiller160.contacts5.model.AndroidModel;
import io.craigmiller160.contacts5.util.AbstractAndroidUtil;

/**
 * Created by craig on 7/16/16.
 */
public class ContactsActivityViewChanger extends AbstractAndroidUtil{

    private AppCompatActivity activity;
    private final AndroidModel contactsModel;

    private static final Object instanceLock = new Object();
    private static ContactsActivityViewChanger instance;


    public static ContactsActivityViewChanger getInstance(){
        synchronized (instanceLock){
            return instance;
        }
    }

    public static void initialize(Context context){
        if(instance == null){
            synchronized (instanceLock){
                if(instance == null){
                    instance = new ContactsActivityViewChanger(context);
                    return;
                }
            }
        }
        throw new IllegalArgumentCtxException("Cannot initialize ContactsActivityViewChanger more than once");
    }

    public static boolean isInitialized(){
        if(instance == null){
            synchronized (instanceLock){
                if(instance == null){
                    return false;
                }
            }
        }
        return true;
    }

    private ContactsActivityViewChanger(Context context){
        super(context);
        this.contactsModel = ContactsApp.getApp().modelFactory().getModel(R.string.model_contacts);
    }

    public void setActivity(AppCompatActivity activity){
        this.activity = activity;
    }

    public void showTabsFragment(){
        //Hide No Tabs Fragment stuff
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        activity.setTitle(getString(R.string.activity_contacts_name_label));
        contactsModel.clearProperty(R.string.prop_contacts_in_group_list);

        //Show Tabs Fragment stuff
        setViewPagerViewVisibility(View.VISIBLE);
        View view = activity.findViewById(R.id.tabs_fragment_container);
        if(view != null){
            view.setVisibility(View.VISIBLE);
        }
    }

    public void showNoTabsFragment(){
        //Hide Tabs Fragment Stuff
        setViewPagerViewVisibility(View.GONE);
        View view = activity.findViewById(R.id.tabs_fragment_container);
        if(view != null){
            view.setVisibility(View.GONE);
        }

        //Show No Tabs Fragment Stuff
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String groupName = contactsModel.getProperty(R.string.prop_selected_group_name, String.class);
        if(groupName != null){
            activity.setTitle(groupName);
        }
    }

    private void setViewPagerViewVisibility(int visibility){
        ViewPager viewPager = (ViewPager) activity.findViewById(R.id.contactsTabsViewPager);
        viewPager.setVisibility(visibility);
        int childCount = viewPager.getChildCount();
        for(int i = 0; i < childCount; i++){
            View view = viewPager.getChildAt(i);
            view.setVisibility(visibility);
        }
    }

}
