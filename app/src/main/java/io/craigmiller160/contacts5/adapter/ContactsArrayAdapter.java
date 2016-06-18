package io.craigmiller160.contacts5.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

import java.util.HashMap;
import java.util.Map;

import io.craigmiller160.contacts5.ContactsApp;
import io.craigmiller160.contacts5.R;
import io.craigmiller160.contacts5.controller.OnClickController;
import io.craigmiller160.contacts5.model.Contact;
import io.craigmiller160.contacts5.util.ContactIconProvider;

import static io.craigmiller160.contacts5.util.ContactsConstants.SELECT_CONTACT_CONTROLLER;

/**
 * Created by Craig on 1/22/2016.
 */
public class ContactsArrayAdapter extends MyArrayAdapter<Contact> /*implements SectionIndexer*/{

    private final ContactIconProvider contactIconProvider;

    public ContactsArrayAdapter(Context context, String propertyName){
        super(context, R.layout.contact_row, propertyName);
        this.contactIconProvider = new ContactIconProvider(context);
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

            Map<String,Object> args = new HashMap<>();
            args.put(getContext().getString(R.string.contact_uri), contact.getUri());
            args.put(getContext().getString(R.string.contact_name), contact.getDisplayName());

            view.setOnClickListener(new OnClickController(getContext(), args, OnClickController.CONTACTS_LIST));
        }

        return view;
    }
}
