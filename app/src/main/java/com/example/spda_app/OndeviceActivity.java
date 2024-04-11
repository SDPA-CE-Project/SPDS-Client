package com.example.spda_app;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
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
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;


public class OndeviceActivity extends AppCompatActivity {
    PreviewView previewView;
    ImageView imgView;
    ExecutorService cameraExecutor;
    FaceDetector faceDetector;
    String TAG = "onDeviceTest";
    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private static final String model = "FL16_default.tflite";



    private static final String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.CAMERA
    };
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ondevice);
        previewView = findViewById(R.id.vw_Preview);
        imgView = findViewById(R.id.imgview);

        cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        if (allPermissionsGranted()) {
            startDetect();
        } else {
            ActivityCompat.requestPermissions(
            this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            );
        }

        cameraExecutor = Executors.newSingleThreadExecutor();

    }

    private void startDetect() {
        LifecycleCameraController cameraController = new LifecycleCameraController(getBaseContext());
        FaceDetectorOptions faceNoneOpt = new FaceDetectorOptions.Builder().
                                                build();
        faceDetector = FaceDetection.getClient(faceNoneOpt);



        cameraController.setImageAnalysisAnalyzer(ContextCompat.getMainExecutor(this),
                new MlKitAnalyzer(List.of(faceDetector), 1, ContextCompat.getMainExecutor(this), result -> {
                    Log.d(TAG, "analyze complete");
                    List<Face> faceResult = result.getValue(faceDetector);
                    if (faceResult == null || faceResult.isEmpty()) {
                        previewView.getOverlay().clear();
                    } else {
                        Bitmap fullImage = previewView.getBitmap();
                        Metadata metadata = new Metadata(faceResult.get(0));
                        DrawFace drawFace = new DrawFace(metadata);
                        Log.d(TAG, "face res: " + faceResult.get(0));


                        Bitmap croppedFace = cropFaceResize(fullImage, faceResult.get(0).getBoundingBox());


                        previewView.getOverlay().clear();
                        previewView.getOverlay().add(drawFace);
                        imgView.setImageBitmap(croppedFace);
                        imgView.setVisibility(View.VISIBLE);
                    }
                }));
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                .build();
        cameraController.setCameraSelector(cameraSelector);
        cameraController.bindToLifecycle(this);

        previewView.setController(cameraController);

    }

    private Bitmap cropFaceResize(Bitmap fullImage, Rect boundingBox) {
        int width = fullImage.getWidth();
        int height = fullImage.getHeight();

        // 얼굴 영역의 좌표
        int left = boundingBox.left;
        int top = boundingBox.top;
        int right = boundingBox.right;
        int bottom = boundingBox.bottom;

        left = Math.max(left, 0);
        top = Math.max(top, 0);
        right = Math.min(right, width);
        bottom = Math.min(bottom, height);

        Bitmap faceBitmap = Bitmap.createBitmap(fullImage, left, top, right - left, bottom - top);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(faceBitmap, 256, 256, true);
        faceBitmap.recycle();

        return resizedBitmap;
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
    protected void onPause() {
        super.onPause();

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
                startDetect();
            } else {
                Toast.makeText(this,
                        "Permissions not granted by the user.",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}