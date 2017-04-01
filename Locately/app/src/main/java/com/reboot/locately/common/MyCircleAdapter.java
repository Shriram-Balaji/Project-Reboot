package com.reboot.locately.common;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.reboot.locately.R;

import java.util.List;

public class MyCircleAdapter extends RecyclerView.Adapter<MyCircleAdapter.CircleViewHolder> {

    private List<String> contactVOList;
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");

    private Context mContext;

    public MyCircleAdapter(List<String> contactVOList, Context mContext) {
        this.contactVOList = contactVOList;
        this.mContext = mContext;
    }

    public List<String> getContactsList() {
        return contactVOList;
    }

    @Override
    public CircleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.card_friend_info, null);
        CircleViewHolder circleViewHolder = new CircleViewHolder(view);
        return circleViewHolder;
    }

    @Override
    public void onBindViewHolder(final CircleViewHolder holder, int position) {
        final String contactVO = contactVOList.get(position);
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = String.valueOf(dataSnapshot.child("first_name").getValue()) + " " + String.valueOf(dataSnapshot.child("last_name").getValue());
                String battery_level = String.valueOf(dataSnapshot.child("battery_percent").getValue()) + " %";
//
//                SimpleDateFormat dateFormat = new SimpleDateFormat("hh-mm-ss");
//                String last_seen_time = dateFormat.format(new Date((Long) dataSnapshot.child("lastLocationUpdate").getValue()));
//                String last_seen = "Last Online At " + last_seen_time;
                holder.mFriendName.setText(name);
                holder.mFriendBatteryPercent.setText(battery_level);
//                holder.mFriendLastSeen.setText(last_seen);
                holder.mCallBtn.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        mContext = v.getContext();
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + contactVO));
                        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.

                            return;
                        }
                        else
                            mContext.startActivity(intent);

                    }
                });

                holder.mMessageBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Intent chatActivity = new Intent(this,ChatMessageActivity.class);

                    }
                });

                holder.mNavigateBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                Log.i("name","test"+name);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("error", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        String contact = contactVO.replace(" ","");
        //Log.d("TAG",contact);
        ref.child(contact).addValueEventListener(postListener);

    }



    @Override
    public int getItemCount() {
        return contactVOList.size();
    }

    public static class CircleViewHolder extends RecyclerView.ViewHolder{

        public TextView mFriendName;
        public TextView mFriendBatteryPercent;
        public TextView mFriendLastSeen;
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
            mFriendLastSeen = (TextView)itemView.findViewById(R.id.friend_last_seen);
        }
    }
}