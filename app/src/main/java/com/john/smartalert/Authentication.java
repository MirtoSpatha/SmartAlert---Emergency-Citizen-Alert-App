package com.john.smartalert;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;

public class Authentication extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    EditText email,password,fullname;
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseDatabase database;
    Spinner spinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authentication);
        email = findViewById(R.id.editTextText);
        password = findViewById(R.id.editTextTextPassword);
        fullname = findViewById(R.id.editTextText2);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        if (user!=null){
            //Button b = findViewById(R.id.button);
            //b.setVisibility(View.GONE);
        }
        spinner = findViewById(R.id.spinner2);
        spinner.setOnItemSelectedListener(this);
        // Create an ArrayAdapter using the string array and a default spinner layout.
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.languages_array,
                android.R.layout.simple_spinner_item
        );
        // Specify the layout to use when the list of choices appears.
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner.
        spinner.setAdapter(adapter);
    }
    public void signup(View view){
        if(!email.getText().toString().isEmpty() &&
                !password.getText().toString().isEmpty() &&
                !fullname.getText().toString().isEmpty()){
            auth.createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                user = auth.getCurrentUser();
                                updateUser(user,fullname.getText().toString());
                                DatabaseReference reference2 = database.getReference("Users");
                                reference2.child(user.getUid()).child("UserID").setValue(user.getUid().toString());
                                reference2.child(user.getUid()).child("Fullname").setValue(fullname.getText().toString());
                                reference2.child(user.getUid()).child("statistics").setValue(null);
                                showMessage(getString(R.string.success),getString(R.string.user_profile_created));
                            }else {
                                try {
                                    throw task.getException();
                                } catch(FirebaseAuthWeakPasswordException e) {
                                    showMessage(getString(R.string.error),getString(R.string.error_weak_password));
                                }  catch(FirebaseAuthUserCollisionException e) {
                                    showMessage(getString(R.string.error),getString(R.string.error_user_exists));
                                }catch(FirebaseNetworkException e) {
                                    showMessage(getString(R.string.error),getString(R.string.error_network));
                                } catch(Exception e) {
                                    showMessage(getString(R.string.error),task.getException().getLocalizedMessage());
                                }
                                //showMessage(getString(R.string.error),task.getException().getLocalizedMessage());
                            }
                        }
                    });
        }else {
            showMessage(getString(R.string.error),getString(R.string.provide_all_info));
        }
    }
    private void updateUser(FirebaseUser user, String fullname){
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setDisplayName(fullname)
                .build();
        user.updateProfile(request);
    }
    public void signin(View view){
        if(!email.getText().toString().isEmpty() &&
                !password.getText().toString().isEmpty()){
            auth.signInWithEmailAndPassword(email.getText().toString(),
                    password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        showMessage(getString(R.string.success),getString(R.string.successful_sign_in));
                        if(email.getText().toString().contains("@civilprotection.gr"))
                        {
                            Intent intent = new Intent(Authentication.this, EmployeeHomePage.class);
                            intent.putExtra("fullname",auth.getCurrentUser().getDisplayName());
                            intent.putExtra("authId",auth.getUid().toString());
                            //intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            startActivity(intent);
                        }
                        else
                        {
                            Intent intent = new Intent(Authentication.this, UserHomePage.class);
                            intent.putExtra("fullname",auth.getCurrentUser().getDisplayName());
                            intent.putExtra("authId",auth.getUid().toString());
                            //intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            startActivity(intent);
                        }
                    }else {
                        try {
                            throw task.getException();
                        } catch(FirebaseAuthInvalidCredentialsException e) {
                            showMessage(getString(R.string.error),getString(R.string.error_invalid_credentials));
                        } catch(FirebaseNetworkException e) {
                            showMessage(getString(R.string.error),getString(R.string.error_network));
                        }catch(Exception e) {
                            showMessage(getString(R.string.error),task.getException().getLocalizedMessage());
                        }

                        //showMessage(getString(R.string.error),task.getException().getLocalizedMessage());
                    }
                }
            });
        }
    }

    void showMessage(String title, String message){
        new AlertDialog.Builder(this).setTitle(title).setMessage(message).setCancelable(true).show();
    }
    public static void setLocale(Activity activity, String language){
        Locale locale = Locale.forLanguageTag(language);
        Locale.setDefault(locale);
        Resources resources = activity.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
        SharedPreferences.Editor editor = activity.getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("Language", language);
        editor.apply();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getItemAtPosition(position).toString().equals("English") || parent.getItemAtPosition(position).toString().equals("Αγγλικά")){
            setLocale(this, "en");
            //finish();
            //startActivity(getIntent());
        }
        else if (parent.getItemAtPosition(position).toString().equals("Greek") || parent.getItemAtPosition(position).toString().equals("Ελληνικά")){
            setLocale(this, "el");
            //finish();
            //startActivity(getIntent());
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        setLocale(this, "en");
        //();
        //startActivity(getIntent());
    }
}