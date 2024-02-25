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
        ongoing_alerts(null);
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

    public void ongoing_alerts(View view) {

        DatabaseReference reference1 = database.getReference("Alerts");
        reference1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                HashMap<String, String> alert = new HashMap<>();
                alert.put("alertKey", snapshot.getKey());
                for (DataSnapshot data : snapshot.getChildren()) {
                    alert.put(data.getKey().toString(), data.getValue().toString());
                }
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss");
                LocalDateTime date = LocalDateTime.parse(alert.get("time"), formatter);
                ;
                LocalDateTime now = LocalDateTime.now();
                Duration duration = Duration.between(date, LocalDateTime.now());
                if (duration.toHours() < 24) {
                    String[] temp = alert.get("location").split(",");
                    double lat = Double.parseDouble(temp[0]);
                    double lon = Double.parseDouble(temp[1]);
                    String[] location = UserHomePage.userLocation.split(",");
                    double ulat = Double.parseDouble(location[0]);
                    double ulon = Double.parseDouble(location[1]);
                    double distance = Distance.calculateDistance2(lat, lon, ulat, ulon);
                    if (distance <= 10) {
                        Intent intent = new Intent(Alert.this, Alert.class);
                        intent.putExtra("fullname", fullname);
                        intent.putExtra("authId", authId);
                        intent.putExtra("address", alert.get(" address"));
                        intent.putExtra("category", alert.get("category"));
                        intent.putExtra("time", alert.get("time"));
                        startActivity(intent);

                        reference = database.getReference("Users/" + authId + "/statistics/" + alert.get("alertKey"));
                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                System.out.println(snapshot.getValue());
                                if (snapshot.getValue() == null) {
                                    reference.child("address").setValue(alert.get(" address"));//edo bazo tin odo kai oxi to location
                                    reference.child("category").setValue(alert.get("category"));
                                    reference.child("time").setValue(alert.get("time"));
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}