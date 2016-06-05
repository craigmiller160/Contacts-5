package io.craigmiller160.contacts5.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.craigmiller160.contacts5.R;
import io.craigmiller160.contacts5.controller.ControllerFactory;
import io.craigmiller160.contacts5.model.Contact;
import io.craigmiller160.contacts5.model.ModelFactory;
import io.craigmiller160.contacts5.service.ContactIconService;
import io.craigmiller160.contacts5.service.ServiceFactory;

import static io.craigmiller160.contacts5.util.ContactsConstants.CONTACTS_IN_GROUP_LIST;
import static io.craigmiller160.contacts5.util.ContactsConstants.CONTACTS_LIST;
import static io.craigmiller160.contacts5.util.ContactsConstants.CONTACTS_MODEL;
import static io.craigmiller160.contacts5.util.ContactsConstants.SELECT_CONTACT_CONTROLLER;

/**
 * Created by Craig on 1/22/2016.
 */
public class ContactsArrayAdapter extends ArrayAdapter<Contact> implements PropertyChangeListener /*implements SectionIndexer*/{

    //TODO need this to be able to differentiate between group use and all contacts use

    private final ContactIconService contactIconService;
    public static final int CONTACTS = 321;
    public static final int CONTACTS_IN_GROUP = 322;

    private List<Contact> contacts;
    private final int type;

    public ContactsArrayAdapter(Context context, int type){
        super(context, R.layout.contact_row);
        if(type != CONTACTS && type != CONTACTS_IN_GROUP){
            throw new IllegalArgumentException("Invalid type: " + type);
        }
        this.type = type;
        this.contactIconService = ServiceFactory.getInstance().getContactIconService();
        ModelFactory.getInstance().getModel(CONTACTS_MODEL).addPropertyChangeListener(this);
    }

    public void setContactsList(final List<Contact> contacts){
        if(Looper.myLooper() == Looper.getMainLooper()){
            this.contacts = contacts;
            notifyDataSetChanged();
        }
        else{
            Handler h = new Handler(Looper.getMainLooper());
            h.post(new Runnable() {
                @Override
                public void run() {
                    ContactsArrayAdapter.this.contacts = contacts;
                    notifyDataSetChanged();
                }
            });
        }
    }

    public List<Contact> getContactsList(){
        return contacts;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent){
        if(view == null){
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.contact_row, parent, false);
        }

        if(contacts != null && contacts.get(position) != null){
            Contact contact = contacts.get(position);
            TextView nameTextView = (TextView) view.findViewById(R.id.contactName);
            nameTextView.setText(contact.getDisplayName());

            Drawable defaultPic = contactIconService.createContactIcon(contact.getDisplayName(), contact.getDisplayName().charAt(0));

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

            view.setOnClickListener(ControllerFactory.getInstance().getController(SELECT_CONTACT_CONTROLLER, View.OnClickListener.class, args));
        }

        return view;
    }

    @Override
    public int getCount(){
        return contacts != null ? contacts.size() : 0;
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        if(type == CONTACTS && CONTACTS_LIST.equals(event.getPropertyName())){
            setContactsList((List<Contact>) event.getNewValue());
        }
        else if(type == CONTACTS_IN_GROUP && CONTACTS_IN_GROUP_LIST.equals(event.getPropertyName())){
            setContactsList((List<Contact>) event.getNewValue());
        }
    }
}
