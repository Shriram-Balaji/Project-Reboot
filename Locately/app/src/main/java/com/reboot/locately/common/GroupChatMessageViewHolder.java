package com.reboot.locately.common;

/**
 * Created by Harold Prabhu on 01-04-2017.
 */

import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import com.reboot.locately.R;

public class GroupChatMessageViewHolder extends RecyclerView.ViewHolder {
    public TextView messageText;
    public ImageView photoView;
    //android.widget.TextView timeText;
    //android.widget.RelativeLayout relativeLayout;

    public GroupChatMessageViewHolder(android.view.View itemView) {
        super(itemView);
        messageText = (TextView) itemView.findViewById(R.id.received_text_view);
        photoView = (ImageView) itemView.findViewById(R.id.received_image_view);
        //timeText=(TextView)itemView.findViewById(com.example.android.chat.R.id.time_text_view);
        //relativeLayout=(android.widget.RelativeLayout)itemView.findViewById(com.example.android.chat.R.id.relative_layout);
    }
}