package com.john.smartalert;

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

    private FirebaseDatabase database;
    private DatabaseReference reference;
    private StorageReference storageReference;
    private LinearLayout incident;
    String group, language;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.emergencies_details);
        language = this.getSharedPreferences("Settings", MODE_PRIVATE).getString("Language","");
        Authentication.setLocale(EmergenciesDetails.this, language);

        database = FirebaseDatabase.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("new_images/");
        group= getIntent().getStringExtra("Group");
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
                                    if (task.isSuccessful()) {
                                        photo.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
                                    }
                                    else {
                                        photo.setImageResource(android.R.drawable.ic_menu_gallery);
                                    }
                                }
                            });
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                    else {
                        photo.setImageResource(R.drawable.image);
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
        alert.put("Location",getIntent().getStringExtra("Location"));
        alert.put("Address", getIntent().getStringExtra("Address"));
        alert.put("StartTime", getIntent().getStringExtra("Time"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss", Locale.forLanguageTag(""));
        alert.put("Time", formatter.format(new Date()));
        alert.put("Category", getIntent().getStringExtra("Category"));
        alert.put("Group",group);
        reference.push().setValue(alert);
        Toast.makeText(this,getString(R.string.sms_send),Toast.LENGTH_LONG);
        Intent intent = new Intent(EmergenciesDetails.this,EmployeeHomePage.class);
        startActivity(intent);
    }

    public void decline(View view){
        ArrayList<String> result = getIntent().getStringArrayListExtra("Results");
        ArrayList<String> address2 = getIntent().getStringArrayListExtra("Address2");
        int i= 0;
        for (String s: result) {
            if (s.contains(group)){
                result.remove(s);
                address2.remove(i);
                break;
            }
            i++;
        }
        Intent intent = new Intent(EmergenciesDetails.this, Emergencies.class);
        intent.putStringArrayListExtra("Results",result);
        intent.putStringArrayListExtra("Address",address2);
        startActivity(intent);
    }
}