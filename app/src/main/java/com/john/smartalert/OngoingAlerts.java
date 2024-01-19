package com.john.smartalert;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class OngoingAlerts extends AppCompatActivity {

    String fullname, authId;
    TextView textView4;
    FirebaseDatabase database;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ongoing_alerts);
        fullname = getIntent().getStringExtra("fullname");
        authId = getIntent().getStringExtra("authId");
        textView4 = findViewById(R.id.textView4);
        textView4.setText("Hello "+ fullname+". Here you can see all the ongoing emergency events. " +
                "Please, strictly follow the instructions of authorities, for your safety.");
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Users").child(authId);
    }
}