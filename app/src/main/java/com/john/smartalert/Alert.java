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

    TextView textView4, Address, Category, Time,info;
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
        if(address.isEmpty() && category.isEmpty() && time.isEmpty()){
            info.setText(getString(R.string.no_ongoing_alerts));
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
        allalerts = findViewById(R.id.allalerts);
        /*
        for (String s:) {
            String[] categories =s.split("\\|");
//            System.out.println(categories[0]);
            HashMap<String,String> map = new HashMap<>();
            String[] group = categories[0].split("=");
            map.put(group[0],group[1]);
            String[] loc = categories[1].split("=");
            map.put(loc[0],loc[1]);
            String[] time = categories[2].split("=");
            map.put(time[0],time[1]);
            String[] reports = categories[3].split("=");
            map.put(reports[0],reports[1]);
            String[] c = categories[4].split("=");
            map.put(c[0],c[1]);
            alerts.put(String.valueOf(i),map);
            i++;
        }
         */
        for(int i=0;i<count;i++){
            View view = LayoutInflater.from(this).inflate(R.layout.alert_item, null);
            Address = view.findViewById(R.id.region);
            Category = view.findViewById(R.id.category2);
            Time = view.findViewById(R.id.time2);
            info = view.findViewById(R.id.alertInfo);
            Address.setText(address.get(i));
            Category.setText(category.get(i));
            Time.setText(time.get(i));
            int finalI = i;
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Map<String, String> data = (Map<String, String>) snapshot.getValue();
                    System.out.println(data.get("Comments"));
                    switch (category.get(finalI)) {
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

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    public void back(View view){

        this.finish();
    }

    void showMessage(String title, String message){
        new AlertDialog.Builder(this).setTitle(title).setMessage(message).setCancelable(true).show();
    }
}