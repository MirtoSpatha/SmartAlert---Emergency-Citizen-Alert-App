package com.john.smartalert;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

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
import java.util.Objects;

public class Statistics extends AppCompatActivity {
    String fullname, authId, language;
    String allStatistics, fireStatistics, floodStatistics, earthquakeStatistics, thunderstormStatistics, heatwaveStatistics, tornadoStatistics, blizzardStatistics;
    TextView textView3;
    FirebaseDatabase database;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistics);
        fullname = getIntent().getStringExtra("fullname");
        authId = getIntent().getStringExtra("authId");
        language = this.getSharedPreferences("Settings", MODE_PRIVATE).getString("Language","");
        Authentication.setLocale(Statistics.this, language);
        //recreate();
        textView3 = findViewById(R.id.textView3);
        textView3.setText(getString(R.string.welcome)+fullname+getString(R.string.statistics_intro));
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Users").child(authId).child("statistics");
        ongoing_alerts(null);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snap) {
                if (snap.getValue()!=null){
                        for(DataSnapshot emergencies : snap.getChildren()){
                            if(allStatistics== null){
                                allStatistics = Objects.requireNonNull(emergencies.getValue()).toString();
                            }
                            else {
                                allStatistics = allStatistics.concat(Objects.requireNonNull(emergencies.getValue()).toString());
                            }
                            switch (Objects.requireNonNull(emergencies.child("category").getValue()).toString()){
                                case ("Fire"):{
                                    if(fireStatistics== null){
                                        fireStatistics = Objects.requireNonNull(emergencies.child("address").getValue()).toString().concat(emergencies.child("time").getValue().toString());
                                    }
                                    else {
                                        fireStatistics = fireStatistics.concat(Objects.requireNonNull(emergencies.child("address").getValue()).toString()).concat(emergencies.child("time").getValue().toString());
                                    }
                                    break;
                                }
                                case ("Flood"):{
                                    if(floodStatistics== null){
                                        floodStatistics = Objects.requireNonNull(emergencies.child("address").getValue()).toString().concat(emergencies.child("time").getValue().toString());
                                    }
                                    else {
                                        floodStatistics = floodStatistics.concat(Objects.requireNonNull(emergencies.child("address").getValue()).toString()).concat(emergencies.child("time").getValue().toString());
                                    }                                    break;
                                }
                                case ("Earthquake"):{
                                    if(earthquakeStatistics== null){
                                        earthquakeStatistics = Objects.requireNonNull(emergencies.child("address").getValue()).toString().concat(emergencies.child("time").getValue().toString());
                                    }
                                    else {
                                        earthquakeStatistics = earthquakeStatistics.concat(Objects.requireNonNull(emergencies.child("address").getValue()).toString()).concat(emergencies.child("time").getValue().toString());
                                    }                                    break;
                                }
                                case ("Thunderstorm"):{
                                    if(thunderstormStatistics== null){
                                        thunderstormStatistics = Objects.requireNonNull(emergencies.child("address").getValue()).toString().concat(emergencies.child("time").getValue().toString());
                                    }
                                    else {
                                        thunderstormStatistics = thunderstormStatistics.concat(Objects.requireNonNull(emergencies.child("address").getValue()).toString()).concat(emergencies.child("time").getValue().toString());
                                    }
                                    break;
                                }
                                case ("Heatwave"):{
                                    if(heatwaveStatistics== null){
                                        heatwaveStatistics = Objects.requireNonNull(emergencies.child("address").getValue()).toString().concat(emergencies.child("time").getValue().toString());
                                    }
                                    else {
                                        heatwaveStatistics = heatwaveStatistics.concat(Objects.requireNonNull(emergencies.child("address").getValue()).toString()).concat(emergencies.child("time").getValue().toString());
                                    }
                                    break;
                                }
                                case ("Tornado"):{
                                    if(tornadoStatistics== null){
                                        tornadoStatistics = Objects.requireNonNull(emergencies.child("address").getValue()).toString().concat(emergencies.child("time").getValue().toString());
                                    }
                                    else {
                                        tornadoStatistics = tornadoStatistics.concat(Objects.requireNonNull(emergencies.child("address").getValue()).toString()).concat(emergencies.child("time").getValue().toString());
                                    }
                                    break;
                                }
                                case ("Blizzard"):{
                                    if(blizzardStatistics== null){
                                        blizzardStatistics = Objects.requireNonNull(emergencies.child("address").getValue()).toString().concat(emergencies.child("time").getValue().toString());
                                    }
                                    else {
                                        blizzardStatistics = blizzardStatistics.concat(Objects.requireNonNull(emergencies.child("address").getValue()).toString()).concat(emergencies.child("time").getValue().toString());
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void Fire(View view){
        if(fireStatistics == null){
            fireStatistics = getString(R.string.no_alerts);
        }
        showMessage(getString(R.string.fire_alerts), fireStatistics);
    }

    public void Flood(View view){
        if(floodStatistics == null){
            floodStatistics = getString(R.string.no_alerts);
        }
        showMessage(getString(R.string.flood_alerts), floodStatistics);
    }

    public void Earthquake(View view){
        if(earthquakeStatistics == null){
            earthquakeStatistics = getString(R.string.no_alerts);
        }
        showMessage(getString(R.string.earthquake_alerts), earthquakeStatistics);
    }

    public void Heatwave(View view){
        if(heatwaveStatistics == null){
            heatwaveStatistics = getString(R.string.no_alerts);
        }
        showMessage(getString(R.string.heatwave_alerts), heatwaveStatistics);
    }

    public void Blizzard(View view){
        if(blizzardStatistics == null){
            blizzardStatistics = getString(R.string.no_alerts);
        }
        showMessage(getString(R.string.blizzard_alerts), blizzardStatistics);
    }

    public void Thunderstorm(View view){
        if(thunderstormStatistics == null){
            thunderstormStatistics = getString(R.string.no_alerts);
        }
        showMessage(getString(R.string.thunderstorm_alerts), thunderstormStatistics);
    }
    public void Tornado(View view){
        if(tornadoStatistics == null){
            tornadoStatistics = getString(R.string.no_alerts);
        }
        showMessage(getString(R.string.tornado_alerts), tornadoStatistics);
    }

    public void All_Statistics(View view){
        if(allStatistics == null){
            allStatistics = getString(R.string.no_alerts);
        }
        showMessage(getString(R.string.all_alerts), allStatistics);
    }

    void showMessage(String title, String message){
        new AlertDialog.Builder(this).setTitle(title).setMessage(message).setCancelable(true).show();
    }

    public void ongoing_alerts(View view) {

        DatabaseReference reference1 = database.getReference("Alerts");
        reference1.addChildEventListener(new ChildEventListener() {
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
                    String[] location = UserHomePage.userLocation.split(",");
                    double ulat = Double.parseDouble(location[0]);
                    double ulon = Double.parseDouble(location[1]);
                    double distance = Distance.calculateDistance2(lat, lon, ulat, ulon);
                    if (distance <= 10) {
                        Intent intent = new Intent(Statistics.this, Alert.class);
                        intent.putExtra("fullname", fullname);
                        intent.putExtra("authId", authId);
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
}