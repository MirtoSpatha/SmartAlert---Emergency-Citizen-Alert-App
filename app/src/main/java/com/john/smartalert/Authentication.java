package com.john.smartalert;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.john.smartalert.R;

public class Authentication extends AppCompatActivity {
    EditText email,password,fullname;
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseDatabase database;

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
                                showMessage("Success","User profile created!");
                            }else {
                                showMessage("Error",task.getException().getLocalizedMessage());
                            }
                        }
                    });
        }else {
            showMessage("Error","Please provide all info!");
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
                        showMessage("Success","User signed in successfully!");
                        if(email.getText().toString().contains("@civilprotection.gr"))
                        {
                            Intent intent = new Intent(Authentication.this, EmployeeHomePage.class);
                            intent.putExtra("fullname",fullname.getText().toString());
                            //intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            startActivity(intent);
                        }
                        else
                        {
                            Intent intent = new Intent(Authentication.this, UserHomePage.class);
                            intent.putExtra("fullname",fullname.getText().toString());
                            //intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            startActivity(intent);
                        }
                    }else {
                        showMessage("Error",task.getException().getLocalizedMessage());
                    }
                }
            });
        }
    }

    void showMessage(String title, String message){
        new AlertDialog.Builder(this).setTitle(title).setMessage(message).setCancelable(true).show();
    }
}