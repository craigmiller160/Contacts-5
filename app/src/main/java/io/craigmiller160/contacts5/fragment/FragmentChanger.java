package io.craigmiller160.contacts5.fragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.widget.ArrayAdapter;

import io.craigmiller160.contacts5.ContactsApp;
import io.craigmiller160.contacts5.R;
import io.craigmiller160.contacts5.log.Logger;
import io.craigmiller160.contacts5.model.AndroidModel;
import io.craigmiller160.contacts5.model.Contact;
import io.craigmiller160.contacts5.util.AbstractAndroidUtil;
import io.craigmiller160.utils.reflect.ObjectCreator;

/**
 * Created by craig on 6/12/16.
 */
public class FragmentChanger extends AbstractAndroidUtil{

    private static final String TAG = "FragmentChanger";
    private static final Logger logger = Logger.newLogger(TAG);

    private AndroidModel contactsModel;

    public FragmentChanger(Context context) {
        super(context);
        this.contactsModel = ContactsApp.getApp().modelFactory().getModel(R.string.model_contacts);
    }

    public void addFragment(FragmentManager fm, int displayContainerId,
                            Class<? extends Fragment> displayFragmentType, String displayFragmentTag){
        Fragment fragment = ObjectCreator.instantiateClass(displayFragmentType);
        addFragment(fm, displayContainerId, fragment, displayFragmentTag);
    }

    public void addFragment(FragmentManager fm, int displayContainerId,
                            Fragment displayFragment, String displayFragmentTag){
        logger.d(TAG, "Adding Fragment: " + displayFragmentTag);
        contactsModel.setProperty(R.string.prop_displayed_fragment, displayFragmentTag);
        fm.beginTransaction()
                .replace(displayContainerId, displayFragment, displayFragmentTag)
                .addToBackStack(null)
                .commit();
    }

    public void addTabsFragment(FragmentManager fm){
        addFragment(fm, R.id.tabs_fragment_container, TabsFragment.class, getString(R.string.tag_tabs_fragment));
    }

    public void addNoTabsFragment(FragmentManager fm){
        addFragment(fm, R.id.no_tabs_fragment_container, ContactsInGroupFragment.class, getString(R.string.tag_no_tabs_fragment));
    }

    public void addSearchFragment(FragmentManager fm, ArrayAdapter<Contact> adapter){
        SearchFragment fragment = new SearchFragment();
        fragment.setArrayAdapter(adapter);
        addFragment(fm, R.id.no_tabs_fragment_container, fragment, getString(R.string.tag_search_fragment));
    }

}
