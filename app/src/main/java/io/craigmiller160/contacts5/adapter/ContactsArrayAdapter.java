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

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import io.craigmiller160.contacts5.R;
import io.craigmiller160.contacts5.application.ContactsApplication;
import io.craigmiller160.contacts5.controller.ContactSelectionController;
import io.craigmiller160.contacts5.model.Contact;
import io.craigmiller160.contacts5.model.ContactsStorage;

import static io.craigmiller160.contacts5.helper.ContactsHelper.*;

/**
 * Created by Craig on 1/22/2016.
 */
public class ContactsArrayAdapter extends ArrayAdapter<Contact> implements SectionIndexer{

    private ListView listView; //TODO if I don't end up using it, remove this as a requirement

    //TODO need this to be able to differentiate between group use and all contacts use

    private Map<String,Object> sectionMap;

    public ContactsArrayAdapter(Activity activity, ListView listView){
        super(activity, R.layout.contacts_list_row);
        this.listView = listView;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent){
        if(view == null){
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.contacts_list_row, parent, false);
        }

        TextView nameTextView = (TextView) view.findViewById(R.id.contactName);
        final Contact contact = (Contact) ContactsApplication.getInstance().getModelProperty(CONTACT_AT_INDEX_PROP, position);
        if(contact != null){
            nameTextView.setText(contact.getDisplayName());
        }
        view.setOnClickListener(new ContactSelectionController(contact, position));

        return view;
    }

    @Override
    public int getCount(){
        return (Integer) ContactsApplication.getInstance().getModelProperty(CONTACT_COUNT_PROPERTY);
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

    @Override
    public Object[] getSections() {
        return new Object[0];
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        return 0;
    }

    @Override
    public int getSectionForPosition(int position) {
        return 0;
    }
}
