package com.example.placearchiverapp;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import java.util.ArrayList;
import java.util.List;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
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
    private MapView mapView;
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
    LatLng userLocation;
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

        // Gets the MapView from the XML layout and creates it
        mapView = (MapView) findViewById(R.id.mapa);
        mapView.onCreate(savedInstanceState);


        mapView.getMapAsync(this);
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
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                LinearLayout info = new LinearLayout(MapsActivity.this);
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(MapsActivity.this);
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(MapsActivity.this);
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });
userMarker=mMap.addMarker(new MarkerOptions().position(new LatLng(0,0)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).title("Tu jesteś"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(53.9357324,18.8865807), 6));

        LocationListener mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                //until there is not hundrets of places it won't be filted
                    findNearPlaces(new LatLng(location.getLatitude(), location.getLongitude()));
                userMarker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
                userLocation=new LatLng(location.getLatitude(), location.getLongitude());
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
            //gets all places
            //until there is not hundrets of places it won't be filted
            places = FirebaseDatabase.getInstance().getReference("places");
             places.addListenerForSingleValueEvent(valueEventListener);



            //get places in range
            /*Query query1 = FirebaseDatabase.getInstance().getReference("places")
               .orderByChild("Longitude").startAt(userLocation.latitude-0.03).endAt(userLocation.latitude+0.03);
            query1.addListenerForSingleValueEvent(valueEventListener);
                  //Toast.makeText(MapsActivity.this,"eee"+userLocation.longitude,Toast.LENGTH_LONG).show();*/

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
                    mMap.addMarker(new MarkerOptions().position(placeLatLng).title(place.LocName).snippet(place.Description));
                }

            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
        Log.i("logi",databaseError.getMessage());
        }
    };
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }
    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
