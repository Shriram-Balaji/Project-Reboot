package com.reboot.locately.fragment;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.games.multiplayer.Invitation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.reboot.locately.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.reboot.locately.common.InvitationAdapter;
import com.reboot.locately.common.Invitations;

import static android.content.Context.MODE_PRIVATE;

public class InvitationFragment extends Fragment {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference("users");
    List<Invitations> invitations=new ArrayList<>();
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_invitation, container, false);
        SharedPreferences user_prefs = getActivity().getSharedPreferences("user_pref", MODE_PRIVATE);
        String phone=user_prefs.getString("phoneNumber","");
        Log.d("TAGG","test"+phone);
        ref.child(phone).child("my_invitations").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                invitations.clear();
                HashMap<String,String> hashMap=(HashMap<String, String>) dataSnapshot.getValue();

                if(hashMap!=null) {
                    Log.d("TAGG", "test" + hashMap.size());
                    for (String key : hashMap.keySet()) {
                        Invitations temp = new Invitations();
                        temp.setInvitedFor(key);
                        temp.setInvitedBy(hashMap.get(key));
                        invitations.add(temp);
                        Log.d("TAG1", key);
                        Log.d("TAG2", hashMap.get(key));
                    }
                    RecyclerView rvContacts = (RecyclerView) view.findViewById(R.id.rvInvitations);
                    InvitationAdapter invitationAdapter = new InvitationAdapter(invitations, getContext());
                    rvContacts.setLayoutManager(new LinearLayoutManager(getContext()));
                    rvContacts.setAdapter(invitationAdapter);
                }

                else{
                    TextView tv = (TextView)view.findViewById(R.id.noInvites);
                    tv.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return view;
    }

}
