package com.john.smartalert;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class Statistics extends AppCompatActivity {
    String fullname, authId;
    String allStatistics, fireStatistics, floodStatistics, earthquakeStatistics, thunderstormStatistics, heatwaveStatistics, tornadoStatistics, blizzardStatistics;
    FirebaseDatabase database;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistics);
        fullname = getIntent().getStringExtra("fullname");
        authId = getIntent().getStringExtra("authId");
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Users").child(authId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue()!=null){
                    for (DataSnapshot snap : snapshot.getChildren()){
                        if (snap.child("statistics").getValue()!=null){
                            allStatistics = snap.child("statistics").getValue().toString();
                        }
                        if (snap.child("statistics").child("fire").getValue()!=null){
                            fireStatistics = snap.child("statistics").child("fire").getValue().toString();
                        }
                        if (snap.child("statistics").child("flood").getValue()!=null){
                            floodStatistics = snap.child("statistics").child("flood").getValue().toString();
                        }
                        if (snap.child("statistics").child("earthquake").getValue()!=null){
                            earthquakeStatistics = snap.child("statistics").child("earthquake").getValue().toString();
                        }
                        if (snap.child("statistics").child("thunderstorm").getValue()!=null){
                            thunderstormStatistics = snap.child("statistics").child("thunderstorm").getValue().toString();
                        }
                        if (snap.child("statistics").child("heatwave").getValue()!=null){
                            heatwaveStatistics = snap.child("statistics").child("heatwave").getValue().toString();
                        }
                        if (snap.child("statistics").child("tornado").getValue()!=null){
                            tornadoStatistics = snap.child("statistics").child("tornado").getValue().toString();
                        }
                        if (snap.child("statistics").child("blizzard").getValue()!=null){
                            blizzardStatistics = snap.child("statistics").child("blizzard").getValue().toString();
                        }
                        }
                    }
                }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void showMessage(String title, String message){
        new AlertDialog.Builder(this).setTitle(title).setMessage(message).setCancelable(true).show();
    }
}