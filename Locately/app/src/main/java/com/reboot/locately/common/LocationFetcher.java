package com.reboot.locately.common;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.location.LocationManager.NETWORK_PROVIDER;

/**
 * Created by Naveen on 3/29/2017.
 */

public class LocationFetcher extends Application {
    private static final String USER_DETAILS = "user_pref";

    Context context;
    Double lat, lon = 0d;
    FirebaseDatabase database;
    DatabaseReference ref;
    Map<String, Object> map = new HashMap<>();


    public LocationFetcher(Context context) {
        this.context = context;
    }

    public void fetchCellLocation() {
        promptUser();
        boolean is_gps_available = checkGPS();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference("users");
        final SharedPreferences user_prefs = context.getSharedPreferences(USER_DETAILS, MODE_PRIVATE);
        if (is_gps_available == true) {
            LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            LocationListener locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    String phoneNumber = user_prefs.getString("phoneNumber", "");
                    ref.child(phoneNumber).child("latitude").setValue(location.getLatitude());
                    ref.child(phoneNumber).child("longitude").setValue(location.getLongitude());
                    ref.child(phoneNumber).child("lastLocationUpdate").setValue(new Date().getTime());
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };
            String phoneNumber = user_prefs.getString("phoneNumber", "");
            locationManager.requestLocationUpdates(NETWORK_PROVIDER, 0, 0, locationListener);
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            ref.child(phoneNumber).child("latitude").setValue(location.getLatitude());
            ref.child(phoneNumber).child("longitude").setValue(location.getLongitude());
            ref.child(phoneNumber).child("lastLocationUpdate").setValue(new Date().getTime());

        } else {


            TelephonyManager tm = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
            Log.d("nettype", String.valueOf(tm.getNetworkType()));
            Log.d("pgsm", String.valueOf(TelephonyManager.PHONE_TYPE_GSM));

            if (tm.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM) {
                GsmCellLocation loc = (GsmCellLocation) tm.getCellLocation();
                if (loc != null) {
                    int cellId = loc.getCid();
                    int lac = loc.getLac();
                    String operatorCode = tm.getNetworkOperator();
                    Log.d("cid", String.valueOf(cellId));
                    Log.d("lac", String.valueOf(lac));
                    Log.d("opcode", operatorCode);
                    String opcode = String.valueOf(operatorCode);
                    final String operatorName = tm.getNetworkOperatorName();
                    int mcc = Integer.parseInt(opcode.substring(0, 3));
                    int mnc = Integer.parseInt(opcode.substring(3));

        /* volley json GET request to opencellid.org */
                    RequestQueue queue = Volley.newRequestQueue(context);
                    final String url = "https://opencellid.org/cell/get?key=671ff9d8-2532-4319-a863-ac7da7520cfd&mcc=" + mcc + "&mnc=" + mnc + "&lac=" + lac + "&cellid=" + cellId + "&format=json";
                    Log.d("URL", String.valueOf(url));

                    JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        if (response.getString("lat") != null) {
                                            lat = response.getDouble("lat");
                                            lon = response.getDouble("lon");
                                            Log.d("GSMLatitude", String.valueOf(lat));
                                            Log.d("GSMLongitude", String.valueOf(lon));
                                            map.put("operator_name", operatorName);
                                            map.put("latitude", lat);
                                            map.put("longitude", lon);
                                            String phoneNumber = user_prefs.getString("phoneNumber", "");
                                            ref.child(phoneNumber).child("latitude").setValue(lat);
                                            ref.child(phoneNumber).child("longitude").setValue(lat);
                                            ref.child(phoneNumber).child("lastLocationUpdate").setValue(new Date().getTime());
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    Log.d("Response", response.toString());
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.d("Error.Response", error.getMessage());
                                }
                            }
                    );
                    queue.add(getRequest);
                }

            }
        }

    }

    private boolean checkGPS() {
        LocationManager lm = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        boolean gps_enabled = false;
        Log.d("beGPS", String.valueOf(gps_enabled));
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }
        Log.d("GPS", String.valueOf(gps_enabled));
        return gps_enabled;


    }

    private void promptUser() {
        LocationManager lm = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        boolean gps_enabled = false;
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }
        if (!gps_enabled) {
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(context);
            builder.setMessage("Change Location Setting to High accuracy")
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface d, int id) {
                                    context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                    d.dismiss();
                                }
                            })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface d, int id) {
                                    d.cancel();
                                }
                            });
            builder.create().show();
        }
    }
}

