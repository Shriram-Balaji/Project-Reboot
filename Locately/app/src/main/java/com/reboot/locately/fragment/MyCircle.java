package com.reboot.locately.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.reboot.locately.CircleViewHolder;
import com.reboot.locately.R;
import com.reboot.locately.common.MyCircleAdapter;
import com.reboot.locately.common.Users;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.LOCATION_SERVICE;

public class MyCircle extends Fragment implements OnMapReadyCallback{

    List<String> members = new ArrayList<>();
    private static final int ACCESS_LOCATION = 10;
    public  static final String TAG = "MyCircleFragment";
    private static final LatLng HYDERABAD = new LatLng(17.3850, 78.4867);
    private static final LatLng COCHIN = new LatLng(9.9312, 76.2673);
    private static final LatLng CHENNAI = new LatLng(13.0827, 80.2707);
    private static final LatLng BHUBHANESWAR = new LatLng(20.2961, 85.8245);
    private static final LatLng BENGALURU = new LatLng(12.9716, 77.5946);
    private static final LatLng MADURAI = new LatLng(9.9252, 78.1198);
    private static final LatLng INDIA = new LatLng(20.5937, 78.9629);
    private BottomSheetBehavior mBottomSheetBehavior;
    private GoogleMap mMap;
    MapView mMapView;
    private CoordinatorLayout coordinatorLayout;
    private FragmentManager fragmentManager;
    private GoogleMap mGoogleMap;
    private Marker mHYDERABAD,mCHENNAI;
    private FirebaseRecyclerAdapter<Users, CircleViewHolder> mAdapter;
    private LinearLayoutManager mLayoutManager;
    private com.google.firebase.database.Query queryRef;
    private DatabaseReference userRef;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference usersRef= FirebaseDatabase.getInstance().getReference("users");

    View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        //change to circles later

        final ArrayList<String> FriendsList = new ArrayList<>();
        final DatabaseReference ref = database.getReference("circles").child("-KgcdFYHdWyA_Fp3TNnV").child("members");
        userRef = database.getReference("users");


        view = inflater.inflate(R.layout.fragment_my_circle, container, false);
        View mBottomSheet = view.findViewById(R.id.bottom_sheet);



        coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.coordinatorLayout);
        mBottomSheetBehavior = BottomSheetBehavior.from(mBottomSheet);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);


//        RecyclerView mCircleRecyclerView = (RecyclerView) view.findViewById(R.id.circle_rv);
//        mLayoutManager = new LinearLayoutManager(getActivity());
//        mCircleRecyclerView.setHasFixedSize(true);
//        mCircleRecyclerView.setLayoutManager(mLayoutManager);





        mMapView = (MapView) view.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();// needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(this);
        // latitude and longitude
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String,String> temp=(HashMap<String, String>) dataSnapshot.getValue();
                for(String key:temp.keySet()){
                    members.add(key);
                }
                Log.d("TAG", String.valueOf(members.size()));
                RecyclerView rvContacts = (RecyclerView) view.findViewById(R.id.circle_rv);
                MyCircleAdapter contactAdapter = new MyCircleAdapter(members, getContext());
                rvContacts.setLayoutManager(new LinearLayoutManager(getContext()));
                rvContacts.setAdapter(contactAdapter);
                addTrustedCircleMarkers();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        ref.addListenerForSingleValueEvent(postListener);


        return view;
    }


       @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
//        LatLng chennai = new LatLng(13.0637539, 80.2505319);
//        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(chennai, 10));



        // create marker
        addTrustedCircleMarkers();
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(INDIA).zoom(5).build();
        mGoogleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));
        enableMyLocation();

    }


    private void addTrustedCircleMarkers() {


        Log.d("TAG2", String.valueOf(members.size()));
        for(String cur:members){
            String contact = cur.replace(" ","");
            Log.d("TAG",contact);

            DatabaseReference temp=usersRef.push();
            usersRef.child(temp.getKey()).setValue("test");
            usersRef.child(contact).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d("TAG", String.valueOf(dataSnapshot.getValue()));
                    Double latitude= Double.parseDouble( String.valueOf(dataSnapshot.child("latitude").getValue()));
                    Log.d("TAG",String.valueOf(latitude) + "TESTING ");
                    Double longitude=Double.parseDouble(String.valueOf(dataSnapshot.child("longitude").getValue()));
                    String name=(String) dataSnapshot.child("first_name").getValue();
                    mGoogleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(latitude,longitude))
                            .title(name)
                            .icon(vectorToBitmap(R.drawable.ic_location_on_black_24dp, Color.parseColor("#0087FF")))
                            .snippet(name));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting Post failed, log a message
                    Log.w("error", "loadPost:onCancelled", databaseError.toException());
                    // ...
                }
            });
        }


//            // Uses a custom icon
//            // Uses a custom icon with the info window popping out of the center of the icon.
//           mMap.addMarker(new MarkerOptions()
//                    .position(CHENNAI)
//                    .title("CHENNAI")
//                    .snippet("Shriram Balaji")
//                   .icon(vectorToBitmap(R.drawable.ic_location_on_black_24dp, Color.parseColor("#AA00FF")))
//                   .infoWindowAnchor(0.5f, 0.5f));
//
//            // Creates a draggable marker. Long press to drag.
//             mMap.addMarker(new MarkerOptions()
//                    .position(COCHIN)
//                    .title("COCHIN")
//                     .icon(vectorToBitmap(R.drawable.ic_location_on_black_24dp, Color.parseColor("#FFCC80")))
//                     .snippet("Aravinth")
//                    .draggable(true));
//
//            // A few more markers for good measure.
//             mMap.addMarker(new MarkerOptions()
//                    .position(BENGALURU)
//                    .title("BENGALURU")
//                     .icon(vectorToBitmap(R.drawable.ic_location_on_black_24dp, Color.parseColor("#00C853")))
//                     .snippet("Aishwarya"));
//
//             mMap.addMarker(new MarkerOptions()
//                    .position(BHUBHANESWAR)
//                    .title("BHUBHANESWAR")
//                     .icon(vectorToBitmap(R.drawable.ic_location_on_black_24dp, Color.parseColor("#FF1744")))
//                     .snippet("Herald"));
//
//            // Vector drawable resource as a marker icon.
//            mMap.addMarker(new MarkerOptions()
//                    .position(MADURAI)
//                    .icon(vectorToBitmap(R.drawable.ic_location_on_black_24dp, Color.parseColor("#00BFA5")))
//                    .title("Dillan"));
//
//            // Creates a marker rainbow demonstrating how to create default marker icons of different
//            // hues (colors).
//
            }
        // Changing marker icon

    private BitmapDescriptor vectorToBitmap(@DrawableRes int id, @ColorInt int color) {
        Drawable vectorDrawable = ResourcesCompat.getDrawable(getActivity().getResources(), id, null);
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        DrawableCompat.setTint(vectorDrawable, color);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }


    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            //Get Permissions if not provided
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this.getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this.getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_LOCATION
                );

                // ACCESS_LOCATION is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }


        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            // Access to the location has been granted to the app.

            mMap.setMyLocationEnabled(true);

            UiSettings uiSettings = mMap.getUiSettings();
            uiSettings.setMyLocationButtonEnabled(true);
            Log.d(TAG,"Map not null");


            LocationManager locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

            // Creating a criteria object to retrieve provider
            Criteria criteria = new Criteria();

            // Getting the name of the best provider
            String provider = locationManager.getBestProvider(criteria, true);
            // Getting Current Location
            Location location = locationManager.getLastKnownLocation(provider);
            if (location != null) {
                LatLng current = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.addMarker(new MarkerOptions().position(current).title("Current Location")).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_on_deep_purple_a700_24dp));
            }
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case ACCESS_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Enable Location
                    enableMyLocation();
                } else {
                    Toast.makeText(getContext(), "Location Permission Required", Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }


            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);

                // other 'case' lines to check for other
                // permissions this app might request
        }
    }

    private void checkNetwork() {
        LocationManager lm = (LocationManager) this.getActivity().getSystemService(LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;
        final Context context = getContext();
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled && !network_enabled) {
            // notify user
            Snackbar
                    .make(coordinatorLayout, R.string.location_not_enabled, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.open_location_settings, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            context.startActivity(myIntent);

                        }
                    })
                    .show();
        }
    }

}