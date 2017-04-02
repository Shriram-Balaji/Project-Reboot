package com.reboot.locately.fragment;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.reboot.locately.R;
import com.reboot.locately.common.GroupChatMessage;
import com.reboot.locately.common.GroupChatMessageViewHolder;
import com.squareup.picasso.Picasso;

import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class GroupChat extends Fragment {

    private static final int RC_PHOTO_PICKER = 1;
    private static final String USER_DETAILS = "user_pref";
    final String currentCircle = "-Kgh_dc-Az6TKCK5mKDt";
    final String currentNumber = "+919940513849";
    Date wholeDate;
    long timeStamp;
    EditText msg;
    private com.google.firebase.FirebaseApp app;
    private FirebaseStorage storage;
    private com.google.firebase.storage.StorageReference storageRef;
    private DatabaseReference ref, ref2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final SharedPreferences user_prefs = getActivity().getSharedPreferences(USER_DETAILS, MODE_PRIVATE);
        View view = inflater.inflate(R.layout.activity_chat, container, false);

        msg = (EditText) view.findViewById(R.id.messageEditText);


        FirebaseRecyclerAdapter<GroupChatMessage, GroupChatMessageViewHolder> madapter;

        String cur_circle = user_prefs.getString("currentGroup", "");
        app = com.google.firebase.FirebaseApp.getInstance();
        ref = FirebaseDatabase.getInstance().getReference().child("circles/" + cur_circle + "/messages/");
//        ref2 = FirebaseDatabase.getInstance().getReference().child("users/+919790821223/messages/");
        storage = FirebaseStorage.getInstance(app);

        RecyclerView recycler = (RecyclerView) view.findViewById(R.id.recycler_view);
        recycler.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recycler.setLayoutManager(layoutManager);

        madapter = new FirebaseRecyclerAdapter<GroupChatMessage, GroupChatMessageViewHolder>(
                GroupChatMessage.class, R.layout.left_layout, GroupChatMessageViewHolder.class, ref) {
            @Override
            protected void populateViewHolder(GroupChatMessageViewHolder groupChatMessageViewHolder, GroupChatMessage groupChatMessage, int position) {

                if (groupChatMessage.speaker.equals(currentNumber) && groupChatMessage.isText) {

                    groupChatMessageViewHolder.messageText.setText("Me:" + ":" + groupChatMessage.text);
                    android.widget.RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) groupChatMessageViewHolder.messageText.getLayoutParams();
                    params.addRule(android.widget.RelativeLayout.ALIGN_PARENT_RIGHT);
                    params.removeRule(android.widget.RelativeLayout.ALIGN_PARENT_START);
                    groupChatMessageViewHolder.messageText.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    groupChatMessageViewHolder.messageText.setLayoutParams(params);
                    groupChatMessageViewHolder.messageText.setBackground(getResources().getDrawable(R.drawable.ic_right_chat_bubble));
                    groupChatMessageViewHolder.photoView.setVisibility(View.INVISIBLE);
                } else if (groupChatMessage.speaker.equals(currentNumber) && !groupChatMessage.isText) {
                    android.widget.RelativeLayout.LayoutParams params1 = (android.widget.RelativeLayout.LayoutParams) groupChatMessageViewHolder.photoView.getLayoutParams();
                    params1.addRule(android.widget.RelativeLayout.ALIGN_PARENT_RIGHT);
                    params1.removeRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    groupChatMessageViewHolder.photoView.setLayoutParams(params1);

                    groupChatMessageViewHolder.messageText.setVisibility(android.view.View.INVISIBLE);
                    Picasso.with(getActivity())
                            .load(groupChatMessage.text)
                            .into(groupChatMessageViewHolder.photoView);
                } else if (!groupChatMessage.speaker.equals(currentNumber) && groupChatMessage.isText) {
                    groupChatMessageViewHolder.messageText.setText(groupChatMessage.speaker + ":" + groupChatMessage.text);
                    groupChatMessageViewHolder.photoView.setVisibility(android.view.View.INVISIBLE);
                } else if (!groupChatMessage.speaker.equals(currentNumber) && !groupChatMessage.isText) {
                    groupChatMessageViewHolder.messageText.setVisibility(android.view.View.INVISIBLE);
                    Picasso.with(getActivity())
                            .load(groupChatMessage.text)
                            .into(groupChatMessageViewHolder.photoView);
                }
            }
        };
        recycler.setAdapter(madapter);
        Button sendButton = (Button) view.findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                wholeDate = new Date();
                timeStamp = wholeDate.getTime();
                if (!msg.getText().toString().equals("")) {
                    GroupChatMessage fMessage = new GroupChatMessage(msg.getText().toString(), user_prefs.getString("phoneNumber", ""), timeStamp, true);
                    ref.push().setValue(fMessage);
                    msg.setText("");
                }
            }
        });
        ImageButton imageBtn = (android.widget.ImageButton) view.findViewById(R.id.imagePicker);
        imageBtn.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(android.view.View v) {
                android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(android.content.Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(android.content.Intent.createChooser(intent, getString(R.string.complete_action_with)), RC_PHOTO_PICKER);
            }
        });
        return view;
    }

    public void onActivityResult(int requestCode, int resultCode, android.content.Intent data) {
        //Recieved result from image picker
        if (requestCode == RC_PHOTO_PICKER && resultCode == getActivity().RESULT_OK) {
            android.net.Uri selectedImageUri = data.getData();
            // Get a reference to the location where we'll store our photos
            storageRef = storage.getReference("photos/");
            // Get a reference to store file at chat_photos/<FILENAME>
            final StorageReference photoRef = storageRef.child(selectedImageUri.getLastPathSegment());
            // Upload file to Firebase Storage
            photoRef.putFile(selectedImageUri).addOnSuccessListener(new com.google.android.gms.tasks.OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Get a URL to the uploaded content
                    wholeDate = new Date();
                    timeStamp = wholeDate.getTime();
                    //android.net.Uri downloadUrl = taskSnapshot.getUploadSessionUri();
                    @SuppressWarnings("VisibleForTests") GroupChatMessage fMessage = new GroupChatMessage(taskSnapshot.getDownloadUrl().toString(), currentNumber, timeStamp, false);
                    ref.push().setValue(fMessage);
                }
            });
        }
    }


}


