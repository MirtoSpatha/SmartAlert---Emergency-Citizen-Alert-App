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
        reference = database.getReference("Users").child(authId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snap) {
                if (snap.getValue()!=null){
                        if (snap.child("statistics").getValue()!=null){
                            allStatistics = snap.child("statistics").getValue().toString();
                        }
                        else {
                            allStatistics = getString(R.string.no_alerts);
                        }
                        if (snap.child("statistics").child("fire").getValue()!=null){
                            fireStatistics = snap.child("statistics").child("fire").getValue().toString();
                        }
                        else {
                            fireStatistics = getString(R.string.no_alerts);
                        }
                        if (snap.child("statistics").child("flood").getValue()!=null){
                            floodStatistics = snap.child("statistics").child("flood").getValue().toString();
                        }
                        else {
                            floodStatistics = getString(R.string.no_alerts);
                        }
                        if (snap.child("statistics").child("earthquake").getValue()!=null){
                            earthquakeStatistics = snap.child("statistics").child("earthquake").getValue().toString();
                        }
                        else {
                            earthquakeStatistics = getString(R.string.no_alerts);
                        }
                        if (snap.child("statistics").child("thunderstorm").getValue()!=null){
                            thunderstormStatistics = snap.child("statistics").child("thunderstorm").getValue().toString();
                        }
                        else {
                            thunderstormStatistics = getString(R.string.no_alerts);
                        }
                        if (snap.child("statistics").child("heatwave").getValue()!=null){
                            heatwaveStatistics = snap.child("statistics").child("heatwave").getValue().toString();
                        }
                        else {
                            heatwaveStatistics = getString(R.string.no_alerts);
                        }
                        if (snap.child("statistics").child("tornado").getValue()!=null){
                            tornadoStatistics = snap.child("statistics").child("tornado").getValue().toString();
                        }
                        else {
                            tornadoStatistics = getString(R.string.no_alerts);
                        }
                        if (snap.child("statistics").child("blizzard").getValue()!=null){
                            blizzardStatistics = snap.child("statistics").child("blizzard").getValue().toString();
                        }
                        else {
                            blizzardStatistics = getString(R.string.no_alerts);
                        }
                    }
                }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void Fire(View view){
        showMessage(getString(R.string.fire_alerts), fireStatistics);
    }

    public void Flood(View view){
        showMessage(getString(R.string.flood_alerts), floodStatistics);
    }

    public void Earthquake(View view){
        showMessage(getString(R.string.earthquake_alerts), earthquakeStatistics);
    }

    public void Heatwave(View view){
        showMessage(getString(R.string.heatwave_alerts), heatwaveStatistics);
    }

    public void Blizzard(View view){
        showMessage(getString(R.string.blizzard_alerts), blizzardStatistics);
    }

    public void Thunderstorm(View view){
        showMessage(getString(R.string.thunderstorm_alerts), thunderstormStatistics);
    }
    public void Tornado(View view){
        showMessage(getString(R.string.tornado_alerts), tornadoStatistics);
    }

    public void All_Statistics(View view){
        showMessage(getString(R.string.all_alerts), allStatistics);
    }

    void showMessage(String title, String message){
        new AlertDialog.Builder(this).setTitle(title).setMessage(message).setCancelable(true).show();
    }
}