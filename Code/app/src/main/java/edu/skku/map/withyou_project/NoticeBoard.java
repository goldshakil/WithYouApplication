package edu.skku.map.withyou_project;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class NoticeBoard extends AppCompatActivity implements OnMapReadyCallback, TaskLoadedCallback{

    private GoogleMap mMap;
    private MarkerOptions place1, place2,place3;
    Button getDirection;
    private Polyline currentPolyline;
    private DatabaseReference mPostReference;
    String name=null;

    double longitude;
    double latitude;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);
        Intent getIntent = getIntent();
        name = getIntent.getStringExtra("myName");
        getDirection = findViewById(R.id.btnGetDirection);
        mPostReference = FirebaseDatabase.getInstance().getReference();
        final LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( getApplicationContext(),
                        android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions( NoticeBoard.this, new String[]
                    { android.Manifest.permission.ACCESS_FINE_LOCATION },0 );
        }
        else{
            //lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,100,0, gpsLocationListener);
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,100, 0, networkLocationListener);
        }
        getDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFirebaseDatabase();
                postFirebaseDatabase(true);

            }
        });
        //place1 = new MarkerOptions().position(new LatLng(27.658143, 85.3199503)).title("Location 1");
        //place2 = new MarkerOptions().position(new LatLng(27.667491, 85.3208583)).title("Location 2");
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.mapNearBy);
        mapFragment.getMapAsync(this);


        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                Intent getIntent;
                switch (item.getItemId()){


                    case R.id.arduino:
                        intent = new Intent(NoticeBoard.this, Arduino.class);
                        getIntent = getIntent();
                        intent.putExtra("myName",getIntent.getStringExtra("myName"));
                        intent.putExtra("myGmail",getIntent.getStringExtra("myGmail"));
                        intent.putExtra("ReOrVol",getIntent.getStringExtra("ReOrVol"));
                        startActivity(intent);
                        break;

                    case R.id.noticeboard:
                        break;

                    case R.id.donation:
                        intent = new Intent(NoticeBoard.this, donate.class);
                        getIntent = getIntent();
                        intent.putExtra("myName",getIntent.getStringExtra("myName"));
                        intent.putExtra("myGmail",getIntent.getStringExtra("myGmail"));
                        intent.putExtra("ReOrVol",getIntent.getStringExtra("ReOrVol"));
                        startActivity(intent);
                        break;


                }


                return false;
            }
        });

    }
    public void getFirebaseDatabase()
    {
        final ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("onDataChange", "Data is Updated");
                //data.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    locationInfo get = postSnapshot.getValue(locationInfo.class);
                    String[] info = {get.name,get.ReOrVol};
                    double[] info_d = {get.latitude,get.longitude};

                    if(info[1].equals("Refugee"))
                    {
                        place1 = new MarkerOptions().position(new LatLng(info_d[0],info_d[1])).title("Refugee location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                        place2 = new MarkerOptions().position(new LatLng(latitude,longitude)).title("MyLocation").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                        place3 = new MarkerOptions().position(new LatLng(info_d[0]-0.007348,info_d[1]-0.007348)).title("FIRE").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

                        LatLng place1_ = new LatLng(latitude,longitude);
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(place1_));
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
                        mMap.addMarker(place1);
                        mMap.addMarker(place2);
                        mMap.addMarker(place3);
                        new FetchURL(NoticeBoard.this).execute(getUrl(place1.getPosition(), place2.getPosition(), "driving"), "driving");

                    }

                    Log.d("getFirebaseDatabase", "key: " + key);
                    Log.d("getFirebaseDatabase", "info: " + info[0] + info[1] + info_d[0] + info_d[1]);
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        mPostReference.child("location").addValueEventListener(postListener);
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Log.d("mylog", "Added Markers");
        //mMap.addMarker(place1);
        //mMap.addMarker(place2);

    }

    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=AIzaSyBfWvRC-fzpvFtYL0npkf0xU9ngqm4YJBU";
        return url;
    }

    public void postFirebaseDatabase(boolean add){
        java.util.Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> postValues = null;
        String ReOrVol = "Volunteer";
        if(add){
            locationInfo mem = new locationInfo(ReOrVol,latitude,longitude,name);
            postValues = mem.toMap();
        }
        childUpdates.put("/location/" + name, postValues);
        mPostReference.updateChildren(childUpdates);
    }

    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
    }
    final LocationListener networkLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            String provider = location.getProvider();
            longitude = location.getLongitude();
            latitude = location.getLatitude();

            latitude -= 9.6377302;
            longitude -= 41.6558811;
            latitude += 0.009348;
            longitude += 0.0013553;
            double altitude = location.getAltitude();
            LatLng my_loc = new LatLng(latitude, longitude);

           Marker new_mkr = mMap.addMarker(new MarkerOptions()
                    .position(my_loc)
                    .title("here")
                    .snippet("MyLocation").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
            //mMap.moveCamera(CameraUpdateFactory.newLatLng(my_loc));
            //mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
        @Override
        public void onProviderEnabled(String provider) {}
        @Override
        public void onProviderDisabled(String provider) {}
    };
}