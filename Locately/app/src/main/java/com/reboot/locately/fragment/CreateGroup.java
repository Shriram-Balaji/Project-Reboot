package com.reboot.locately.fragment;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.reboot.locately.R;
import com.reboot.locately.common.AllContactsAdapter;
import com.reboot.locately.common.Contacts;
import com.reboot.locately.common.Group;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class CreateGroup extends Fragment {
    private EditText editText;
    AllContactsAdapter contactAdapter;
    private DatabaseReference root= FirebaseDatabase.getInstance().getReference().getRoot();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_create_group, container, false);
        List<Contacts> contactVOList = new ArrayList();
        Contacts contactVO;
        editText=(EditText) view.findViewById(R.id.group_name);
        ContentResolver contentResolver = getContext().getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {

                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
                if (hasPhoneNumber > 0) {
                    String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                    contactVO = new Contacts();
                    contactVO.setName(name);

                    Cursor phoneCursor = contentResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id},
                            null);
                    if (phoneCursor.moveToNext()) {
                        String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        phoneNumber.replace(" ","");
                        if(!phoneNumber.startsWith("+")){
                            phoneNumber="+91"+phoneNumber;
                        }
                        contactVO.setPhone(phoneNumber);
                    }

                    phoneCursor.close();

                    Cursor emailCursor = contentResolver.query(
                            ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (emailCursor.moveToNext()) {
                        String emailId = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                    }
                    contactVOList.add(contactVO);
                }
            }
        }
        RecyclerView rvContacts = (RecyclerView) view.findViewById(R.id.rvContacts2);
        contactAdapter = new AllContactsAdapter(contactVOList, getContext());
        rvContacts.setLayoutManager(new LinearLayoutManager(getContext()));
        rvContacts.setAdapter(contactAdapter);
        Button addFriend=(Button)view.findViewById(R.id.create_group_button);
        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Group group=new Group();
                group.setGroupName(editText.getText().toString());
                if(group.getGroupName().equals("")){
                    Toast.makeText(getContext(),"Group name can\'t be empty",Toast.LENGTH_SHORT).show();
                    return;
                }
                SharedPreferences user_prefs = getContext().getSharedPreferences("user_pref", MODE_PRIVATE);
                String phoneNumber=user_prefs.getString("phoneNumber","");
                group.setCreatedBy(phoneNumber);
                SharedPreferences.Editor editor = user_prefs.edit();

                HashMap<String,String> invitations= new HashMap<>();
                List<Contacts> temp=contactAdapter.getContactsList();
                DatabaseReference temp1=root.child("circles").push();
                String key=temp1.getKey();
                for(Contacts c:temp){
                    if(c.isSelected()){
                        invitations.put(c.getPhone(),"true");
                        root.child("users").child(c.getPhone()).child("my_invitations").child(key).setValue(phoneNumber);
                    }
                }
                group.setInvitations(invitations);
                temp1.setValue(group);
                root.child("users").child(group.getCreatedBy()).child("my_circles").child(key).setValue(group.getGroupName());
                root.child("circles").child(key).child("members").child(phoneNumber).setValue("true");
                editor.putString("currentGroup",key);
                editor.apply();
                Toast.makeText(getContext(),"Group created successfully",Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }


}
