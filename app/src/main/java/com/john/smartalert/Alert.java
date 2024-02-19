package com.john.smartalert;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Alert extends AppCompatActivity {

    TextView Address, Category, Time,info;
    String address,category,time;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);
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
        switch (category){
            case ("Fire"):{
                info.setText("1");
                break;
            }
            case ("Flood"):{
                info.setText("2");
                break;
            }
            case ("Earthquake"):{
                info.setText("3");
                break;
            }
            case ("Thunderstorm"):{
                info.setText("4");
                break;
            }
            case ("Heatwave"):{
                info.setText("5");
                break;
            }
            case ("Tornado"):{
                info.setText("6");
                break;
            }
            case ("Blizzard"):{
                info.setText("7");
                break;
            }
        }
    }

    public void back(View view){

        this.finish();
    }
}