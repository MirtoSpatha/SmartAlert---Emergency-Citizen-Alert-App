package com.john.smartalert;

import android.Manifest;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EmergenciesDetails extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference reference;
    StorageReference storageReference;
    LinearLayout incident;
    String group, language;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.emergencies_details);
        language = this.getSharedPreferences("Settings", MODE_PRIVATE).getString("Language","");
        Authentication.setLocale(EmergenciesDetails.this, language);
        //recreate();
        ActivityCompat.requestPermissions(EmergenciesDetails.this,new String[]{Manifest.permission.SEND_SMS},100);

        database = FirebaseDatabase.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
         group= getIntent().getStringExtra("group");
        String[] e = group.split(",");
        incident = findViewById(R.id.allincidents);
        for (String s :e){
            View view = LayoutInflater.from(this).inflate(R.layout.emergancie_item,null);
            TextView comment = view.findViewById(R.id.textView16);
            TextView location = view.findViewById(R.id.textView18);
            TextView time = view.findViewById(R.id.textView20);
            ImageView photo = view.findViewById(R.id.photo);
            reference = database.getReference("New Emergency/"+s);
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Map<String,String> data = (Map<String, String>) snapshot.getValue();
                    System.out.println(data.get("Comments"));
                    comment.setText(data.get("Comments"));
                    location.setText(data.get("Location"));
                    time.setText(data.get("Time"));
                    if (!data.get("Photo").equals("-")){
                        try {
                            File file = File.createTempFile("temp","png");
                            StorageReference reference1 = storageReference.child(data.get("Photo"));
                            reference1.getFile(file).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                                    photo.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
                                }
                            });
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                    incident.addView(view);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    public void accept(View view){
        reference = database.getReference("Alerts");
        HashMap<String,String> alert = new HashMap<>();
        alert.put("location",getIntent().getStringExtra("location"));
        alert.put(" address", getIntent().getStringExtra("address"));
        alert.put("startTime", getIntent().getStringExtra("time"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss", Locale.forLanguageTag(""));
        alert.put("time", formatter.format(new Date()));
        alert.put("category", getIntent().getStringExtra("category"));
        alert.put("group",group);
        reference.push().setValue(alert);
        Toast.makeText(this,getString(R.string.sms_send),Toast.LENGTH_LONG);
        Intent intent = new Intent(EmergenciesDetails.this,EmployeeHomePage.class);
        startActivity(intent);
    }
//test@civilprotection.gr
    public void decline(View view){
        ArrayList<String> result = getIntent().getStringArrayListExtra("Results");
        for (String s: result) {
            if (s.contains(group)){
                result.remove(s);
            }
        }
        Intent intent = new Intent(EmergenciesDetails.this, Emergencies.class);
        intent.putStringArrayListExtra("Results",result);
        intent.putStringArrayListExtra("Address",getIntent().getStringArrayListExtra("Address"));
        startActivity(intent);
    }
}