package com.reboot.locately.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.digits.sdk.android.AuthCallback;
import com.digits.sdk.android.AuthConfig;
import com.digits.sdk.android.Digits;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsSession;
import com.reboot.locately.R;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;

import io.fabric.sdk.android.Fabric;


public class MainActivity extends AppCompatActivity {

    public static String TAG = "MainActivity";
    private static final String TWITTER_KEY = "8qBdR5fyDCyBz9ldbBMmAwYL1";
    private static final String TWITTER_SECRET = "q9H2M4gmsBpiDr60RAaL2lOQ0RMNZ5EONs7ez8Xhmh9FtdpnK2";
    private AuthConfig config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        //Digits Authentication API
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new TwitterCore(authConfig), new Digits.Builder().build());
        setContentView(R.layout.activity_main);

        AuthCallback authCallback = new AuthCallback() {
            @Override
            public void success(DigitsSession session, String phoneNumber) {
                SharedPreferences settings = getSharedPreferences("Fabrics", 0);
                SharedPreferences.Editor editor = settings.edit();

                //always set the boolean to false initally
                boolean login = settings.getBoolean("login", false);
                editor.commit();
                //check whether user logged out or not
                if (login) {
                    //check whether existing user or not
                    Intent intent = new Intent(getApplicationContext(), Register.class);
                    Bundle bundle = new Bundle();
                    //send phone no in bundle
                    bundle.putString("phone", phoneNumber);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("phone", phoneNumber);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void failure(DigitsException error) {
                // Do something
                Log.d(TAG,"Login Failed");
            }
        };
        AuthConfig.Builder builder = new AuthConfig.Builder();
        builder.withAuthCallBack(authCallback);
        config = builder.build();
        Digits.authenticate(config);//invokes OTP activity
    }
}