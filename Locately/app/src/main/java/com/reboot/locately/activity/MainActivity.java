package com.reboot.locately.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.maps.MapsInitializer;
import com.reboot.locately.R;
import com.reboot.locately.common.LocationFetcher;
import com.reboot.locately.fragment.AddFriends;
import com.reboot.locately.fragment.CheckIn;
import com.reboot.locately.fragment.MyCircle;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public BottomNavigationView navigation;
    Fragment fragment1 = null, fragment2 = null, fragment3 = null, fragment4 = null;
    @BindView(R.id.logo_app_name)
    TextView mLogoTextView;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Typeface logoTypeface = Typeface.createFromAsset(getResources().getAssets(), "Pattaya-Regular.ttf");
        mLogoTextView.setTypeface(logoTypeface);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                selectFragment(item);
                return true;
            }

        };

        navigation = (BottomNavigationView) findViewById(R.id.navigation_view);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
//        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        LocationFetcher locationFetcher = new LocationFetcher(this);
        locationFetcher.fetchCellLocation();
        // Battery status receiver
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        BroadcastReceiver b = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
                boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                        status == BatteryManager.BATTERY_STATUS_FULL;
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                float batteryPct = level / (float) scale;

                int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
                boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
                boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
                Log.d("status", String.valueOf(status));
                Log.d("level", String.valueOf(level));
                Log.d("usbcharge", String.valueOf(usbCharge));
                Log.d("isCharging", String.valueOf(isCharging));
                Log.d("batterypct", String.valueOf(batteryPct));
            }
        };
        Intent intent = this.registerReceiver(b, ifilter);
//        int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
//        boolean isCharging = false;
//        if (status == BatteryManager.BATTERY_STATUS_CHARGING) {
//            Toast.makeText(this, "The battery is charging.", Toast.LENGTH_LONG).show();
//            isCharging = true;
//        } else if (status == BatteryManager.BATTERY_STATUS_DISCHARGING) {
//            Toast.makeText(this, "The battery is discharging.", Toast.LENGTH_LONG).show();
//            isCharging = false;
//        } else if (status == BatteryManager.BATTERY_STATUS_FULL) {
//            Toast.makeText(this, "The battery is full.", Toast.LENGTH_LONG).show();
//            isCharging = true;
//        }
//        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
//                status == BatteryManager.BATTERY_STATUS_FULL;
//        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
//        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
//        float batteryPct = level / (float) scale;
//
//        int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
//        boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
//        boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
//        Log.d("status", String.valueOf(status));
//        Log.d("level", String.valueOf(level));
//        Log.d("usbcharge", String.valueOf(usbCharge));
//        Log.d("isCharging", String.valueOf(isCharging));
//        Log.d("batterypct", String.valueOf(batteryPct));

        MapsInitializer.initialize(this);
        Menu menu = navigation.getMenu();
        selectFragment(menu.getItem(0));
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1:   // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    LocationFetcher loc = new LocationFetcher(getApplicationContext());
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
        }
        // other 'case' lines to check for other
        // permissions this app might request
    }


    // other 'case' lines to check for other
    // permissions this app might request


    protected void selectFragment(MenuItem item) {
        item.setChecked(true);
        Fragment fragment = null;
        switch (item.getItemId()) {
            case R.id.my_circle:
                if (fragment1 == null)
                    fragment1 = new MyCircle();
                fragment = fragment1;
                getSupportFragmentManager().beginTransaction().replace(R.id.main_container,fragment).commit();
                break;

            case R.id.add_friend:
                if (fragment2 == null)
                    fragment2 = new AddFriends();
                fragment = fragment2;
                getSupportFragmentManager().beginTransaction().replace(R.id.main_container,fragment).commit();

                break;

            case R.id.check_in:
                if (fragment3 == null)
                    fragment3 = new CheckIn();
                fragment = fragment3;
                getSupportFragmentManager().beginTransaction().replace(R.id.main_container,fragment).commit();
                break;


//                    case R.id.leave_circle:
//                        if(fragment4==null)
//                            fragment4 = new LeaveCircle();
//                        fragment = fragment4;
//                        break;
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            // Handle the camera action
        } else if (id == R.id.nav_new_circle) {

        } else if (id == R.id.nav_join_circle) {

        } else if (id == R.id.nav_leave_circle) {

        } else if (id == R.id.nav_messages) {

        } else if (id == R.id.nav_settings) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
