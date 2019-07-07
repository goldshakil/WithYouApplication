package edu.skku.map.withyou_project;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;

public class Arduino extends AppCompatActivity {

    final String ON = "1";
    final String OFF = "0";

    private DatabaseReference mPostReference;
    BluetoothSPP bluetooth;

    Button connect;
    Button off;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arduino);
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);

//move to next activity on cl
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                Intent getIntent;
                switch (item.getItemId()){


                    case R.id.arduino:
                        break;

                    case R.id.noticeboard: //move to board list

                        intent = new Intent(Arduino.this, board_list.class);
                        getIntent = getIntent();
                        intent.putExtra("myName",getIntent.getStringExtra("myName"));
                        intent.putExtra("myGmail",getIntent.getStringExtra("myGmail"));
                        intent.putExtra("ReOrVol",getIntent.getStringExtra("ReOrVol"));
                        startActivity(intent);                        break;


                    case R.id.donation: //move to donation
                        intent = new Intent(Arduino.this, donate.class);
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

        bluetooth = new BluetoothSPP(this);

        connect = (Button) findViewById(R.id.connect);
        off = (Button) findViewById(R.id.off);

        if (!bluetooth.isBluetoothAvailable()) {
            Toast.makeText(getApplicationContext(), "Bluetooth is not available", Toast.LENGTH_SHORT).show();
            finish();
        }
        //accesssiing initialization
        mPostReference = FirebaseDatabase.getInstance().getReference();//Notify node is usedt to control the arduino


        bluetooth.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() {
            public void onDeviceConnected(String name, String address) {
                connect.setText("Connected to " + name);
            }

            public void onDeviceDisconnected() {
                connect.setText("Connection lost");
            }

            public void onDeviceConnectionFailed() {
                connect.setText("Unable to connect");
            }
        });



        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bluetooth.getServiceState() == BluetoothState.STATE_CONNECTED) {
                    bluetooth.disconnect();
                } else {
                    Intent intent = new Intent(getApplicationContext(), DeviceList.class);
                    startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
                }
            }
        });

        getFirebaseDatabase();//check the fire base



        off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bluetooth.send(OFF, true);
            }
        });

    }

    public void onStart() {
        super.onStart();
        if (!bluetooth.isBluetoothEnabled()) {
            bluetooth.enable();
        } else {
            if (!bluetooth.isServiceAvailable()) {
                bluetooth.setupService();
                bluetooth.startService(BluetoothState.DEVICE_OTHER);
            }
        }
    }


    public void onDestroy() {
        super.onDestroy();
        bluetooth.stopService();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if (resultCode == Activity.RESULT_OK)
                bluetooth.connect(data);
        } else if (requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                bluetooth.setupService();
            } else {
                Toast.makeText(getApplicationContext()
                        , "Bluetooth was not enabled."
                        , Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
    public void getFirebaseDatabase() {
        final ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (bluetooth.getServiceState() == BluetoothState.STATE_CONNECTED) {
                    Log.w("HELLLLLO", "HELLO");

                    bluetooth.send(ON, true);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };

        mPostReference.child("Notify").addValueEventListener(postListener);

    }
}