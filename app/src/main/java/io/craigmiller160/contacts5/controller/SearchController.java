package io.craigmiller160.contacts5.controller;

import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.MenuItem;

import java.util.HashMap;
import java.util.Map;

import io.craigmiller160.contacts5.ContactsApp;
import io.craigmiller160.contacts5.IllegalArgumentCtxException;
import io.craigmiller160.contacts5.R;
import io.craigmiller160.contacts5.fragment.ContactsInGroupFragment;
import io.craigmiller160.contacts5.fragment.FragmentChanger;
import io.craigmiller160.contacts5.fragment.TabsFragment;
import io.craigmiller160.contacts5.model.AndroidModel;

/**
 * Created by craig on 6/25/16.
 */
public class SearchController extends AbstractAndroidController implements SearchView.OnCloseListener, SearchView.OnQueryTextListener, MenuItemCompat.OnActionExpandListener{

    private final FragmentChanger fragmentChanger;
    private final AndroidModel contactsModel;

    public SearchController(AppCompatActivity activity) {
        this(activity, new HashMap<String, Object>());
    }

    public SearchController(AppCompatActivity activity, Map<String,Object> args){
        super(activity, args);
        this.fragmentChanger = new FragmentChanger(activity);
        this.contactsModel = ContactsApp.getApp().modelFactory().getModel(R.string.model_contacts);
    }

    public AppCompatActivity getActivity(){
        return (AppCompatActivity) getContext();
    }

    @Override
    public boolean onClose() {
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        String displayedFragment = contactsModel.getProperty(R.string.prop_displayed_fragment, String.class);
        int fragmentContainerId = -1;
        Class<? extends Fragment> fragmentClass = null;
        if(getString(R.string.tag_tabs_fragment).equals(displayedFragment)){
            fragmentContainerId = R.id.tabs_fragment_container;
            fragmentClass = TabsFragment.class;
        }
        else if(getString(R.string.tag_no_tabs_fragment).equals(displayedFragment)){
            fragmentContainerId = R.id.no_tabs_fragment_container;
            fragmentClass = ContactsInGroupFragment.class;
        }
        else{
            throw new IllegalArgumentCtxException("Invalid displayedFragment setting")
                    .addContextValue("Displayed Fragment", displayedFragment);
        }

        fragmentChanger.displayFragment(getActivity().getSupportFragmentManager(), fragmentContainerId,
                fragmentClass, displayedFragment, new String[]{getString(R.string.tag_search_fragment)}, true);

        return true;
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        fragmentChanger.displaySearchFragment(getActivity().getSupportFragmentManager());
        return true;
    }
}
