package io.craigmiller160.contacts5.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import io.craigmiller160.contacts5.R;

/**
 * Created by craig on 6/5/16.
 */
public class ContactsViewAdapter extends RecyclerView.Adapter<ContactsViewAdapter.ContactViewHolder> {

    public ContactsViewAdapter(){

    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LinearLayout layout = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_row, parent, false);


        return new ContactViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        TextView tf = (TextView) holder.findViewById(R.id.contactName);
        tf.setText("" + position);
    }

    @Override
    public int getItemCount() {
        return 200;
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder{

        private final View itemView;

        public ContactViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
        }

        public View findViewById(int id){
            return itemView.findViewById(id);
        }
    }

}
