package com.reboot.locately.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import com.google.android.gms.games.multiplayer.Invitation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.reboot.locately.R;

import static android.content.Context.MODE_PRIVATE;

public class InvitationAdapter extends RecyclerView.Adapter<InvitationAdapter.InvitationViewHolder>{

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref = database.getReference("users");
    private List<Invitations> contactVOList;
    private Context mContext;
    public InvitationAdapter(List<Invitations> contactVOList, Context mContext){
        this.contactVOList = contactVOList;
        this.mContext = mContext;
    }


    @Override
    public InvitationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.single_invitation_view, null);
        InvitationViewHolder contactViewHolder = new InvitationViewHolder(view);
        return contactViewHolder;
    }

    @Override
    public void onBindViewHolder(final InvitationViewHolder holder, final int position) {
        final Invitations invitation = contactVOList.get(position);
        ref.child(invitation.getInvitedBy()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String name=String.valueOf(dataSnapshot.child("first_name").getValue());
                final String group=String.valueOf(dataSnapshot.child("circles").child(invitation.getInvitedFor()).getValue());
                holder.tvInvitation.setText("You have been invited to the circle "+group+" by "+name);
                holder.addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatabaseReference reference=FirebaseDatabase.getInstance().getReference();
                        SharedPreferences user_prefs = mContext.getSharedPreferences("user_pref", MODE_PRIVATE);
                        reference.child("circles").child(invitation.getInvitedFor()).child("invitations").child(user_prefs.getString("phoneNumber","")).setValue(null);
                        reference.child("users").child(user_prefs.getString("phoneNumber","")).child("my_invitations").child(invitation.getInvitedFor()).setValue(null);
                        reference.child("circles").child(invitation.getInvitedFor()).child("members").child(user_prefs.getString("phoneNumber","")).setValue("true");
                        reference.child("users").child(user_prefs.getString("phoneNumber","")).child("my_circles").child(invitation.getInvitedFor()).setValue(group);
                    }
                });
                holder.rejectButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("circles");
                        SharedPreferences user_prefs = mContext.getSharedPreferences("user_pref", MODE_PRIVATE);
                        reference.child("users").child(user_prefs.getString("phoneNumber","")).child("my_invitations").child(invitation.getInvitedFor()).setValue(null);
                        reference.child(invitation.getInvitedFor()).child("invitations").child(user_prefs.getString("phoneNumber","")).setValue(null);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return contactVOList.size();
    }

    public static class InvitationViewHolder extends RecyclerView.ViewHolder{

        TextView tvInvitation;
        Button addButton,rejectButton;

        public InvitationViewHolder(View itemView) {
            super(itemView);
            tvInvitation = (TextView) itemView.findViewById(R.id.invitation_message);
            addButton = (Button) itemView.findViewById(R.id.accept_button);
            rejectButton = (Button) itemView.findViewById(R.id.reject_button);
        }
    }
}