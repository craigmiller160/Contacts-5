package io.craigmiller160.contacts5.model;

import android.graphics.Bitmap;
import android.net.Uri;

import java.io.IOError;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Craig on 1/20/2016.
 */
public class Contact implements Comparable<Contact>, Serializable{

    private long id;
    private String firstName;
    private String lastName;
    private String displayName;
    private Uri uri;
    //private Bitmap photo;
    private String accountName;

    public Contact(){}

    public Contact(long id, String displayName){
        this.id = id;
        this.displayName = displayName;
    }

    public long getId(){
        return id;
    }

    public void setId(long id){
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

//    public Bitmap getPhoto() {
//        return photo;
//    }
//
//    public void setPhoto(Bitmap photo) {
//        this.photo = photo;
//    }

    public void setAccountName(String accountName){
        this.accountName = accountName;
    }

    public String getAccountName(){
        return accountName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Contact contact = (Contact) o;

        if (id != contact.id) return false;
        if (firstName != null ? !firstName.equals(contact.firstName) : contact.firstName != null)
            return false;
        if (lastName != null ? !lastName.equals(contact.lastName) : contact.lastName != null)
            return false;
        if (displayName != null ? !displayName.equals(contact.displayName) : contact.displayName != null)
            return false;
        if (uri != null ? !uri.equals(contact.uri) : contact.uri != null) return false;
        return !(accountName != null ? !accountName.equals(contact.accountName) : contact.accountName != null);

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (displayName != null ? displayName.hashCode() : 0);
        result = 31 * result + (uri != null ? uri.hashCode() : 0);
        result = 31 * result + (accountName != null ? accountName.hashCode() : 0);
        return result;
    }

    @Override
    public String toString(){
        return displayName;
    }

    @Override
    public int compareTo(Contact another) {
        return displayName.compareToIgnoreCase(another.displayName);
    }

    private void writeObject(ObjectOutputStream out) throws IOException{
        out.writeLong(id);
        out.writeUTF(displayName);
        if(uri != null){
            out.writeUTF(uri.toString());
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
        id = in.readLong();
        displayName = in.readUTF();
        uri = Uri.parse(in.readUTF());
    }

    private void readObjectNoData() throws ObjectStreamException{
        //Do nothing
    }
}
