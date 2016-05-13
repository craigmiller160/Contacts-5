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

import java.util.Map;

import io.craigmiller160.contacts5.R;
import io.craigmiller160.contacts5.model.Contact;
import io.craigmiller160.contacts5.model.ContactsStorage;
import io.craigmiller160.contacts5.service.ResourceService;
import io.craigmiller160.contacts5.service.ServiceFactory;
import io.craigmiller160.locus.Locus;
import io.craigmiller160.locus.annotations.LView;

/**
 * Created by Craig on 1/22/2016.
 */
@LView
public class ContactsArrayAdapter extends ArrayAdapter<Contact> implements SectionIndexer{

    private ListView listView; //TODO if I don't end up using it, remove this as a requirement

    //TODO need this to be able to differentiate between group use and all contacts use

    private Map<String,Object> sectionMap;
    private final ResourceService resources;

    public ContactsArrayAdapter(Activity activity, ListView listView){
        super(activity, R.layout.contacts_list_row);
        this.listView = listView;
        this.resources = ServiceFactory.getInstance().getResourceService();
        Locus.view.registerView(this);
    }

    @Override
    public void finalize() throws Throwable{
        Locus.view.unregisterView(this);
        super.finalize();
    }

    public void setContactsStorage(ContactsStorage storage){
        //TODO come up with better approach here
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View view, ViewGroup parent){
        if(view == null){
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.contacts_list_row, parent, false);
        }

        TextView nameTextView = (TextView) view.findViewById(R.id.contactName);
        try{
            Contact contact = Locus.model.getValue(resources.getString(R.string.contact_at_index_prop), Contact.class, position);
            if(contact != null){
                nameTextView.setText(contact.getDisplayName());
            }
        }
        catch(Throwable t){
            t.printStackTrace();
        }

//        view.setOnClickListener(new ContactSelectionController(contact, position));

        return view;
    }

    @Override
    public int getCount(){
        //return (Integer) ContactsApplication.getInstance().getModelProperty(CONTACT_COUNT_PROPERTY);
        return Locus.model.getValue(resources.getString(R.string.contacts_count_prop), Integer.class);
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
