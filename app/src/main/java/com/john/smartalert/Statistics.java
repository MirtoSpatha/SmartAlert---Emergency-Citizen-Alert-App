package com.john.smartalert;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
}