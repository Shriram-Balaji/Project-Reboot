package com.reboot.locately;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference ref;
    EditText name,password;
    String phone;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        database = FirebaseDatabase.getInstance();
        ref = database.getReference("users");

        name=(EditText)findViewById(R.id.name);
        password=(EditText)findViewById(R.id.password);

	//get phone no from bundle
        phone =this.getIntent().getExtras().getString("phone");


        //insert into firebase
        Button register=(Button)findViewById(R.id.Register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ref.child(phone).setValue(new Users(password.getText().toString(),name.getText().toString()));
                Toast.makeText(getApplicationContext(),"Registration successful", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
