package com.reboot.locately;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by shriram on 1/4/17.
 */

public class CircleViewHolder extends RecyclerView.ViewHolder{

    public TextView mFriendName;
    public TextView mFriendBatteryPercent;
    public ImageView mFriendBatteryIcon;
    public ImageButton mMessageBtn;
    public ImageButton mCallBtn;
    public ImageButton mNavigateBtn;

    public CircleViewHolder(View itemView) {
        super(itemView);
        mFriendName = (TextView) itemView.findViewById(R.id.friend_name);
        mFriendBatteryPercent = (TextView) itemView.findViewById(R.id.friend_battery_percentage);
        mFriendBatteryIcon = (ImageView)itemView.findViewById(R.id.friend_battery);
        mMessageBtn = (ImageButton)itemView.findViewById(R.id.messageButton);
        mCallBtn = (ImageButton)itemView.findViewById(R.id.callButton);
        mNavigateBtn = (ImageButton)itemView.findViewById(R.id.navigateButton);

    }




}