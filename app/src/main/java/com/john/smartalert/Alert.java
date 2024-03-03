package com.john.smartalert;

import static com.john.smartalert.UserHomePage.tts;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class Alert extends AppCompatActivity {

    private TextView textView4, Address, Category, Time,info;
    String fullname, authId, language, text;
    private Integer count;
    private ArrayList<String> address,category,time;
    private LinearLayout allalerts;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);
        language = this.getSharedPreferences("Settings", MODE_PRIVATE).getString("Language","");
        fullname = getIntent().getStringExtra("fullname");
        authId = getIntent().getStringExtra("authId");
        Authentication.setLocale(Alert.this, language);
        textView4 = findViewById(R.id.textView4);
        textView4.setText(getString(R.string.hello_user)+fullname+getString(R.string.ongoing_alerts_intro));
        address = getIntent().getStringArrayListExtra("AddressList");
        category = getIntent().getStringArrayListExtra("CategoryList");
        time = getIntent().getStringArrayListExtra("TimeList");
        allalerts = findViewById(R.id.allalerts);
        if(address.isEmpty() && category.isEmpty() && time.isEmpty()){
            textView4.setText(getString(R.string.no_alerts));
            tts.speak(getString(R.string.no_alerts));
        }
        else{
            if(address.size() == category.size() && address.size() == time.size()){
                count = address.size();
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
                            text =getString(R.string.fire_intro);
                            break;
                        }
                        case ("Flood"): {
                            info.setText(getString(R.string.alert_text_intro) + getString(R.string.flood_text));
                            text =getString(R.string.flood_intro);
                            break;
                        }
                        case ("Earthquake"): {
                            info.setText(getString(R.string.alert_text_intro) + getString(R.string.earthquake_text));
                            text =getString(R.string.earthquake_intro);
                            break;
                        }
                        case ("Thunderstorm"): {
                            info.setText(getString(R.string.alert_text_intro) + getString(R.string.thunderstorm_text));
                            text =getString(R.string.thunderstorm_intro);
                            break;
                        }
                        case ("Heatwave"): {
                            info.setText(getString(R.string.alert_text_intro) + getString(R.string.heatwave_text));
                            text =getString(R.string.heatwave_intro);
                            break;
                        }
                        case ("Tornado"): {
                            info.setText(getString(R.string.alert_text_intro) + getString(R.string.tornado_text));
                            text =getString(R.string.tornado_intro);
                            break;
                        }
                        case ("Blizzard"): {
                            info.setText(getString(R.string.alert_text_intro) + getString(R.string.blizzard_text));
                            text =getString(R.string.blizzard_intro);
                            break;
                        }
                    }
                    tts.speak(getString(R.string.attention));
                    tts.speak(text);
                    allalerts.addView(view);
                }
            }
            else{
                showMessage(getString(R.string.error),getString(R.string.database_error));
                this.finish();
            }
        }



    }

    public void back(View view){
        this.finish();
    }

    void showMessage(String title, String message){
        new AlertDialog.Builder(this).setTitle(title).setMessage(message).setCancelable(true).show().getWindow().setGravity(Gravity.CENTER);;
    }
}