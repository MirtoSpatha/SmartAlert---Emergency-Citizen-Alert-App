package com.john.smartalert;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class AddEmergency extends AppCompatActivity {
    String fullname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_emergency);
        fullname = getIntent().getStringExtra("fullname");
    }
}