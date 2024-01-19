package com.john.smartalert;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class UserHomePage extends AppCompatActivity {
    String fullname,authId;
    TextView textView2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_home_page);
        fullname = getIntent().getStringExtra("fullname");
        authId = getIntent().getStringExtra("authId");
        textView2 = findViewById(R.id.textView2);
        textView2.setText("Welcome "+fullname+"! This is an Emergency Alert App.\n" +
                " Here, you can get notified when an emergency is near you, view the ongoing alerts and statistics about previous emergencies near you.\n" +
                "You can also add an emergency event when it happens close to you.");
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


}