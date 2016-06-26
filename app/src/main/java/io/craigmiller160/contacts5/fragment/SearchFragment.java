package io.craigmiller160.contacts5.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;

import io.craigmiller160.contacts5.ContactsApp;
import io.craigmiller160.contacts5.R;
import io.craigmiller160.contacts5.adapter.ContactsArrayAdapter;
import io.craigmiller160.contacts5.model.AndroidModel;
import io.craigmiller160.contacts5.model.Contact;

/**
 * Created by craig on 6/26/16.
 */
public class SearchFragment extends AbstractContactsFragment<Contact> {

    private static final String TAG = "SearchFragment";

    private AndroidModel contactsModel;

    @Override
    protected ArrayAdapter<Contact> getArrayAdapter() {
        return new ContactsArrayAdapter(getContext(), R.string.prop_filtered_contacts);
    }

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        Log.v(TAG, "SearchFragment created");
        this.contactsModel = ContactsApp.getApp().modelFactory().getModel(R.string.model_contacts);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        View view = super.onCreateView(inflater, container, savedInstance);
        getActivity().findViewById(R.id.add_contact_fab).setVisibility(View.GONE);

        return view;
    }

    @Override
    public void onDestroyView(){
        View view = getView();
        if(view != null){
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        getActivity().findViewById(R.id.add_contact_fab).setVisibility(View.VISIBLE);
        contactsModel.clearProperty(R.string.prop_contacts_in_group_list);
        super.onDestroyView();
    }
}
