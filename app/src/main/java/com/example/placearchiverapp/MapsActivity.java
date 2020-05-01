package com.example.placearchiverapp;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    //Get location permisssion ID
    int PERMISSION_ID = 44;
    private FirebaseAuth fba;
    private DatabaseReference places;
    //list of places near user location
    private List<PlaceModel> placeList = new ArrayList<>();
    private LocationManager mLocationManager;
    //userLocationMarker
    MarkerOptions userMarkerOpts;
    Marker userMarker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        fba = FirebaseAuth.getInstance();
        if (fba.getCurrentUser() == null) {
            startActivity(new Intent(getApplicationContext(), Login.class));
            finish();
        }
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_ID
            );
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
userMarker=mMap.addMarker(new MarkerOptions().position(new LatLng(0,0)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).title("Tu jesteś"));

        LocationListener mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
               // mMap.clear();
                userMarker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15));
                    findNearPlaces(new LatLng(location.getLatitude(), location.getLongitude()));

            }
            //unused but required methods
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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1,
                1, mLocationListener);

    }
    //Logout from the application
    public void logout(View view){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(),Login.class));
    }
    //catch data snapshot and fill markers
    private void findNearPlaces(LatLng userLoc){

        //db conection
        try {
            places = FirebaseDatabase.getInstance().getReference("places");
            places.addListenerForSingleValueEvent(valueEventListener);

            // Query query3 = FirebaseDatabase.getInstance().getReference("places")
            //   .orderByChild("ImageName")
            //    .equalTo("wzr1.jpg");
            //query3.addListenerForSingleValueEvent(valueEventListener);
            //      Toast.makeText(MapsActivity.this,"eee"+query3.toString(),Toast.LENGTH_LONG).show();

        } catch (Exception ex) {
            Toast.makeText(MapsActivity.this, "e" + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            placeList.clear();
            if (dataSnapshot.exists()) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.i("logi",dataSnapshot.toString());
                    PlaceModel place = snapshot.getValue(PlaceModel.class);
                    placeList.add(place);
                    LatLng placeLatLng=new LatLng(place.Longitude,place.Latitude);
                    mMap.addMarker(new MarkerOptions().position(placeLatLng).title(place.LocName));
                }

            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
        Log.i("logi",databaseError.getMessage());
        }
    };

}
