package com.reboot.locately.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.reboot.locately.R;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CheckinActivity extends AppCompatActivity {
    private static final String USER_DETAILS = "user_pref";
    private static final int PLACE_PICKER_REQUEST = 1;
    private static LatLngBounds BOUNDS_MOUNTAIN_VIEW;
    FirebaseDatabase database;
    TextView mname;
    DatabaseReference ref;
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkin);
        final SharedPreferences user_prefs = getSharedPreferences(USER_DETAILS, MODE_PRIVATE);
        //mname = (TextView) findViewById()
//        promptUser();
//        boolean is_gps_enabled = checkGPS();
//        if(is_gps_enabled) {
//        }
//        else {
//            LocationFetcher loc = new LocationFetcher(this);
//            loc.fetchCellLocation();
//            ref.child(phoneNumber).child()
//        }
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        Intent intent;
        try {
            intent = builder.build(this);
            startActivityForResult(intent, 5);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }


    }

    protected void onActivityResult(int requestcode, int resultcode, Intent data) {
        Log.d("requestCode", String.valueOf(resultcode));
        Log.d("PLACEPI", String.valueOf(PLACE_PICKER_REQUEST));
        Log.d("resultCode", String.valueOf(resultcode));
        Log.d("RESULT_OK", String.valueOf(RESULT_OK));


        Log.d("resultCode2", String.valueOf(resultcode));
        Log.d("RESULT_OK2", String.valueOf(RESULT_OK));

        Log.d("RESULT_OK3", String.valueOf(RESULT_OK));

        Place place = PlacePicker.getPlace(data, this);
        LatLng l = PlacePicker.getLatLngBounds(data).getCenter();
        Log.d("latlg", String.valueOf(l.longitude));
        Log.d("latlg", String.valueOf(l.latitude));

        String address = String.format("Places %s", place.getAddress());
        Log.d("address", address);
        //mname.setText(address);
        final SharedPreferences user_prefs = getSharedPreferences(USER_DETAILS, MODE_PRIVATE);
        database = FirebaseDatabase.getInstance();
        ref = database.getReference("users");
        String lat = String.valueOf(l.longitude);
        String lon = String.valueOf(l.latitude);
        String phoneNumber = user_prefs.getString("phoneNumber", "");
        Log.d("phoneNumber", String.valueOf(phoneNumber));

        Map map = new HashMap();
        map.put("latitude", lat);
        map.put("longitude", lon);
        map.put("check_in_time", new Date().getTime());

        Log.d("latitudemap", String.valueOf(map.get("latitude")));
        DatabaseReference r = ref.child(phoneNumber).child("check_in").push();
        Log.d("rkey", String.valueOf(r.getKey()));

        ref.child(phoneNumber).child("check_in").child(r.getKey()).child("Latitude").setValue(lat);
        ref.child(phoneNumber).child("check_in").child(r.getKey()).child("Longitude").setValue(lon);
        ref.child(phoneNumber).child("check_in").child(r.getKey()).child("check_in_time").setValue(new Date().getTime());
        if (place.getName().charAt(2) == '°') {
            ref.child(phoneNumber).child("check_in").child(r.getKey()).child("address").setValue(place.getAddress());
        } else {
            ref.child(phoneNumber).child("check_in").child(r.getKey()).child("address").setValue(place.getName() + ", " + place.getAddress());

        }
        Toast.makeText(getApplicationContext(), "Checked-in success", Toast.LENGTH_LONG).show();
        this.finish();
    }


}
