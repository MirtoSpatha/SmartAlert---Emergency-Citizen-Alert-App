package com.john.smartalert;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class UserHomePage extends AppCompatActivity implements LocationListener {
    String fullname, authId, language;
    TextView textView2;
    private int ACCESS_FINE_LOCATION_CODE = 1;
    static LocationManager locationManager;
    static String userLocation;
    FirebaseDatabase database;
    DatabaseReference reference;
    SharedPreferences preferences;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_home_page);
        fullname = getIntent().getStringExtra("fullname");
        authId = getIntent().getStringExtra("authId");
        language = this.getSharedPreferences("Settings", MODE_PRIVATE).getString("Language","");
        Authentication.setLocale(UserHomePage.this, language);
        //recreate();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Alerts");
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 120000, 0, this);
            //userLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude()+","+locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude();
        } else {
            requestLocationPermission();
        }
        preferences = getPreferences(MODE_PRIVATE);
        try {
            userLocation = preferences.getString("location", "0,0");
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
        ongoing_alerts(null);
        textView2 = findViewById(R.id.textView2);
        textView2.setText(getString(R.string.welcome) + fullname + getString(R.string.user_homepage_intro));
    }

    public void add_emergency(View view) {
        Intent intent = new Intent(UserHomePage.this, AddEmergency.class);
        intent.putExtra("fullname", fullname);
        intent.putExtra("authId", authId);
        startActivity(intent);
    }

    public void ongoing_alerts(View view) {
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                HashMap<String, String> alert = new HashMap<>();
                alert.put("alertKey", snapshot.getKey());
                for (DataSnapshot data : snapshot.getChildren()) {
                    alert.put(data.getKey().toString(), data.getValue().toString());
                }
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss");
                LocalDateTime date = LocalDateTime.parse(alert.get("time"), formatter);
                ;
                LocalDateTime now = LocalDateTime.now();
                Duration duration = Duration.between(date, LocalDateTime.now());
                if (duration.toHours() < 24) {
                    String[] temp = alert.get("location").split(",");
                    double lat = Double.parseDouble(temp[0]);
                    double lon = Double.parseDouble(temp[1]);
                    String[] location = userLocation.split(",");
                    double ulat = Double.parseDouble(location[0]);
                    double ulon = Double.parseDouble(location[1]);
                    double distance = Distance.calculateDistance2(lat, lon, ulat, ulon);
                    if (distance <= 10) {
                        Intent intent = new Intent(UserHomePage.this, Alert.class);
                        intent.putExtra("address", alert.get(" address"));
                        intent.putExtra("category", alert.get("category"));
                        intent.putExtra("time", alert.get("time"));
                        startActivity(intent);

                        reference = database.getReference("Users/" + authId + "/statistics/" + alert.get("alertKey"));
                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                System.out.println(snapshot.getValue());
                                if (snapshot.getValue() == null) {
                                    reference.child("address").setValue(alert.get(" address"));//edo bazo tin odo kai oxi to location
                                    reference.child("category").setValue(alert.get("category"));
                                    reference.child("time").setValue(alert.get("time"));
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void statistics(View view) {
        Intent intent = new Intent(UserHomePage.this, Statistics.class);
        intent.putExtra("fullname", fullname);
        intent.putExtra("authId", authId);
        startActivity(intent);
    }

    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)) {

            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.permission_needed))
                    .setMessage(getString(R.string.permission_location))
                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(UserHomePage.this,
                                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_CODE);
                        }
                    })
                    .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
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
                Toast.makeText(this, getString(R.string.location_granted), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.location_denied), Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onLocationChanged(@NonNull Location location) {
        userLocation = location.getLatitude() + "," + location.getLongitude();
        preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("location", userLocation);
        editor.apply();
//        System.out.println(userLocation);
        /*database =FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Users");
        reference.child(authId).child("Location").setValue(userLocation);*/
        //locationManager.removeUpdates(this);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
    }

}