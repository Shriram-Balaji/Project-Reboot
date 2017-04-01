package com.reboot.locately.common;

import android.app.Application;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Naveen on 3/29/2017.
 */

public class LocationFetcher extends Application {

    Context context;
    Double lat, lon = 0d;
    Map<String, Object> map = new HashMap<>();

    public LocationFetcher(Context context) {
        this.context = context;
    }

    public void fetchCellLocation() {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
        if (tm.getNetworkType() == TelephonyManager.PHONE_TYPE_GSM) {
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
                                    lat = response.getDouble("lat");
                                    lon = response.getDouble("lon");
                                    Log.d("Latitude", String.valueOf(lat));
                                    Log.d("Longitude", String.valueOf(lon));
                                    map.put("operator_name", operatorName);
                                    map.put("latitude", lat);
                                    map.put("longitude", lon);


                                    //TODO


                                    Log.d("map", String.valueOf(map));
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
//
//
//
//        else if(tm.getNetworkType() == TelephonyManager.PHONE_TYPE_CDMA){
//
//            CdmaCellLocation loc = (CdmaCellLocation)tm.getCellLocation();
//            int baseStationId = loc.getBaseStationId();
//            loc.getNetworkId();
//        }
//        CdmaCellLocation loccdma = (CdmaCellLocation) tm.getCellLocation();

    }
}

