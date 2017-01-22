/*
 * Copyright 2016 ShadowAngler <craigmiller160@gmail.com> - All Rights Reserved
 * Proprietary / Confidential
 * Unauthorized copying, use, or redistribution of this file is prohibited
 */

package io.craigmiller160.contacts5.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import io.craigmiller160.contacts5.R;
import io.craigmiller160.contacts5.controller.OnClickController;
import io.craigmiller160.contacts5.model.Contact;
import io.craigmiller160.contacts5.util.ContactIconProvider;

/**
 * Created by Craig on 1/22/2016.
 */
public class ContactsArrayAdapter extends MyArrayAdapter<Contact> /*implements SectionIndexer*/{

    private final ContactIconProvider contactIconProvider;

    public ContactsArrayAdapter(Context context, String propertyName){
        super(context, R.layout.contact_row, propertyName);
        this.contactIconProvider = new ContactIconProvider(context);
    }

    public ContactsArrayAdapter(Context context, int resId){
        this(context, context.getString(resId));
    }

    @Override
    public View getView(int position, View view, ViewGroup parent){
        if(view == null){
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.contact_row, parent, false);
        }

        if(getContents() != null && getContents().get(position) != null){
            Contact contact = getContents().get(position);
            TextView nameTextView = (TextView) view.findViewById(R.id.contactName);
            nameTextView.setText(contact.getDisplayName());

            Drawable defaultPic = contactIconProvider.createCircleContactIcon(contact.getDisplayName(), contact.getDisplayName().charAt(0));

            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .resetViewBeforeLoading(true)
                    .showImageOnFail(defaultPic)
                    .showImageOnLoading(defaultPic)
                    .cacheInMemory(true)
                    .displayer(new RoundedBitmapDisplayer(80))
                    .build();

            ImageView photoImageView = (ImageView) view.findViewById(R.id.contact_photo);
            ImageLoader.getInstance().displayImage(contact.getUri().toString(), new ImageViewAware(photoImageView), options);

            OnClickController onClickController = new OnClickController(getContext());
            onClickController.addArg(R.string.on_click_controller_type, OnClickController.CONTACTS_LIST);
            onClickController.addArg(R.string.contact_uri, contact.getUri());
            onClickController.addArg(R.string.contact_name, contact.getDisplayName());

            view.setOnClickListener(onClickController);
        }

        return view;
    }

    @Override
    public Filter getFilter() {
        //Do not call this method directly, use the superclass filter(String) and clearFilter() methods instead
        return new ContactsFilter(this);
    }

    private static class ContactsFilter extends Filter{

        private final ContactsArrayAdapter callbackAdapter;
        private final List<Contact> contacts;

        public ContactsFilter(ContactsArrayAdapter callbackAdapter){
            this.callbackAdapter = callbackAdapter;
            this.contacts = new ArrayList<>();
            contacts.addAll(callbackAdapter.getOriginalContents());
        }

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<Contact> values = new ArrayList<>();
            for(Contact c : contacts){
                if(StringUtils.containsIgnoreCase(c.getDisplayName(), charSequence)){
                    values.add(c);
                }
            }

            FilterResults results = new FilterResults();
            results.count = values.size();
            results.values = values;

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            callbackAdapter.setContents((List<Contact>) filterResults.values);
        }
    }
}
