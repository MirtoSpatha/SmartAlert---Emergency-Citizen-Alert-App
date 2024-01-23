package com.john.smartalert;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Camera;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.play.core.integrity.IntegrityTokenRequest;
import com.google.firebase.Firebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class AddEmergency extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    String fullname, authid, selectedEmergency;
    Spinner spinner;
    TextView textView7, textView8;
    EditText comments;
    Uri imageuri;
    ImageView savedImage;
    StorageReference storageReference;
    FirebaseDatabase database;
    DatabaseReference reference;
    ActivityResultLauncher<String> cameraPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_emergency);
        fullname = getIntent().getStringExtra("fullname");
        authid = getIntent().getStringExtra("authId");
        textView7 = findViewById(R.id.textView7);
        textView8 = findViewById(R.id.textView8);
        SpannableString star=  new SpannableString("*");
        star.setSpan(new ForegroundColorSpan(Color.RED), 0, 1, 0);
        textView7.append(star);
        textView8.append(star);
        comments = findViewById(R.id.editTextTextMultiLine);
        spinner = findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
        // Create an ArrayAdapter using the string array and a default spinner layout.
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.emergencies_array,
                android.R.layout.simple_spinner_item
        );
        // Specify the layout to use when the list of choices appears.
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner.
        spinner.setAdapter(adapter);
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("New Emergency");
       cameraPermission = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
                    @Override
                    public void onActivityResult(Boolean result) {
                        if(result){
                            Toast.makeText(getApplicationContext(), "Camera Permission GRANTED", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "Camera Permission DENIED", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
                if (ContextCompat.checkSelfPermission(AddEmergency.this,
                        android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    cameraPermission.launch(android.Manifest.permission.CAMERA);
                }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedEmergency = parent.getItemAtPosition(position).toString();
    }

    public void onNothingSelected(AdapterView<?> parent) {
        selectedEmergency = null;
    }


    public void photo(View view){
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            cameraPermission.launch(android.Manifest.permission.CAMERA);
        }
        //Intent.ACTION_CAMERA_BUTTON
        //MediaStore.ACTION_IMAGE_CAPTURE
        Intent intent = new Intent(Intent.ACTION_CAMERA_BUTTON);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK){
            imageuri = data.getData();
            savedImage.setAdjustViewBounds(true);
            savedImage.setImageURI(imageuri);
        }
    }

    private boolean uploadImage(){
        final boolean[] result = {false};
        String time = getTime();
        String filename = authid + "_" + time;
        storageReference = FirebaseStorage.getInstance().getReference("new_images/"+filename);
        storageReference.putFile(imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                savedImage.setImageURI(null);
                result[0] = true;
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showMessage("Upload Image Failure", "Something went wrong when uploading the image. Please, try submitting the form again");
                result[0] = false;
            }
        });
        return result[0];
    }

    private String getTime(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.forLanguageTag(""));
        return formatter.format(new Date());
    }

    public void submit(View view){
        String comm = comments.getText().toString();
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String file = "";
                if(selectedEmergency != null && !comm.equals("") && UserHomePage.userLocation != null){
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            if(imageuri != null){
                                if (!uploadImage())
                                {
                                    return;
                                }
                                file = storageReference.toString();
                            }
                            else
                            {
                                file = "";
                            }
                            String time = getTime();
                            String emergency_id = UUID.randomUUID().toString();
                            reference.setValue(emergency_id);
                            reference.child(emergency_id).setValue("UserId");
                            reference.child(emergency_id).setValue("Category");
                            reference.child(emergency_id).setValue("Comments");
                            reference.child(emergency_id).setValue("Location");
                            reference.child(emergency_id).setValue("Time");
                            reference.child(emergency_id).setValue("Photo");
                            reference.child(emergency_id).child("UserId").setValue(authid);
                            reference.child(emergency_id).child("Category").setValue(selectedEmergency);
                            reference.child(emergency_id).child("Comments").setValue(comm);
                            reference.child(emergency_id).child("Location").setValue(UserHomePage.userLocation);
                            reference.child(emergency_id).child("Time").setValue(time);
                            reference.child(emergency_id).child("Photo").setValue(file);
                            Toast.makeText(AddEmergency.this, "Emergency Submitted", Toast.LENGTH_SHORT).show();
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            dialog.dismiss();
                            break;
                    }
                }
                else{
                    showMessage("Invalid Submission", "Please, fill in all the fields with *.");
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to submit this incident?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    void showMessage(String title, String message){
        new AlertDialog.Builder(this).setTitle(title).setMessage(message).setCancelable(true).show();
    }
}