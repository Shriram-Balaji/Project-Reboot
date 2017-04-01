package com.reboot.locately.common;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by aishwarya on 15-03-2017.
 */

public class Locately extends Application{


    @Override
    protected void attachBaseContext(Context Base){
        super.attachBaseContext(Base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        FontChangeCrawler.overrideFont(getApplicationContext(), "SERIF", "Proxima.ttf");
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
