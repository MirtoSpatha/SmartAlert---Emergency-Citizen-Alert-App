package com.john.smartalert;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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
import java.util.Locale;

public class Alert extends AppCompatActivity {

    TextView textView4, Address, Category, Time,info;
    String fullname, authId, language, address,category,time;
    FirebaseDatabase database;
    DatabaseReference reference;
    private TextToSpeech tts;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);
        fullname = getIntent().getStringExtra("fullname");
        authId = getIntent().getStringExtra("authId");
        language = this.getSharedPreferences("Settings", MODE_PRIVATE).getString("Language","");
        Authentication.setLocale(Alert.this, language);
        //recreate();
        textView4 = findViewById(R.id.textView4);
        textView4.setText(getString(R.string.welcome_user)+fullname+getString(R.string.ongoing_alerts_intro));
        address = getIntent().getStringExtra("address");
        category = getIntent().getStringExtra("category");
        time = getIntent().getStringExtra("time");
        Address = findViewById(R.id.Region);
        Category = findViewById(R.id.Category);
        Time = findViewById(R.id.Time);
        info = findViewById(R.id.AlertInfo);
        Address.setText(address);
        Category.setText(category);
        Time.setText(time);
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Alerts");
        if(address.equals("") && category.equals("") && time.equals("")){
            info.setText(getString(R.string.no_ongoing_alerts));
        }
        TextToSpeech.OnInitListener  initListener= new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status==TextToSpeech.SUCCESS){
                    tts.setLanguage(new Locale(language));
                }
            }
        };
        tts = new TextToSpeech(this,initListener);
        switch (category){
            case ("Fire"):{
                info.setText(getString(R.string.alert_text_intro) + getString(R.string.fire_text));
                break;
            }
            case ("Flood"):{
                info.setText(getString(R.string.alert_text_intro) + getString(R.string.flood_text));
                break;
            }
            case ("Earthquake"):{
                info.setText(getString(R.string.alert_text_intro) + getString(R.string.earthquake_text));
                break;
            }
            case ("Thunderstorm"):{
                info.setText(getString(R.string.alert_text_intro) + getString(R.string.thunderstorm_text));
                break;
            }
            case ("Heatwave"):{
                info.setText(getString(R.string.alert_text_intro) + getString(R.string.heatwave_text));
                break;
            }
            case ("Tornado"):{
                info.setText(getString(R.string.alert_text_intro) + getString(R.string.tornado_text));
                break;
            }
            case ("Blizzard"):{
                info.setText(getString(R.string.alert_text_intro) + getString(R.string.blizzard_text));
                break;
            }
        }
        tts.speak(getString(R.string.attention),TextToSpeech.QUEUE_ADD,null,null);
        tts.speak(info.getText(),TextToSpeech.QUEUE_ADD,null,null);
    }

    public void back(View view){

        this.finish();
    }
}