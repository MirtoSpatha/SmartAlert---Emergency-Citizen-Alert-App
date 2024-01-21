package com.john.smartalert;

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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddEmergency extends AppCompatActivity implements AdapterView.OnItemSelectedListener, LocationListener {
    String fullname, authid, selectedEmergency, userLocation;
    Spinner spinner;
    TextView textView7, textView8;
    EditText comments;
    Uri imageuri;
    ImageView savedImage;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_emergency);
        fullname = getIntent().getStringExtra("fullname");
        authid = getIntent().getStringExtra("authid");
        textView7 = findViewById(R.id.textView7);
        textView8 = findViewById(R.id.textView8);
        SpannableString star=  new SpannableString("*");
        star.setSpan(new ForegroundColorSpan(Color.RED), 0, 5, 0);
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
        //captureTxt = findViewById(R.id.idEventBrowse);
        //captureImage
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedEmergency = parent.getItemAtPosition(position).toString();
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback.
    }

    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    public void photo(View view){
        Intent intent = new Intent(Intent.ACTION_CAMERA_BUTTON);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK){
            imageuri = data.getData();
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
                result[0] = true;
            }
        });
        return result[0];
    }

    private String getTime(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.forLanguageTag(""));
        return formatter.format(new Date());
    }
    @Override
    public void onLocationChanged(@NonNull Location location) {
        userLocation = String.valueOf(location);
        UserHomePage.locationManager.removeUpdates(this);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) { }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public void onProviderEnabled(@NonNull String provider) { }


    public void submit(View view){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(selectedEmergency != null && comments != null && userLocation != null){
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            if(imageuri != null){
                                uploadImage();
                            }
                            String time = getTime();
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