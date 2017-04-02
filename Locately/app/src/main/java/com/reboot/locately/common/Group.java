package com.reboot.locately.common;

import java.util.HashMap;

/**
 * Created by aishwarya on 31-03-2017.
 */

public class Group{
    private String createdBy;
    private String groupName;
    private HashMap<String,String> invitations;
    public Group(){

    }

    public Group(String createdBy, String groupName, HashMap<String, String> members) {
        this.createdBy = createdBy;
        this.groupName = groupName;
        this.invitations = members;
    }

    public HashMap<String, String> getInvitations() {
        return invitations;
    }

    public void setInvitations(HashMap<String, String> invitations) {
        this.invitations = invitations;
    }

    public String getCreatedBy(){return createdBy;}

    public String getGroupName(){return groupName;}

    public void setCreatedBy(String createdBy){this.createdBy=createdBy;}

    public void setGroupName(String group_name){this.groupName=group_name;}

}
