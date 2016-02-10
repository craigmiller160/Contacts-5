package io.craigmiller160.contacts5.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Craig on 1/20/2016.
 */
public class ContactGroup {

    private long groupId;
    private String groupName;
    private int groupSize;
    private String groupAccountName;

    public static final String GROUP_ID_PROP = "GroupId";
    public static final String GROUP_NAME_PROP = "GroupName";

    public ContactGroup(){}

    public ContactGroup(long groupId, String groupName, int groupSize){
        this.groupName = groupName;
        this.groupId = groupId;
        this.groupSize = groupSize;
    }

    public void setGroupAccountName(String groupAccountName){
        this.groupAccountName = groupAccountName;
    }

    public String getGroupAccountName(){
        return groupAccountName;
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
}
