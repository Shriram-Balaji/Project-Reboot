package com.reboot.locately.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.digits.sdk.android.AuthCallback;
import com.digits.sdk.android.AuthConfig;
import com.digits.sdk.android.Digits;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsSession;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.reboot.locately.R;
import com.reboot.locately.common.Users;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;

import io.fabric.sdk.android.Fabric;

public class UserRegistrationActivity extends AppCompatActivity {

    private static final String USER_DETAILS = "user_pref";
    FirebaseDatabase database;
    DatabaseReference ref;
    EditText mFirstName,mLastName,mPhoneNumber;
    TextView mLogoTextView;
    String phone;

    public static String TAG = "RegistrationActivity";
    private static final String TWITTER_KEY = "8qBdR5fyDCyBz9ldbBMmAwYL1";
    private static final String TWITTER_SECRET = "q9H2M4gmsBpiDr60RAaL2lOQ0RMNZ5EONs7ez8Xhmh9FtdpnK2";
    private AuthConfig config;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        final SharedPreferences user_prefs = getSharedPreferences(USER_DETAILS, MODE_PRIVATE);


        if(user_prefs.getBoolean("login", false)){
            goToMainActivity();
        }

        //Database Ref
        database = FirebaseDatabase.getInstance();
        ref = database.getReference("users");
        Log.d(TAG,ref.toString());

        //View Declarations
        mLogoTextView = (TextView)findViewById(R.id.App_Name) ;
        mFirstName=(EditText)findViewById(R.id.firstName);
        mLastName=(EditText)findViewById(R.id.lastName);
        mPhoneNumber = (EditText)findViewById(R.id.phoneNumber);
        phone = mPhoneNumber.getText().toString();

        Typeface logoTypeface = Typeface.createFromAsset(getResources().getAssets(),"Pattaya-Regular.ttf");
        mLogoTextView.setTypeface(logoTypeface);

        final String firstName = mFirstName.getText().toString();
        final String lastName = mLastName.getText().toString();


        //Digits Authentication API
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new TwitterCore(authConfig), new Digits.Builder().build());

        //Callback after digits authenticates
        final AuthCallback authCallback = new AuthCallback() {
            @Override
            public void success(DigitsSession session,String phoneNumber) {
                SharedPreferences.Editor editor = user_prefs.edit();

                //always set the boolean to false initially

                //check whether user logged out or not

                    editor.putBoolean("login",true);
                    editor.putString("phoneNumber",phoneNumber);
                    editor.apply();
                    Log.d(TAG,phoneNumber);
                    Users user = new Users();
                    user.setFirst_name(mFirstName.getText().toString());
                    user.setLast_name(mLastName.getText().toString());
                    user.setBattery_percent("");
                    user.setLatitude("");
                    user.setLongitude("gt");
                    ref.child(phoneNumber).setValue(user);
                    Toast.makeText(getApplicationContext(), "Details Saved", Toast.LENGTH_LONG).show();
                    goToMainActivity();


            }

            @Override
            public void failure(DigitsException error) {
                // Do something
                Log.d(TAG,"Login Failed");
            }
        };




        //invokes OTP activity
        FloatingActionButton register=(FloatingActionButton) findViewById(R.id.Register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AuthConfig.Builder builder = new AuthConfig.Builder();
                builder.withAuthCallBack(authCallback);
                builder.withPhoneNumber(phone);
                config = builder.build();
                Digits.authenticate(config);
            }
        });
    }

    public void goToMainActivity(){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }


}
