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
    String fullname, authId;
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
        textView3 = findViewById(R.id.textView3);
        textView3.setText("Hello "+ fullname+". Here you can see all the times you were notified for an emergency, by type of incident.");
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
                            allStatistics = "You have no alerts for this category";
                        }
                        if (snap.child("statistics").child("fire").getValue()!=null){
                            fireStatistics = snap.child("statistics").child("fire").getValue().toString();
                        }
                        else {
                            fireStatistics = "You have no alerts for this category";
                        }
                        if (snap.child("statistics").child("flood").getValue()!=null){
                            floodStatistics = snap.child("statistics").child("flood").getValue().toString();
                        }
                        else {
                            floodStatistics = "You have no alerts for this category";
                        }
                        if (snap.child("statistics").child("earthquake").getValue()!=null){
                            earthquakeStatistics = snap.child("statistics").child("earthquake").getValue().toString();
                        }
                        else {
                            earthquakeStatistics = "You have no alerts for this category";
                        }
                        if (snap.child("statistics").child("thunderstorm").getValue()!=null){
                            thunderstormStatistics = snap.child("statistics").child("thunderstorm").getValue().toString();
                        }
                        else {
                            thunderstormStatistics = "You have no alerts for this category";
                        }
                        if (snap.child("statistics").child("heatwave").getValue()!=null){
                            heatwaveStatistics = snap.child("statistics").child("heatwave").getValue().toString();
                        }
                        else {
                            heatwaveStatistics = "You have no alerts for this category";
                        }
                        if (snap.child("statistics").child("tornado").getValue()!=null){
                            tornadoStatistics = snap.child("statistics").child("tornado").getValue().toString();
                        }
                        else {
                            tornadoStatistics = "You have no alerts for this category";
                        }
                        if (snap.child("statistics").child("blizzard").getValue()!=null){
                            blizzardStatistics = snap.child("statistics").child("blizzard").getValue().toString();
                        }
                        else {
                            blizzardStatistics = "You have no alerts for this category";
                        }
                    }
                }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void Fire(View view){
        showMessage("History of your Fire Alerts", fireStatistics);
    }

    public void Flood(View view){
        showMessage("History of your Flood Alerts", floodStatistics);
    }

    public void Earthquake(View view){
        showMessage("History of your Earthquake Alerts", earthquakeStatistics);
    }

    public void Heatwave(View view){
        showMessage("History of your Heatwave Alerts", heatwaveStatistics);
    }

    public void Blizzard(View view){
        showMessage("History of your Blizzard Alerts", blizzardStatistics);
    }

    public void Thunderstorm(View view){
        showMessage("History of your Thunderstorm Alerts", thunderstormStatistics);
    }
    public void Tornado(View view){
        showMessage("History of your Tornado Alerts", tornadoStatistics);
    }

    public void All_Statistics(View view){
        showMessage("History of all your Alerts", allStatistics);
    }

    void showMessage(String title, String message){
        new AlertDialog.Builder(this).setTitle(title).setMessage(message).setCancelable(true).show();
    }
}