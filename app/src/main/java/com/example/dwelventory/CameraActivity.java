package com.example.dwelventory;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;

public class CameraActivity extends AppCompatActivity {
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    PreviewView previewView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_layout);

        previewView = findViewById(R.id.preview_view);

        cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        ImageCapture imageCapture =
                new ImageCapture.Builder().build();

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPrevew(cameraProvider);
            } catch (ExecutionException | InterruptedException e){
            }
        }, ContextCompat.getMainExecutor(this));

    }

    void bindPrevew(@NonNull ProcessCameraProvider cameraProvider){
        Preview preview = new Preview.Builder().build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK).build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        androidx.camera.core.Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner)this,
                cameraSelector, preview); // bindToLifeCycle returns Camera object
    }
}
