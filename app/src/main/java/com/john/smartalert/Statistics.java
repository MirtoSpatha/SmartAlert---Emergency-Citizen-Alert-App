package com.john.smartalert;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class Statistics extends AppCompatActivity {
    String fullname, authId, language;
    private String allStatistics, fireStatistics, floodStatistics, earthquakeStatistics, thunderstormStatistics, heatwaveStatistics, tornadoStatistics, blizzardStatistics;
    private TextView textView3;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistics);
        fullname = getIntent().getStringExtra("fullname");
        authId = getIntent().getStringExtra("authId");
        language = this.getSharedPreferences("Settings", MODE_PRIVATE).getString("Language","");
        Authentication.setLocale(Statistics.this, language);
        textView3 = findViewById(R.id.textView3);
        textView3.setText(getString(R.string.welcome)+fullname+getString(R.string.statistics_intro));
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Users").child(authId).child("statistics");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snap) {
                if (snap.getValue()!=null){
                        for(DataSnapshot emergencies : snap.getChildren()){
                            String category = emergencies.child("Category").getValue().toString();
                            String address = emergencies.child("Address").getValue().toString();
                            String time = emergencies.child("Time").getValue().toString();
                            String stats = Objects.requireNonNull(address).concat("\n").concat(Objects.requireNonNull(time));

                            if(allStatistics == null){
                                allStatistics = "";
                            }
                            allStatistics = allStatistics.concat("\n");
                            for(DataSnapshot d : emergencies.getChildren()){
                                allStatistics = allStatistics.concat(Objects.requireNonNull(d.getKey().toString())).concat(": ").concat(Objects.requireNonNull(d.getValue()).toString()).concat("\n");
                            }
                            switch (Objects.requireNonNull(category)){
                                case ("Fire"):{
                                    if(fireStatistics == null){
                                        fireStatistics = "";
                                    }
                                    fireStatistics = fireStatistics.concat("\n").concat(stats).concat("\n");
                                    break;
                                }
                                case ("Flood"):{
                                    if(floodStatistics== null){
                                        floodStatistics = "";
                                    }
                                    floodStatistics = floodStatistics.concat("\n").concat(stats).concat("\n");
                                    break;
                                }
                                case ("Earthquake"):{
                                    if(earthquakeStatistics== null){
                                        earthquakeStatistics = "";
                                    }
                                    earthquakeStatistics = earthquakeStatistics.concat("\n").concat(stats).concat("\n");
                                    break;
                                }
                                case ("Thunderstorm"):{
                                    if(thunderstormStatistics== null){
                                        thunderstormStatistics = "";
                                    }
                                    thunderstormStatistics = thunderstormStatistics.concat("\n").concat(stats).concat("\n");
                                    break;
                                }
                                case ("Heatwave"):{
                                    if(heatwaveStatistics== null){
                                        heatwaveStatistics = "";
                                    }
                                    heatwaveStatistics = heatwaveStatistics.concat("\n").concat(stats).concat("\n");

                                    break;
                                }
                                case ("Tornado"):{
                                    if(tornadoStatistics== null){
                                        tornadoStatistics = "";
                                    }
                                    tornadoStatistics = tornadoStatistics.concat("\n").concat(stats).concat("\n");
                                    break;
                                }
                                case ("Blizzard"):{
                                    if(blizzardStatistics== null){
                                        blizzardStatistics = "";
                                    }
                                    blizzardStatistics = blizzardStatistics.concat("\n").concat(stats).concat("\n");
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
        new AlertDialog.Builder(this).setTitle(title).setMessage(message).setCancelable(true).show().getWindow().setGravity(Gravity.CENTER);;
    }
}