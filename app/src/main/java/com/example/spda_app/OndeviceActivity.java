package com.example.spda_app;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.LifecycleCameraController;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.camera.mlkit.vision.MlKitAnalyzer;

import com.example.spda_app.face_detect.DrawFace;
import com.example.spda_app.face_detect.Metadata;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.util.Log;
import android.widget.Toast;

public class OndeviceActivity extends AppCompatActivity {
    PreviewView previewView;

    ExecutorService cameraExecutor;
    FaceDetector faceDetector;
    String TAG = "onDeviceTest";
    private static final int REQUEST_CODE_PERMISSIONS = 10;



    private static final String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.CAMERA
    };
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;


    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ondevice);
        previewView = findViewById(R.id.vw_Preview);

        cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        if (allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(
            this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            );
        }

        cameraExecutor = Executors.newSingleThreadExecutor();

    }

    private void startCamera() {
        LifecycleCameraController cameraController = new LifecycleCameraController(getBaseContext());
        FaceDetectorOptions faceNoneOpt = new FaceDetectorOptions.Builder().
                                                build();
        faceDetector = FaceDetection.getClient(faceNoneOpt);

        Log.d(TAG, "start cameraController");

        cameraController.setImageAnalysisAnalyzer(ContextCompat.getMainExecutor(this),
                new MlKitAnalyzer(List.of(faceDetector), 1, ContextCompat.getMainExecutor(this), result -> {
                    Log.d(TAG, "analyze complete");
                    List<Face> faceResult = result.getValue(faceDetector);
                    if (faceResult == null || faceResult.isEmpty()) {
                        previewView.getOverlay().clear();
                    } else {
                        Metadata metadata = new Metadata(faceResult.get(0));
                        DrawFace drawFace = new DrawFace(metadata);
                        Log.d(TAG, "face res: " + faceResult.get(0));

                        previewView.getOverlay().clear();

                        previewView.getOverlay().add(drawFace);
                    }
                }));
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                .build();
        cameraController.setCameraSelector(cameraSelector);
        cameraController.bindToLifecycle(this);

        previewView.setController(cameraController);

    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(getBaseContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
        faceDetector.close();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                Toast.makeText(this,
                        "Permissions not granted by the user.",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}