package com.john.smartalert;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class Emergencies extends AppCompatActivity {

    HashMap<String,HashMap<String,String>> emergencies;
    ArrayList<String> result, address;
    String language;
    private LinearLayout groups;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.emergencies_page);
        language = this.getSharedPreferences("Settings", MODE_PRIVATE).getString("Language","");
        Authentication.setLocale(Emergencies.this, language);
        result = getIntent().getStringArrayListExtra("Results");
        address = getIntent().getStringArrayListExtra("Address");
        emergencies =new HashMap<>();
        groups = findViewById(R.id.allGroups);
        int i=0;
        for (String s:result) {
            String[] categories =s.split("\\|");
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
            emergencies.put(String.valueOf(i),map);
            i++;
        }
        emergencies.forEach((s, stringStringHashMap) -> {
            View view = LayoutInflater.from(this).inflate(R.layout.group_emergence,null);
            Button b = view.findViewById(R.id.inspect);
            b.setTag(s);
            TextView category = view.findViewById(R.id.category);
            category.setText(stringStringHashMap.get("Category"));
            TextView a = view.findViewById(R.id.address);
            a.setText(address.get(Integer.parseInt(s)));
            TextView location = view.findViewById(R.id.location);
            location.setText(stringStringHashMap.get("Center"));
            TextView time = view.findViewById(R.id.time);
            time.setText(stringStringHashMap.get("Time"));
            TextView reports = view.findViewById(R.id.reports);
            reports.setText(stringStringHashMap.get("Reports"));
            groups.addView(view);
        });
    }

    public void inspectEmergence(View view){
        String tag = view.getTag().toString();
        HashMap<String,String> group = emergencies.get(tag);
        Intent intent = new Intent(this,EmergenciesDetails.class);
        intent.putExtra("Group",group.get("Group"));
        intent.putExtra("Location",group.get("Center"));
        intent.putExtra("Address",address.get(Integer.parseInt(tag)));
        intent.putExtra("Time",group.get("Time"));
        intent.putExtra("Category",group.get("Category"));
        intent.putExtra("Results",getIntent().getStringArrayListExtra("Results"));
        intent.putExtra("Address2",getIntent().getStringArrayListExtra("Address"));
        startActivity(intent);
    }
}