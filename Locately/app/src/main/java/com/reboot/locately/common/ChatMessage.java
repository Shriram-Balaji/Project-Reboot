package com.reboot.locately.common;

/**
 * Created by Harold Prabhu on 31-03-2017.
 */

@com.google.firebase.database.IgnoreExtraProperties

public class ChatMessage {
    public String text;
    public boolean isSender;
    public boolean isText;
    public long timestamp;

    public ChatMessage()
    {

    }

    public ChatMessage(String text, boolean b, long timestamp,boolean b2) {
        this.text = text;
        this.isSender=b;
        this.timestamp=timestamp;
        this.isText=b2;
    }

}
