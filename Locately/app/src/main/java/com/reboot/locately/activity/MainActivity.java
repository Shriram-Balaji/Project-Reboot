package com.reboot.locately.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.gms.maps.MapsInitializer;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.reboot.locately.R;
import com.reboot.locately.common.LocationFetcher;
import com.reboot.locately.fragment.AddFriends;
import com.reboot.locately.fragment.CheckIn;
import com.reboot.locately.fragment.CreateGroup;
import com.reboot.locately.fragment.MyCircle;
import com.reboot.locately.fragment.ProfileFragment;
import com.reboot.locately.fragment.SettingsFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String USER_DETAILS = "user_pref";
    public BottomNavigationView navigation;
    List<String> list;
    ArrayList<String> keyList;
    Spinner spinner2;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference("users");
    Fragment fragment1 = null, fragment2 = null, fragment3 = null, fragment4 = null;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener;
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot();

    // TextView mLogoTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        final SharedPreferences user_prefs = getSharedPreferences(USER_DETAILS, MODE_PRIVATE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Typeface logoTypeface = Typeface.createFromAsset(getResources().getAssets(), "Pattaya-Regular.ttf");
        // mLogoTextView.setTypeface(logoTypeface);
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
                return false;
            }

        };

        navigation = (BottomNavigationView) findViewById(R.id.navigation_view);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        MapsInitializer.initialize(this);

        Menu menu = navigation.getMenu();
        selectFragment(menu.getItem(0));
        addItemsOnSpinner2();
        addListenerOnButton();

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

                ref.child(user_prefs.getString("phoneNumber", "")).child("battery_percent").setValue(level);

            }
        };
    }

    protected void selectFragment(MenuItem item) {
        item.setChecked(true);
        Fragment fragment = null;
        switch (item.getItemId()) {
            case R.id.my_circle:
                if (fragment1 == null)
                    fragment1 = new MyCircle();
                fragment = fragment1;
                break;
            case R.id.message:
                if (fragment2 == null)
                    fragment2 = new AddFriends();
                fragment = fragment2;
                break;
            case R.id.add_friend:

                if(fragment3==null)
                    fragment3 = new CheckIn();
                fragment = fragment3;
                break;
//                    case R.id.leave_circle:
//                        if(fragment4==null)
//                            fragment4 = new LeaveCircle();
//                        fragment = fragment4;
//                        break;
        }
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.main_container, fragment);
        transaction.commit();
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
            Intent intent = new Intent(MainActivity.this, CheckinActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void addItemsOnSpinner2() {
        final SharedPreferences user_prefs = getSharedPreferences(USER_DETAILS, MODE_PRIVATE);
        spinner2 = (Spinner) findViewById(R.id.spinner2);
        list = new ArrayList<String>();
        keyList = new ArrayList<String>();
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list.clear();
                HashMap<String, String> circles = (HashMap<String, String>) dataSnapshot.getValue();
                for (String key : circles.keySet()) {
                    list.add(circles.get(key));
                    keyList.add(key);
                }

                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getApplicationContext(),
                        android.R.layout.simple_spinner_item, list);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner2.setAdapter(dataAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("error", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        root.child("users").child(user_prefs.getString("phoneNumber", "")).child("my_circles").addValueEventListener(postListener);
    }

    public void addListenerOnButton() {

        spinner2 = (Spinner) findViewById(R.id.spinner2);
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SharedPreferences user_prefs = getApplicationContext().getSharedPreferences("user_pref", MODE_PRIVATE);
                SharedPreferences.Editor editor = user_prefs.edit();
                editor.putString("currentGroup", keyList.get(i));
                editor.apply();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            Fragment fragment = new ProfileFragment();
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.main_container, fragment);
            transaction.commit();
        } else if (id == R.id.nav_new_circle) {
            Fragment fragment = new CreateGroup();
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.main_container, fragment);
            transaction.commit();

        } else if (id == R.id.nav_settings) {
            Fragment fragment = new SettingsFragment();
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.main_container, fragment);
            transaction.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
