package com.john.smartalert;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class AddEmergency extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    String fullname, authid, language, selectedEmergency, file, filename;
    Spinner spinner;
    TextView textView7, textView8;
    EditText comments;
    ImageView savedImage;
    StorageReference storageReference;
    FirebaseDatabase database;
    DatabaseReference reference;
    ActivityResultLauncher<String> cameraPermission;
    ActivityResultLauncher<String> storagePermission;
    byte[] byteArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_emergency);
        fullname = getIntent().getStringExtra("fullname");
        authid = getIntent().getStringExtra("authId");
        language = this.getSharedPreferences("Settings", MODE_PRIVATE).getString("Language","");
        Authentication.setLocale(AddEmergency.this, language);
        file = "";
        //recreate();
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
                    Toast.makeText(getApplicationContext(), getString(R.string.camera_granted), Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getApplicationContext(), getString(R.string.camera_denied), Toast.LENGTH_SHORT).show();

                }
            }
       });
       storagePermission = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
           @Override
           public void onActivityResult(Boolean result) {
               if(result){
                   Toast.makeText(getApplicationContext(), getString(R.string.storage_granted), Toast.LENGTH_SHORT).show();
               }
               else{
                   Toast.makeText(getApplicationContext(), getString(R.string.storage_denied), Toast.LENGTH_SHORT).show();
               }
           }
       });
       if (ContextCompat.checkSelfPermission(AddEmergency.this,
               android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(AddEmergency.this,
               Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
           ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE},5);
           //           cameraPermission.launch(android.Manifest.permission.CAMERA);
       }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String category = parent.getItemAtPosition(position).toString();
        switch(category){
            case ("Πυρκαγιά"):
                selectedEmergency = "Fire";
                break;
            case ("Πλημμύρα"):
                selectedEmergency = "Flood";
                break;
            case ("Καύσωνας"):
                selectedEmergency = "Heatwave";
                break;
            case ("Καταιγίδα"):
                selectedEmergency = "Thunderstorm";
                break;
            case ("Σεισμός"):
                selectedEmergency = "Earthquake";
                break;
            case ("Θυελλώδεις Άνεμοι"):
                selectedEmergency = "Tornado";
                break;
            case ("Χιονόπτωση"):
                selectedEmergency = "Blizzard";
                break;
            default:
                selectedEmergency = parent.getItemAtPosition(position).toString();
                break;
        }
    }

    public void onNothingSelected(AdapterView<?> parent) {
        selectedEmergency = null;
    }


    public void photo(View view){
       Intent intent = new Intent(AddEmergency.this, Camera.class);
       intent.putExtra("authId",authid);
        startActivityForResult(intent,1);
        /* if (ContextCompat.checkSelfPermission(this,
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
                showMessage(getString(R.string.activity_not_found), e.toString());
            }*/
        Authentication.setLocale(AddEmergency.this, language);
    }

    void uploadImage(){
        storageReference = FirebaseStorage.getInstance().getReference("new_images/"+filename);
        storageReference.putBytes(byteArray).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                savedImage.setImageURI(null);
                showMessage("Upload image Success", "");
                insertData();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showMessage(getString(R.string.upload_failure_title), getString(R.string.upload_failure_text));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK){
            byteArray = data.getByteArrayExtra("image");
            Bitmap bmp= BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            file = "upload";
            filename = data.getStringExtra("filename");
            System.out.println(filename);
            try {
                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true); // rotating bitmap
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byteArray = baos.toByteArray();
            }
            catch (Exception e) {

            }
            savedImage.setImageBitmap(bmp);

        }
    }

    private String getTime(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss", Locale.forLanguageTag(""));
        return formatter.format(new Date());
    }

    public void submit(View view){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            System.out.println(comments.getText().toString());
                            System.out.println(selectedEmergency);
                            System.out.println(UserHomePage.userLocation);
                            if(selectedEmergency != null && !comments.getText().toString().equals("") && UserHomePage.userLocation != null) {
                                if (file.equals("upload")) {
                                    uploadImage();
                                    file = "";
                                } else {
                                    filename = "-";
                                    insertData();
                                }
                            }
                            else{
                                showMessage(getString(R.string.invalid_submission_title), getString(R.string.invalid_submission_text));
                            }
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            dialog.dismiss();
                            break;
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

    void insertData(){
        String time = getTime();
        String emergency_id = UUID.randomUUID().toString();
        reference.child(emergency_id).child("UserId").setValue(authid);
        reference.child(emergency_id).child("Category").setValue(selectedEmergency);
        reference.child(emergency_id).child("Comments").setValue(comments.getText().toString());
        reference.child(emergency_id).child("Location").setValue(UserHomePage.userLocation);
        reference.child(emergency_id).child("Time").setValue(time);
        reference.child(emergency_id).child("Photo").setValue(filename);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.success)).setMessage(R.string.emergency_submitted).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((Activity) getBaseContext()).finish();
                Authentication.setLocale(AddEmergency.this, language);
            }
        }).show().getWindow().setGravity(Gravity.CENTER);
    }
}

