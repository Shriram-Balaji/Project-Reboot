package com.reboot.locately.activity;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.reboot.locately.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends Activity {

    @BindView(R.id.name) EditText mUsername;
    @BindView(R.id.password) EditText mPassword;
    @BindView(R.id.app_name) TextView mAppName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mAppName = (TextView)findViewById(R.id.app_name);
        Typeface typeface = Typeface.createFromAsset(getResources().getAssets(),"Pattaya-Regular.ttf");
        mAppName.setTypeface(typeface);
    }

}
