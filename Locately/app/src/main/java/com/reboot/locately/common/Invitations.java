package com.reboot.locately.common;

/**
 * Created by aravinth on 4/2/17.
 */

public class Invitations {
    private String invitedBy,invitedFor;

    public Invitations(){

    }
    public Invitations(String invitedBy, String invitedFor) {
        this.invitedBy = invitedBy;
        this.invitedFor = invitedFor;
    }



    public String getInvitedBy() {
        return invitedBy;
    }

    public void setInvitedBy(String invitedBy) {
        this.invitedBy = invitedBy;
    }

    public String getInvitedFor() {
        return invitedFor;
    }

    public void setInvitedFor(String invitedFor) {
        this.invitedFor = invitedFor;
    }

}
