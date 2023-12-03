package com.example.dwelventory;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class CameraActivity extends AppCompatActivity{
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private CollectionReference photosRef;
    private String userId;

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private PreviewView previewView;
    private Button captureBtn;
    private Button retakeBtn;
    private Button confirmBtn;
    private CheckBox displayMainCheck;
    private Camera camera;
    private ImageCapture imageCapture;
    private static final int CAMERA_PERMISSION_CODE = 202;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_layout);

        Intent intent = getIntent();
        //userId = intent.getStringExtra("userId");
        //mAuth = FirebaseAuth.getInstance();
        //db = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        Log.d("STORAGEREF", storageRef.getPath());

        // get user device camera permissions
        cameraPermissions();

        /*if (CameraActivity.this instanceof CameraActivity.CameraActivityListener){
            listener = (CameraActivity.CameraActivityListener) CameraActivity.this;
        } else {
            throw new RuntimeException();
        }*/

        // find frontend elements
        previewView = findViewById(R.id.preview_view);
        captureBtn = findViewById(R.id.capture_button);
        retakeBtn = findViewById(R.id.retake_button);
        confirmBtn = findViewById(R.id.confirm_button);
        displayMainCheck = findViewById(R.id.checkBox);

        // initialize camera
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPrevew(cameraProvider);
            } catch (ExecutionException | InterruptedException e){
                Log.d("CAMERA", "Unable to open camera");
            }
        }, ContextCompat.getMainExecutor(this));

        captureBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void onClick(View v) {
                capturedState();
                // show captured image
                // take picture
                imageCapture.takePicture(Executors.newSingleThreadExecutor(),
                        new ImageCapture.OnImageCapturedCallback() {
                            @Override
                            public void onCaptureSuccess(@NonNull ImageProxy image){
                                Log.d("CAMERA", "Photo captured");
                                confirmBtn.setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View v) {
                                        StorageReference ref = storageRef.child("images/" +
                                                UUID.randomUUID().toString());
                                        Bitmap imageBitmap = image.toBitmap();
                                        String path = MediaStore.Images.Media.insertImage(getBaseContext().getContentResolver(),
                                                imageBitmap, "newpic", null);
                                        ref.putFile(Uri.parse(path))
                                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                    @Override
                                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                        Toast.makeText(CameraActivity.this,
                                                                "Upload successful",
                                                                Toast.LENGTH_SHORT).show();
                                                        Log.d("CAMERA", ref.getPath());
                                                        Intent newIntent = new Intent();
                                                        newIntent.putExtra("imagePath", ref.getPath());
                                                        setResult(Activity.RESULT_OK, newIntent);
                                                        Log.d("ADDPHOTO", "data from camera intent: " + newIntent.getStringExtra("imagePath"));
                                                        finish();
                                                    }
                                                })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Toast.makeText(CameraActivity.this,
                                                                        "Upload failed",
                                                                        Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                        finish();
                                    }
                                });
                                retakeBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        image.close();
                                        uncapturedState();
                                    }
                                });
                            }

                            @Override
                            public void onError(@NonNull ImageCaptureException exception) {
                                Toast.makeText(CameraActivity.this, "Error taking picture", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

    }

    private void bindPrevew(@NonNull ProcessCameraProvider cameraProvider){
        Preview preview = new Preview.Builder().build();
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK).build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        imageCapture = new ImageCapture.Builder().build();

        camera = cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, preview, imageCapture);
            // bindToLifeCycle returns Camera object
    }

    /***
     * show capture button
     */
    private void uncapturedState(){
        confirmBtn.setVisibility(View.GONE);
        displayMainCheck.setVisibility(View.GONE);
        retakeBtn.setVisibility(View.GONE);
        captureBtn.setVisibility(View.VISIBLE);
    }

    /***
     * show confirm button
     * show retake button
     * show option to display in list checkbox (default: unselected)
     */
    private void capturedState(){
        confirmBtn.setVisibility(View.VISIBLE);
        displayMainCheck.setVisibility(View.VISIBLE);
        retakeBtn.setVisibility(View.VISIBLE);
        captureBtn.setVisibility(View.GONE);
    }

    private void cameraPermissions(){
        /*if (ContextCompat.checkSelfPermission(CameraActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(CameraActivity.this,
                    new String[] {Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_CODE);
        }*/
    }

}
