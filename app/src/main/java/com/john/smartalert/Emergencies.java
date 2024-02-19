package com.john.smartalert;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.HashMap;

public class Emergencies extends AppCompatActivity {

    HashMap<String,HashMap<String,String>> emergencies;
    ArrayList<String> result;

    LinearLayout groups;
    RequestQueue requestQueue;
    ArrayList<String> address;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.emergencies_page);
        requestQueue = Volley.newRequestQueue(this);
        result = getIntent().getStringArrayListExtra("Results");
        address = getIntent().getStringArrayListExtra("Address");
//        System.out.println(address);
//        System.out.println(result.get(0));
        emergencies =new HashMap<>();
        groups = findViewById(R.id.allGroups);
        int i=0;
        for (String s:result) {
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
            emergencies.put(String.valueOf(i),map);
            i++;
        }
        emergencies.forEach((s, stringStringHashMap) -> {

            View view = LayoutInflater.from(this).inflate(R.layout.group_emergence,null);
            Button b = view.findViewById(R.id.inspect);
            b.setTag(s);
            TextView category = view.findViewById(R.id.category);
            category.setText(stringStringHashMap.get("category"));
            TextView a = view.findViewById(R.id.address);
            a.setText(address.get(Integer.parseInt(s)));
            TextView location = view.findViewById(R.id.location);
            location.setText(stringStringHashMap.get("center"));
            TextView time = view.findViewById(R.id.time);
            time.setText(stringStringHashMap.get("time"));
            TextView reports = view.findViewById(R.id.reports);
            reports.setText(stringStringHashMap.get("reports"));
            groups.addView(view);
        });


//        System.out.println(emergencies);
    }

    public void inspectEmergence(View view){
        String tag = view.getTag().toString();
        HashMap<String,String> group = emergencies.get(tag);
        System.out.println(group);
        Intent intent = new Intent(this,EmergenciesDetails.class);
        intent.putExtra("group",group.get("group"));
        intent.putExtra("location",group.get("center"));
        intent.putExtra("address",address.get(Integer.parseInt(tag)));
        intent.putExtra("time",group.get("time"));
        intent.putExtra("category",group.get("category"));
        intent.putExtra("Results",getIntent().getStringArrayListExtra("Results"));
        intent.putExtra("Address",getIntent().getStringArrayListExtra("Address"));
        startActivity(intent);
    }

    /*private void locationToAddress(String loc){
        String[] l = loc.split(",");
        StringBuilder url = new StringBuilder();
        url.append("https://nominatim.openstreetmap.org/reverse?format=jsonv2&lat=").append(l[1]).append("&lon=").append(l[0]);
        JSONObject body = new JSONObject();
        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.GET,
                url.toString(), body, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    System.out.println(response.getString("display_name"));
                    temp.add(response.getString("display_name"));
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
    }*/
//test@civilprotection.gr
}