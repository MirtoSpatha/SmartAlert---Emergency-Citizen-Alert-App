package com.john.smartalert;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Alert extends AppCompatActivity {

    TextView textView4, textView5, Address, Category, Time,info;
    String fullname, authId, language;
    Integer count;
    ArrayList<String> address,category,time;
    FirebaseDatabase database;
    DatabaseReference reference;
    LinearLayout allalerts;
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
        textView4.setText(getString(R.string.hello_user)+fullname+getString(R.string.ongoing_alerts_intro));
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Alerts");
        address = getIntent().getStringArrayListExtra("AddressList");
        category = getIntent().getStringArrayListExtra("CategoryList");
        time = getIntent().getStringArrayListExtra("TimeList");
        allalerts = findViewById(R.id.allalerts);
        if(address.isEmpty() && category.isEmpty() && time.isEmpty()){
            textView4.setText(getString(R.string.no_alerts));
        }
        else{
            if(address.size() == category.size() && address.size() == time.size()){
                count = address.size();
            }
            else{
                showMessage(getString(R.string.error),getString(R.string.database_error));
                this.finish();
            }
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
        for(int i=0;i<count;i++){
            View view = LayoutInflater.from(this).inflate(R.layout.alert_item, null);
            Address = view.findViewById(R.id.region);
            Category = view.findViewById(R.id.category2);
            Time = view.findViewById(R.id.time2);
            info = view.findViewById(R.id.alertInfo);
            Address.setText(address.get(i));
            Category.setText(category.get(i));
            Time.setText(time.get(i));
            switch (category.get(i)) {
                case ("Fire"): {
                    info.setText(getString(R.string.alert_text_intro) + getString(R.string.fire_text));
                    break;
                }
                case ("Flood"): {
                    info.setText(getString(R.string.alert_text_intro) + getString(R.string.flood_text));
                    break;
                }
                case ("Earthquake"): {
                    info.setText(getString(R.string.alert_text_intro) + getString(R.string.earthquake_text));
                    break;
                }
                case ("Thunderstorm"): {
                    info.setText(getString(R.string.alert_text_intro) + getString(R.string.thunderstorm_text));
                    break;
                }
                case ("Heatwave"): {
                    info.setText(getString(R.string.alert_text_intro) + getString(R.string.heatwave_text));
                    break;
                }
                case ("Tornado"): {
                    info.setText(getString(R.string.alert_text_intro) + getString(R.string.tornado_text));
                    break;
                }
                case ("Blizzard"): {
                    info.setText(getString(R.string.alert_text_intro) + getString(R.string.blizzard_text));
                    break;
                }
            }
            tts.speak(getString(R.string.attention), TextToSpeech.QUEUE_ADD, null, null);
            tts.speak(info.getText(), TextToSpeech.QUEUE_ADD, null, null);
            allalerts.addView(view);
        }
    }

    public void back(View view){
        this.finish();
    }

    void showMessage(String title, String message){
        new AlertDialog.Builder(this).setTitle(title).setMessage(message).setCancelable(true).show();
    }
}