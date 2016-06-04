package io.craigmiller160.contacts5.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
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

import java.util.List;
import java.util.Map;

import io.craigmiller160.contacts5.R;
import io.craigmiller160.contacts5.controller.ControllerFactory;
import io.craigmiller160.contacts5.model.Contact;
import io.craigmiller160.contacts5.service.ContactIconService;
import io.craigmiller160.contacts5.service.ServiceFactory;

import static io.craigmiller160.contacts5.util.ContactsConstants.*;

/**
 * Created by Craig on 1/22/2016.
 */
public class ContactsArrayAdapter extends ArrayAdapter<Contact> /*implements SectionIndexer*/{

    private ListView listView; //TODO if I don't end up using it, remove this as a requirement

    //TODO need this to be able to differentiate between group use and all contacts use

    private Map<String,Object> sectionMap;
    private final ContactIconService contactIconService;

    private List<Contact> contacts;

    public ContactsArrayAdapter(Context context){
        super(context, R.layout.contacts_list_row);
        this.contactIconService = ServiceFactory.getInstance().getContactIconService();
    }

    public void setContactsList(List<Contact> contacts){
        this.contacts = contacts;
        notifyDataSetChanged();
    }

    public List<Contact> getContactsList(){
        return contacts;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent){
        if(view == null){
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.contacts_list_row, parent, false);
        }

        if(contacts != null && contacts.get(position) != null){
            Contact contact = contacts.get(position);
            TextView nameTextView = (TextView) view.findViewById(R.id.contactName);
            nameTextView.setText(contact.getDisplayName());
            view.setTag(R.string.contact_uri, contact.getUri());

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

            view.setOnClickListener(ControllerFactory.getInstance().getController(SELECT_CONTACT_CONTROLLER, View.OnClickListener.class));
        }

        return view;
    }

    @Override
    public int getCount(){
        //return (Integer) ContactsApplication.getInstance().getModelProperty(CONTACT_COUNT_PROPERTY);
        return contacts != null ? contacts.size() : 0;
    }

//    @Override
//    public void add(Contact contact){
//        contactsList.add(contact);
//        notifyDataSetChanged();
//    }
//
//    @Override
//    public void insert(Contact contact, int position){
//        contactsList.add(position, contact);
//        notifyDataSetChanged();
//    }
//
//    public void replace(Contact contact, int position){
//        contactsList.set(position, contact);
//        notifyDataSetChanged();
//    }
//
//    @Override
//    public void remove(Contact contact){
//        contactsList.remove(contact);
//        notifyDataSetChanged();
//    }
//
//    public void remove(int position){
//        contactsList.remove(position);
//        notifyDataSetChanged();
//    }
//
//    @Override
//    public void sort(Comparator<? super Contact> comparator){
//        //TODO custom implementation needed here
//        super.sort(comparator);
//    }

    //TODO none of this model/prop change stuff is really necessary. just use the array adapter api methods
    //TODO use the adapter api methods way way more

    //TODO finish the section indexing stuff

//    @Override
//    public Object[] getSections() {
//        return new Object[0];
//    }
//
//    @Override
//    public int getPositionForSection(int sectionIndex) {
//        return 0;
//    }
//
//    @Override
//    public int getSectionForPosition(int position) {
//        return 0;
//    }
}
