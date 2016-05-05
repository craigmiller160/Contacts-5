package io.craigmiller160.contacts5.old.model;

import android.net.Uri;

/**
 * Created by Craig on 1/20/2016.
 */
public class ContactGroup implements Comparable<ContactGroup>{

    private long groupId;
    private String groupName;
    private int groupSize;
    private String accountName;
    private Uri uri;

    public static final String GROUP_ID_PROP = "GroupId";
    public static final String GROUP_NAME_PROP = "GroupName";

    public ContactGroup(){}

    public ContactGroup(long groupId, String groupName, int groupSize){
        this.groupName = groupName;
        this.groupId = groupId;
        this.groupSize = groupSize;
    }

    public void setAccountName(String accountName){
        this.accountName = accountName;
    }

    public String getAccountName(){
        return accountName;
    }

    public void setGroupId(long groupId){
        this.groupId = groupId;
    }

    public long getGroupId(){
        return groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getGroupSize() {
        return groupSize;
    }

    public void setGroupSize(int groupSize) {
        this.groupSize = groupSize;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ContactGroup that = (ContactGroup) o;

        if (groupId != that.groupId) return false;
        if (groupName != null ? !groupName.equals(that.groupName) : that.groupName != null)
            return false;
        if (accountName != null ? !accountName.equals(that.accountName) : that.accountName != null)
            return false;
        return !(uri != null ? !uri.equals(that.uri) : that.uri != null);

    }

    @Override
    public int hashCode() {
        int result = (int) (groupId ^ (groupId >>> 32));
        result = 31 * result + (groupName != null ? groupName.hashCode() : 0);
        result = 31 * result + (accountName != null ? accountName.hashCode() : 0);
        result = 31 * result + (uri != null ? uri.hashCode() : 0);
        return result;
    }

    @Override
    public String toString(){
        return groupName + " (" + accountName + ")";
    }

    @Override
    public int compareTo(ContactGroup another) {
        return groupName.compareToIgnoreCase(another.groupName);
    }
}
