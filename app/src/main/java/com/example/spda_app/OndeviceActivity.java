package com.example.spda_app;

import static org.tensorflow.lite.DataType.FLOAT32;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
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

import com.example.spda_app.face_detect.DrawLandmark;
import com.example.spda_app.face_detect.DrawLandmarkGraphic;
import com.example.spda_app.face_detect.DrawOverlay;
import com.example.spda_app.face_detect.GraphicOverlay;
import com.example.spda_app.face_detect.LandmarkData;
import com.example.spda_app.face_detect.Metadata;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.common.model.LocalModel;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.google.mlkit.vision.objects.DetectedObject;
import com.google.mlkit.vision.objects.ObjectDetection;
import com.google.mlkit.vision.objects.ObjectDetector;
import com.google.mlkit.vision.objects.custom.CustomObjectDetectorOptions;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.label.TensorLabel;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;


public class OndeviceActivity extends AppCompatActivity {
    PreviewView previewView;
    GraphicOverlay graphicOverlay;
    ImageView imgView;
    ExecutorService cameraExecutor;
    FaceDetector faceDetector;
    String TAG = "onDeviceTest";
    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private static final String model_1 = "FL16_default.tflite";
    private static final String model_2 = "model.tflite";

    private Interpreter interpreter;

    private static final String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.CAMERA
    };
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;

    private CustomObjectDetectorOptions customObjectDetectorOptions;
    private ObjectDetector objectDetector;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ondevice);
        previewView = findViewById(R.id.vw_Preview);
        imgView = findViewById(R.id.imgview);
        graphicOverlay = findViewById(R.id.vw_overlay);
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        try {
            interpreter = new Interpreter(loadModelFile(model_2));
        } catch (IOException e) {
            e.getMessage();
            throw new RuntimeException(e);
        }

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
                    List<Face> faceResult = result.getValue(faceDetector);
                    if (faceResult == null || faceResult.isEmpty()) {
                        previewView.getOverlay().clear();
                        graphicOverlay.clear();
                    } else {
                        Bitmap fullImage = previewView.getBitmap();
                        Metadata metadata = new Metadata(faceResult.get(0));
                        DrawOverlay drawOverlay = new DrawOverlay(metadata);
                        Log.d(TAG, "face res: " + faceResult.get(0));


                        Bitmap croppedFace = cropFaceResize(fullImage, faceResult.get(0).getBoundingBox());
                        TensorImage inputImageBuffer = new TensorImage(FLOAT32);
                        inputImageBuffer.load(croppedFace);


                        //TensorBuffer outputBuffer = TensorBuffer.createFixedSize(new int[]{1, 64, 64, 68}, FLOAT32);
                        TensorBuffer outputBuffer2 = TensorBuffer.createFixedSize(new int[]{1, 136}, FLOAT32);
                        interpreter.run(inputImageBuffer.getBuffer(), outputBuffer2.getBuffer());

                        float[] flatArray = (outputBuffer2.getFloatArray());

                        //Log.d(TAG, Arrays.toString(flatArray));
                        int[] resultArr = new int[136];
                        for (int i = 0; i < flatArray.length; i++) {
                            resultArr[i] = (int)(flatArray[i]*256);
                        }
                        Log.d(TAG, Arrays.toString(resultArr));
                        LandmarkData landmark = new LandmarkData(resultArr, metadata);

//                        float[][][][] outputArray = new float[1][64][64][68];
//                        int index = 0;
//                        for (int i = 0; i < outputArray.length; i++) {
//                            for (int j = 0; j < outputArray[i].length; j++) {
//                                for (int k = 0; k < outputArray[i][j].length; k++) {
//                                    for (int l = 0; l < outputArray[i][j][k].length; l++) {
//                                        outputArray[i][j][k][l] = flatArray[index++];
//                                    }
//                                }
//                            }
//                        }
//                        int[][] outputArray = new int[1][136];
//                        int index = 0;
//                        for (int i = 0; i < outputArray.length; i++) {
//                            for (int j = 0; j < outputArray[i].length; j++) {
//                                outputArray[i][j] = (int) (flatArray[index++] * 256);
//                            }
//                        }


                        //Log.d(TAG, "Output array: " + Arrays.deepToString(outputArray));
                        DrawLandmark drawLandmark = new DrawLandmark(landmark, metadata);

                        previewView.getOverlay().clear();
                        graphicOverlay.clear();
                        previewView.getOverlay().add(drawOverlay);
                        //previewView.getOverlay().add(drawLandmark);

                        graphicOverlay.add(new DrawLandmarkGraphic(graphicOverlay, landmark));
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
        
        int left = boundingBox.left;
        int top = boundingBox.top;
        int right = boundingBox.right;
        int bottom = boundingBox.bottom;

        left = Math.max(left, 0);
        top = Math.max(top, 0);
        right = Math.min(right, width);
        bottom = Math.min(bottom, height);

        int faceWidth = right - left;
        int faceHeight = bottom - top;

        float scaleFactor = 1.5f;

        int expandedWidth = (int) (faceWidth * scaleFactor);
        int expandedHeight = (int) (faceHeight * scaleFactor);

        int faceCenterX = (left + right) / 2;
        int faceCenterY = (top + bottom) / 2;

        int expandedLeft = Math.max(faceCenterX - expandedWidth / 2, 0);
        int expandedTop = Math.max(faceCenterY - expandedHeight / 2, 0);
        int expandedRight = Math.min(faceCenterX + expandedWidth / 2, width);
        int expandedBottom = Math.min(faceCenterY + expandedHeight / 2, height);

        Bitmap faceBitmap = Bitmap.createBitmap(fullImage,
                expandedLeft,
                expandedTop,
                expandedRight - expandedLeft,
                expandedBottom - expandedTop);

        Bitmap resizedBitmap = Bitmap.createScaledBitmap(faceBitmap, 256, 256, true);

        faceBitmap.recycle();

        return resizedBitmap;
    }

    ByteBuffer inputBuffer(Bitmap bitmap) {
        ByteBuffer input = ByteBuffer.allocateDirect(256 * 256 * 3 * 4).order(ByteOrder.nativeOrder());
        for (int y = 0; y < 256; y++) {
            for (int x = 0; x < 256; x++) {
                int px = bitmap.getPixel(x, y);

                // Get channel values from the pixel value.
                int r = Color.red(px);
                int g = Color.green(px);
                int b = Color.blue(px);

                // Normalize channel values to [0.0, 1.0].
                float rf = r / 255.0f;
                float gf = g / 255.0f;
                float bf = b / 255.0f;

                input.putFloat(rf);
                input.putFloat(gf);
                input.putFloat(bf);
            }
        }
        return input;
    }

//    private Interpreter getInterpreter(String path) {
//        try {
//            return new Interpreter(loadModelFile(this, path));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//    private MappedByteBuffer loadModelFile(Activity activity, String path) throws IOException {
//        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd(path);
//        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
//        FileChannel fileChannel = inputStream.getChannel();
//        long startOffset = fileDescriptor.getStartOffset();
//        long declaredLength = fileDescriptor.getDeclaredLength();
//        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
//    }
    private MappedByteBuffer loadModelFile(String path) throws IOException {
        AssetManager assetManager = getAssets();
        AssetFileDescriptor fileDescriptor = assetManager.openFd(path);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
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