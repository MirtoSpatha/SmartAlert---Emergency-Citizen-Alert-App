package com.john.smartalert;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

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
    ActivityResultLauncher<String> storagePermission;

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
        savedImage = findViewById(R.id.imageView2);
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
        storagePermission = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if(result){
                    Toast.makeText(getApplicationContext(), "Storage Permission GRANTED", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getApplicationContext(), "Storage Permission DENIED", Toast.LENGTH_SHORT).show();

                }
            }
        });
                if (ContextCompat.checkSelfPermission(AddEmergency.this,
                        android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    cameraPermission.launch(android.Manifest.permission.CAMERA);
                }
        if (ContextCompat.checkSelfPermission(AddEmergency.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            cameraPermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
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
        if (ContextCompat.checkSelfPermission(AddEmergency.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            cameraPermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        //Intent.ACTION_CAMERA_BUTTON
        //MediaStore.ACTION_IMAGE_CAPTURE
        final int REQUEST_IMAGE_CAPTURE = 1;
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            try {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            } catch (ActivityNotFoundException e) {
                showMessage("Activity not found", e.toString());
            }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK){
            imageuri = data.getData();
            //savedImage.getLayoutParams().width = 120;
            //savedImage.getLayoutParams().height = 220;
            //savedImage.setAdjustViewBounds(true);
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
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss", Locale.forLanguageTag(""));
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
                                file = "-";
                            }
                            String time = getTime();
                            String emergency_id = UUID.randomUUID().toString();
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
        builder.setMessage(R.string.confirm_submit).setPositiveButton(R.string.yes, dialogClickListener)
                .setNegativeButton(R.string.no, dialogClickListener).show();
    }

    void showMessage(String title, String message){
        new AlertDialog.Builder(this).setTitle(title).setMessage(message).setCancelable(true).show();
    }
}