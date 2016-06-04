package io.craigmiller160.contacts5.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import io.craigmiller160.contacts5.R;
import io.craigmiller160.contacts5.controller.ControllerFactory;
import io.craigmiller160.contacts5.model.Contact;
import io.craigmiller160.contacts5.model.ContactsStorage;
import io.craigmiller160.contacts5.service.ResourceService;
import io.craigmiller160.contacts5.service.ServiceFactory;

import static io.craigmiller160.contacts5.util.ContactsConstants.*;

/**
 * Created by Craig on 1/22/2016.
 */
public class ContactsArrayAdapter extends ArrayAdapter<Contact> /*implements SectionIndexer*/{

    private ListView listView; //TODO if I don't end up using it, remove this as a requirement

    //TODO need this to be able to differentiate between group use and all contacts use

    private Map<String,Object> sectionMap;
    private final ResourceService resources;

    private List<Contact> contacts;

    public ContactsArrayAdapter(Context context){
        super(context, R.layout.contacts_list_row);
        this.resources = ServiceFactory.getInstance().getResourceService();
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

        if(contacts != null){
            TextView nameTextView = (TextView) view.findViewById(R.id.contactName);
            Contact contact = contacts.get(position);
            if(contact != null){
                nameTextView.setText(contact.getDisplayName());
                view.setTag(R.string.contact_uri, contact.getUri());
            }

            view.setOnClickListener(ControllerFactory.getInstance().getController(SELECT_CONTACT_CONTROLLER, View.OnClickListener.class));
        }

        return view;
    }

    @Override
    public int getCount(){
        //return (Integer) ContactsApplication.getInstance().getModelProperty(CONTACT_COUNT_PROPERTY);
        return contacts != null ? contacts.size() : 0;
    }

//    private View createView(int position, ViewGroup parent){
//
//    }

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
