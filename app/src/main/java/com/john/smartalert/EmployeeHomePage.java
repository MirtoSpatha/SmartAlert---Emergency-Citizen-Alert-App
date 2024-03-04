package com.john.smartalert;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class EmployeeHomePage extends AppCompatActivity {
    String fullname,authId,language;
    private TextView textView24;
    private FirebaseDatabase database;
    private DatabaseReference reference, reference2;
    private RequestQueue requestQueue;
    ArrayList<String> result,address;
    private HashMap<String,String> tempAddress;
    private JSONObject newEmergencies, alerts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.employee_home_page);
        fullname = getIntent().getStringExtra("fullname");
        authId = getIntent().getStringExtra("authId");
        language = this.getSharedPreferences("Settings", MODE_PRIVATE).getString("Language","");
        Authentication.setLocale(EmployeeHomePage.this, language);
        textView24 = findViewById(R.id.textView24);
        textView24.setText(getString(R.string.welcome)+fullname+getString(R.string.employee_homepage_intro));
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("New Emergency");
        newEmergencies = new JSONObject();
        requestQueue = Volley.newRequestQueue(this);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    for (DataSnapshot d : snapshot.getChildren()){
                        try {
                            HashMap<String,String> temp = (HashMap<String, String>) d.getValue();
                            JSONObject t = new JSONObject();
                            temp.forEach((s, s2) -> {
                                try {
                                    t.put(s,s2);
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                            newEmergencies.put(d.getKey(),t);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        reference2 = database.getReference("Alerts");
        alerts = new JSONObject();

        reference2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    for (DataSnapshot d : snapshot.getChildren()){
                        try {
                            HashMap<String,String> temp = (HashMap<String, String>) d.getValue();
                            JSONObject t = new JSONObject();
                            temp.forEach((s, s2) -> {
                                try {
                                    t.put(s,s2);
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                            alerts.put(d.getKey(),t);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    if (newEmergencies.length()>0){
                        emergencies(newEmergencies,alerts);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void emergencies(JSONObject newEmergencies,JSONObject Alerts){
        String url = "http://10.0.2.2:8080/emergence";
        JSONObject body = new JSONObject();
        try {
            body.put("alerts",Alerts);
            body.put("newEmergencies",newEmergencies);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST,
                url, body, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    result = new ArrayList<>();
                    tempAddress = new HashMap<>();
                    int size = response.length();
                    if (size>2) {
                        for (int i = 0; i < size-2; i++) {
                            result.add(response.getString(String.valueOf(i)));
                            String[] temp = response.getString(String.valueOf(i)).split("\\|");
                            String[] center =temp[1].split("=");
                            String[] loc = center[1].split(",");
                            final int j=i;
                            new Thread(()->{
                                List<Address> addresses;
                                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                                try {
                                    addresses = geocoder.getFromLocation(Double.parseDouble(loc[0]),Double.parseDouble(loc[1]),1);
                                    tempAddress.put(String.valueOf(j),addresses.get(0).getAddressLine(0));
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }).start();
                        }
                        if (!response.getString("updateAlerts").isEmpty()) {
                            updateAlerts(response.getString("updateAlerts"));
                        }
                        if (!response.getString("oldEmergencies").isEmpty()){
                            updateEmergencies(response.getString("oldEmergencies"));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.getLocalizedMessage();
            }
        });
        requestQueue.add(postRequest);
    }
    private void updateAlerts(String s){
        String[] alerts = s.split("\\|");
        for (String t:alerts) {
            String[] key = t.split("=");
            reference2 = database.getReference("Alerts/"+key[0]+"/Group");
            reference2.setValue(key[1]);
        }
    }

    private void updateEmergencies(String emergencies){
        String[] old = emergencies.split(",");
        for (String s:old) {
            reference = database.getReference("New Emergency").child(s);
            HashMap<String,String> data = new HashMap<>();
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot snapshot1 :snapshot.getChildren()){
                        data.put(snapshot1.getKey(),snapshot1.getValue().toString());
                    }
                    reference2 = database.getReference("All Emergency");
                    reference2.child(s).setValue(data);
                    reference =  database.getReference("New Emergency").child(snapshot.getKey());
                    reference.removeValue();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    public void showTheEmergencies(View view) {
        address = new ArrayList<>();
        for (int k=0;k<tempAddress.size();k++){
            address.add(tempAddress.get(String.valueOf(k)));
        }
        Intent intent = new Intent(EmployeeHomePage.this, Emergencies.class);
        intent.putStringArrayListExtra("Results",result);
        intent.putStringArrayListExtra("Address",address);
        startActivity(intent);
    }
}