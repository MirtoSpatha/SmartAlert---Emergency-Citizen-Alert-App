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
import java.util.ArrayList;
import java.util.HashMap;

public class UserHomePage extends AppCompatActivity implements LocationListener {
    static MyTts tts;
    String fullname, authId, language;
    private TextView textView2;
    private int ACCESS_FINE_LOCATION_CODE = 1;
    static LocationManager locationManager;
    static String userLocation;
    private FirebaseDatabase database;
    private DatabaseReference reference, reference2;
    private SharedPreferences preferences;
    private int no_alert =0;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_home_page);
        fullname = getIntent().getStringExtra("fullname");
        authId = getIntent().getStringExtra("authId");
        language = this.getSharedPreferences("Settings", MODE_PRIVATE).getString("Language","");
        Authentication.setLocale(UserHomePage.this, language);
        tts = new MyTts(this,language);
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Alerts");
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 120000, 0, this);
        } else {
            requestLocationPermission();
        }
        preferences = getPreferences(MODE_PRIVATE);
        try {
            userLocation = preferences.getString("location", "0,0");
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
        ongoing_alerts();
        textView2 = findViewById(R.id.textView2);
        textView2.setText(getString(R.string.welcome) +" "+ fullname + getString(R.string.user_homepage_intro));
    }

    public void add_emergency(View view) {
        Intent intent = new Intent(UserHomePage.this, AddEmergency.class);
        intent.putExtra("fullname", fullname);
        intent.putExtra("authId", authId);
        startActivity(intent);
    }

    public void alerts(View view){
        ArrayList<String> address = new ArrayList<>();
        ArrayList<String> category = new ArrayList<>();
        ArrayList<String> time = new ArrayList<>();
        no_alert = 0;
        Intent intent = new Intent(UserHomePage.this, Alert.class);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    String loc = "";
                    String tim = "";
                    tim = data.child("Time").getValue().toString();
                    loc = data.child("Location").getValue().toString();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss");
                    LocalDateTime date = LocalDateTime.parse(tim, formatter);
                    Duration duration = Duration.between(date, LocalDateTime.now());
                    if (duration.toHours() < 24) {

                        String[] temp = loc.split(",");
                        double lat = Double.parseDouble(temp[0]);
                        double lon = Double.parseDouble(temp[1]);
                        String[] location = userLocation.split(",");
                        double ulat = Double.parseDouble(location[0]);
                        double ulon = Double.parseDouble(location[1]);
                        double distance = Distance.calculateDistance2(lat, lon, ulat, ulon);
                        if (distance <= 10){
                            address.add(data.child("Address").getValue().toString());
                            category.add(data.child("Category").getValue().toString());
                            time.add(data.child("Time").getValue().toString());
                            no_alert+=1;
                        }
                    }
                    else{
                        no_alert += 0;
                    }
                }
                if(no_alert < 1){
                    intent.putStringArrayListExtra("AddressList", new ArrayList<>());
                    intent.putStringArrayListExtra("CategoryList", new ArrayList<>());
                    intent.putStringArrayListExtra("TimeList", new ArrayList<>());
                }
                else{
                    System.out.println("ok");
                    intent.putStringArrayListExtra("AddressList", address);
                    intent.putStringArrayListExtra("CategoryList", category);
                    intent.putStringArrayListExtra("TimeList", time);
                }
                intent.putExtra("fullname", fullname);
                intent.putExtra("authId", authId);
                startActivity(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void ongoing_alerts() {
        System.out.println(userLocation);
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                HashMap<String, String> alert = new HashMap<>();
                alert.put("alertKey", snapshot.getKey());
                for (DataSnapshot data : snapshot.getChildren()) {
                    alert.put(data.getKey().toString(), data.getValue().toString());
                }
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss");
                LocalDateTime date = LocalDateTime.parse(alert.get("Time"), formatter);
                Duration duration = Duration.between(date, LocalDateTime.now());
                if (duration.toHours() < 24) {
                    String[] temp = alert.get("Location").split(",");
                    double lat = Double.parseDouble(temp[0]);
                    double lon = Double.parseDouble(temp[1]);
                    String[] location = userLocation.split(",");
                    double ulat = Double.parseDouble(location[0]);
                    double ulon = Double.parseDouble(location[1]);
                    double distance = Distance.calculateDistance2(lat, lon, ulat, ulon);
                    if (distance <= 10) {
                        Intent intent = new Intent(UserHomePage.this, Alert.class);
                        intent.putExtra("fullname", fullname);
                        intent.putExtra("authId", authId);
                        ArrayList<String> address_list = new ArrayList<>();
                        address_list.add(alert.get("Address"));
                        ArrayList<String> category_list = new ArrayList<>();
                        category_list.add(alert.get("Category"));
                        ArrayList<String> time_list = new ArrayList<>();
                        time_list.add(alert.get("Time"));
                        intent.putStringArrayListExtra("AddressList", address_list);
                        intent.putStringArrayListExtra("CategoryList", category_list);
                        intent.putStringArrayListExtra("TimeList", time_list);
                        startActivity(intent);

                        reference2 = database.getReference("Users/" + authId + "/statistics/" + alert.get("alertKey"));
                        reference2.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.getValue() == null) {
                                    reference2.child("Address").setValue(alert.get("Address"));
                                    reference2.child("Category").setValue(alert.get("Category"));
                                    reference2.child("Time").setValue(alert.get("Time"));
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
    }
}