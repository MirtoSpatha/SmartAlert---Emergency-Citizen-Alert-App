package com.john.smartalert;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class AddEmergency extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    String fullname, authid;
    Spinner spinner;
    TextView textView7, textView8;

    TextView captureTxt;
    String path;
    Uri uri;
    private ImageView captureImage;
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

    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback.
    }

    public void photo(View view){
        //ImagePicker.Companion.with(AddEmergency.this).crop.maxResultSize();
    }

    public void submit(View view){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:

                        Toast.makeText(AddEmergency.this, "Emergency Submitted", Toast.LENGTH_SHORT).show();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.dismiss();
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to submit this incident?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }
}