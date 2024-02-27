package com.john.smartalert;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.LifecycleCameraController;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

public class Camera extends AppCompatActivity {
    String authid, language;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
//    Button bTakePicture, bRecording;
    StorageReference storageReference;
    private ImageCapture imageCapture;
    PreviewView previewView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera);
        previewView = findViewById(R.id.previewView);
        authid = getIntent().getStringExtra("authId");
        language = this.getSharedPreferences("Settings", MODE_PRIVATE).getString("Language","");

        LifecycleCameraController cameraController = new LifecycleCameraController(this);
        cameraController.bindToLifecycle(this);
        cameraController.setCameraSelector(CameraSelector.DEFAULT_BACK_CAMERA);
        previewView.setController(cameraController);

        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                startCameraX(cameraProvider);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }, getExecutor());

        /*ImageCapture imageCapture =
                new ImageCapture.Builder()
                        .setTargetRotation(previewView.getDisplay().getRotation())
                        .build();

        cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, imageCapture, imageAnalysis, preview);*/
     /*   ImageCapture imageCapture =
                new ImageCapture.Builder()
                        .setTargetRotation(previewView.getDisplay().getRotation())
                        .build();*/
    }

    private Executor getExecutor() {
        return ContextCompat.getMainExecutor(this);
    }

    @SuppressLint("RestrictedApi")
    private void startCameraX(ProcessCameraProvider cameraProvider) {

        cameraProvider.unbindAll();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        Preview preview = new Preview.Builder().build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        imageCapture = new ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build();


        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);
    }

    @SuppressLint("RestrictedApi")
    public void takePhoto(View view){
 /*       long timeStamp = System.currentTimeMillis();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, timeStamp);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");*/
        findViewById(R.id.loading).setVisibility(View.VISIBLE);

        imageCapture.takePicture(
                getExecutor(), new ImageCapture.OnImageCapturedCallback() {
                    @Override
                    public void onCaptureSuccess(@NonNull ImageProxy image) {
                        super.onCaptureSuccess(image);
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss", Locale.forLanguageTag(""));
                        String time =  formatter.format(new Date());
                        String filename =  authid + "_" + time;

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        image.toBitmap().compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] data = baos.toByteArray();
                        Intent intent = new Intent();
                        intent.putExtra("image",data);
                        intent.putExtra("filename",filename);
                        setResult(Activity.RESULT_OK,intent);
                        Authentication.setLocale(Camera.this, language);
                        finish();
                    }

                    @Override
                    public void onPostviewBitmapAvailable(@NonNull Bitmap bitmap) {
                        super.onPostviewBitmapAvailable(bitmap);
                    }
                }
            /*new ImageCapture.OnImageSavedCallback() {
                @Override
                public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                    Toast.makeText(Camera.this,"Saving...",Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(@NonNull ImageCaptureException exception) {
                    Toast.makeText(Camera.this,"Error: "+exception.getMessage(),Toast.LENGTH_SHORT).show();


                }
            }*/
        );
    }

}