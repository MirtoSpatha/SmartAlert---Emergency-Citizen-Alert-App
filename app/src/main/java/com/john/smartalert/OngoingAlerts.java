package com.john.smartalert;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class OngoingAlerts extends AppCompatActivity {

    String fullname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ongoing_alerts);
        fullname = getIntent().getStringExtra("fullname");
    }
}