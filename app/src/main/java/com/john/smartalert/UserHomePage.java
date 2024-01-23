package com.john.smartalert;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class UserHomePage extends AppCompatActivity implements LocationListener{
    String fullname,authId;
    TextView textView2;
    private int ACCESS_FINE_LOCATION_CODE = 1;
    static LocationManager locationManager;
    static String userLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_home_page);
        fullname = getIntent().getStringExtra("fullname");
        authId = getIntent().getStringExtra("authId");
        textView2 = findViewById(R.id.textView2);
        textView2.setText("Welcome "+fullname+"!\n This is an Emergency Alert App.\n" +
                " Here, you can get notified when an emergency is near you, view the ongoing alerts and statistics about previous emergencies near you.\n" +
                "You can also add an emergency event when it happens close to you.");
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0, this);
        } else {
            requestLocationPermission();
        }
    }

    public void add_emergency(View view){
        Intent intent = new Intent(UserHomePage.this, AddEmergency.class);
        intent.putExtra("fullname",fullname.toString());
        intent.putExtra("authId",authId);
        startActivity(intent);
    }

    public void ongoing_alerts(View view){
        Intent intent = new Intent(UserHomePage.this, OngoingAlerts.class);
        intent.putExtra("fullname",fullname.toString());
        intent.putExtra("authId",authId);
        startActivity(intent);
    }

    public void statistics(View view){
        Intent intent = new Intent(UserHomePage.this, Statistics.class);
        intent.putExtra("fullname",fullname.toString());
        intent.putExtra("authId",authId);
        startActivity(intent);
    }

    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)) {

            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed to know the location of emergency.")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(UserHomePage.this,
                                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_CODE);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ACCESS_FINE_LOCATION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Location Permission GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Location Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onLocationChanged(@NonNull Location location) {
        userLocation = location.getLongitude() + "," + location.getLatitude();
        System.out.println(userLocation);
        //locationManager.removeUpdates(this);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) { }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public void onProviderEnabled(@NonNull String provider) { }
}