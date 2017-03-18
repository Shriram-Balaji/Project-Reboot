package com.reboot.locately.activity;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.reboot.locately.R;

public class LoginActivity extends Activity {

    TextView mAppName;
    EditText mUsername,mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAppName = (TextView)findViewById(R.id.app_name);
        mUsername = (EditText)findViewById(R.id.name);
        mPassword = (EditText)findViewById(R.id.password);
        Typeface typeface = Typeface.createFromAsset(getResources().getAssets(),"Pattaya-Regular.ttf");
        mAppName.setTypeface(typeface);
    }

}
