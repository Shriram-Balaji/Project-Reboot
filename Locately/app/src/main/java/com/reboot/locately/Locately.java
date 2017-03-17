package com.reboot.locately;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

/**
 * Created by aishwarya on 15-03-2017.
 */

public class Locately extends Application{


    @Override
    protected void attachBaseContext(Context Base){
        super.attachBaseContext(Base);
        MultiDex.install(this);
    }

}
