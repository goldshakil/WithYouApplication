package edu.skku.map.withyou_project;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
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

import java.util.HashMap;
import java.util.Map;

public class NoticeBoardRefugee extends AppCompatActivity implements OnMapReadyCallback, TaskLoadedCallback {


    private DatabaseReference mPostReference;
    double longitude;
    double latitude;
    String name=null;
    private GoogleMap gmap;
    private MarkerOptions place1, place2,place3;
    private Polyline currentPolyline;
    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = gmap.addPolyline((PolylineOptions) values[0]);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent getIntent;
        getIntent = getIntent();
        name=getIntent.getStringExtra("myName");
        setContentView(R.layout.activity_notice_board_refugee);

        FragmentManager fragmentManager = getFragmentManager();
        MapFragment mapFragment = (MapFragment)fragmentManager
                .findFragmentById(R.id.mapNearBy);
        mapFragment.getMapAsync(this);
        final LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        mPostReference = FirebaseDatabase.getInstance().getReference();

        Button btn = (Button)findViewById(R.id.btnHelp);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if ( Build.VERSION.SDK_INT >= 23 &&
                        ContextCompat.checkSelfPermission( getApplicationContext(),
                                android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
                    ActivityCompat.requestPermissions( NoticeBoardRefugee.this, new String[]
                            { android.Manifest.permission.ACCESS_FINE_LOCATION },0 );
                }
                else{
                    //lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,100,0, gpsLocationListener);
                    lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,100, 0, networkLocationListener);
                }
                getFirebaseDatabase();
            }
        });

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
                        intent = new Intent(NoticeBoardRefugee.this, Arduino.class);
                        getIntent = getIntent();
                        intent.putExtra("myName",getIntent.getStringExtra("myName"));
                        intent.putExtra("myGmail",getIntent.getStringExtra("myGmail"));
                        intent.putExtra("ReOrVol",getIntent.getStringExtra("ReOrVol"));
                        startActivity(intent);
                        break;

                    case R.id.noticeboard:
                        break;

                    case R.id.donation:
                        intent = new Intent(NoticeBoardRefugee.this, donate.class);
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
    public void postFirebaseDatabase(boolean add){
        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> postValues = null;
        String ReOrVol = "Refugee";
        if(add){
            locationInfo mem = new locationInfo(ReOrVol,latitude,longitude,name);
            postValues = mem.toMap();
        }
        childUpdates.put("/location/" + name, postValues);
        mPostReference.updateChildren(childUpdates);
    }
    final LocationListener networkLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            String provider = location.getProvider();
            longitude = location.getLongitude();
            latitude = location.getLatitude();

            latitude -= 9.6377302;
            longitude -= 41.6558811;
            double altitude = location.getAltitude();
            LatLng my_loc = new LatLng(latitude, longitude);
            gmap.moveCamera(CameraUpdateFactory.newLatLng(my_loc));
            gmap.animateCamera(CameraUpdateFactory.zoomTo(10));

            postFirebaseDatabase(true);
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
        @Override
        public void onProviderEnabled(String provider) {}
        @Override
        public void onProviderDisabled(String provider) {}
    };
    @Override
    public void onMapReady(final GoogleMap map) {
        this.gmap=map;
        LatLng SEOUL = new LatLng(37.56, 126.97);
        MarkerOptions markerOptions = new MarkerOptions();
        map.moveCamera(CameraUpdateFactory.newLatLng(SEOUL));
        map.animateCamera(CameraUpdateFactory.zoomTo(10));
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


                    for (DataSnapshot postSnapshot2 : dataSnapshot.getChildren()) {
                        String key2 = postSnapshot.getKey();
                        locationInfo get2 = postSnapshot.getValue(locationInfo.class);
                        if(get2.ReOrVol.equals("Refugee"))
                        {
                            latitude = get2.latitude;
                            longitude = get2.longitude;
                        }
                    }

                    if(info[1].equals("Volunteer"))
                    {


                        place1 = new MarkerOptions().position(new LatLng(info_d[0],info_d[1])).title("Volunteer location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                        place2 = new MarkerOptions().position(new LatLng(latitude,longitude)).title("MyLocation").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                        place3 = new MarkerOptions().position(new LatLng(latitude-0.007348,longitude-0.007348)).title("FIRE").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

                        gmap.addMarker(place1);
                        gmap.addMarker(place2);
                        gmap.addMarker(place3);

                        LatLng my_loc = new LatLng(latitude, longitude);
                        gmap.moveCamera(CameraUpdateFactory.newLatLng(my_loc));
                        gmap.animateCamera(CameraUpdateFactory.zoomTo(14));
                        new FetchURL(NoticeBoardRefugee.this).execute(getUrl(place1.getPosition(), place2.getPosition(), "driving"), "driving");

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
}
