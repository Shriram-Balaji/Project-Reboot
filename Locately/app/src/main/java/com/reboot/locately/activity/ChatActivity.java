package com.reboot.locately.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
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
import com.reboot.locately.common.ChatMessage;
import com.reboot.locately.common.ChatMessageViewHolder;
import com.squareup.picasso.Picasso;

import java.util.Date;

public class ChatActivity extends AppCompatActivity {
    private static final int RC_PHOTO_PICKER = 1;
    private static final String USER_DETAILS = "user_pref";
    Date wholeDate;
    long timeStamp;
    private com.google.firebase.FirebaseApp app;
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private DatabaseReference ref, ref2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        FirebaseRecyclerAdapter<ChatMessage, ChatMessageViewHolder> madapter;
        Bundle b = getIntent().getExtras();
        String receiver = b.getString("receiver");
        final SharedPreferences user_prefs = getSharedPreferences(USER_DETAILS, MODE_PRIVATE);
        String sender = user_prefs.getString("phoneNumber", "");
        app = com.google.firebase.FirebaseApp.getInstance();
        ref = FirebaseDatabase.getInstance().getReference().child("users/" + sender + "/messages/"+receiver+"/");
        ref2 = FirebaseDatabase.getInstance().getReference().child("users/" + receiver + "/messages/"+sender+"/");
        storage = FirebaseStorage.getInstance(app);

        RecyclerView recycler = (RecyclerView) findViewById(R.id.recycler_view);
        recycler.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(ChatActivity.this);
        recycler.setLayoutManager(layoutManager);

        madapter = new FirebaseRecyclerAdapter<ChatMessage, ChatMessageViewHolder>(
                ChatMessage.class, R.layout.left_layout, ChatMessageViewHolder.class, ref) {
            public void populateViewHolder(ChatMessageViewHolder chatMessageViewHolder,
                                           ChatMessage chatMessage,
                                           int position) {


                if (chatMessage.isSender && chatMessage.isText) {

                    chatMessageViewHolder.messageText.setText(chatMessage.text);
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) chatMessageViewHolder.messageText.getLayoutParams();
                    params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    params.removeRule(RelativeLayout.ALIGN_PARENT_START);
                    chatMessageViewHolder.messageText.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    chatMessageViewHolder.messageText.setLayoutParams(params);
                    chatMessageViewHolder.messageText.setBackground(getResources().getDrawable(R.drawable.ic_right_chat_bubble));
                    chatMessageViewHolder.photoView.setVisibility(View.INVISIBLE);
                } else if (chatMessage.isSender && !chatMessage.isText) {
                    RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) chatMessageViewHolder.photoView.getLayoutParams();
                    params1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    params1.removeRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    chatMessageViewHolder.photoView.setLayoutParams(params1);

                    chatMessageViewHolder.messageText.setVisibility(View.INVISIBLE);
                    Picasso.with(ChatActivity.this)
                            .load(chatMessage.text)
                            .into(chatMessageViewHolder.photoView);
                } else if (!chatMessage.isSender && chatMessage.isText) {
                    chatMessageViewHolder.messageText.setText(chatMessage.text);
                    chatMessageViewHolder.photoView.setVisibility(View.INVISIBLE);
                } else if (!chatMessage.isSender && !chatMessage.isText) {
                    chatMessageViewHolder.messageText.setVisibility(View.INVISIBLE);
                    Picasso.with(ChatActivity.this)
                            .load(chatMessage.text)
                            .into(chatMessageViewHolder.photoView);
                }
            }
        };
        recycler.setAdapter(madapter);
        Button sendButton = (Button) findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wholeDate = new Date();
                timeStamp = wholeDate.getTime();
                EditText msg = (EditText) findViewById(R.id.messageEditText);
                if (!msg.getText().toString().equals("")) {
                    ChatMessage fMessage = new ChatMessage(msg.getText().toString(), true, timeStamp, true);
                    ref.push().setValue(fMessage);
                    fMessage.isSender = false;
                    ref2.push().setValue(fMessage);
                    msg.setText("");
                }
            }
        });
        ImageButton imageBtn = (ImageButton) findViewById(R.id.imagePicker);
        imageBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(android.content.Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(android.content.Intent.createChooser(intent, getString(R.string.complete_action_with)), RC_PHOTO_PICKER);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, android.content.Intent data) {
        //Recieved result from image picker
        if (requestCode == RC_PHOTO_PICKER && resultCode == this.RESULT_OK) {
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
                    @SuppressWarnings("VisibleForTests") ChatMessage fMessage = new ChatMessage(taskSnapshot.getDownloadUrl().toString(), true, timeStamp, false);
                    ref.push().setValue(fMessage);
                    fMessage.isSender = false;
                    ref2.push().setValue(fMessage);
                }
            });
        }
    }
}

