package com.reboot.locately.common;

/**
 * Created by Harold Prabhu on 31-03-2017.
 */

@com.google.firebase.database.IgnoreExtraProperties

public class GroupChatMessage {
    public String text;
    public String speaker;
    public boolean isText;
    public long timestamp;

    public GroupChatMessage()
    {

    }

    public GroupChatMessage(String text, String s, long timestamp, boolean b2) {
        this.text = text;
        this.speaker=s;
        this.timestamp=timestamp;
        this.isText=b2;
    }

}
