package com.example.spda_app;

import static org.tensorflow.lite.DataType.FLOAT32;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.LifecycleCameraController;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.camera.mlkit.vision.MlKitAnalyzer;

import com.example.spda_app.face_detect.DrawLandmarkGraphic;
import com.example.spda_app.face_detect.DrawOverlay;
import com.example.spda_app.face_detect.GraphicOverlay;
import com.example.spda_app.face_detect.LandmarkData;
import com.example.spda_app.face_detect.Metadata;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

public class FacedetectActivity extends AppCompatActivity {
    private static final String model_5 = "FL2_gen2_MNv2_fp16.tflite";
    private static final String TAG = "FaceDetectTest";
    private static final String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.CAMERA
    };

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private TextView text_msg;
    private PreviewView previewView;
    private float cropoffsetx, cropoffsety;
    private ExecutorService cameraExecutor;
    private FaceDetector faceDetector;
    private Interpreter interpreter;
    private GraphicOverlay graphicOverlay;

    private Button next_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_facedetect);
        previewView = findViewById(R.id.vw_Preview);
        graphicOverlay = findViewById(R.id.vw_overlay);
        text_msg = findViewById(R.id.textView_msg);
        next_btn = findViewById(R.id.button_next);
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cropoffsetx = cropoffsety = 0;



        try {
            interpreter = new Interpreter(loadModelFile(model_5));
        } catch (IOException e) {
            e.getMessage();
            throw new RuntimeException(e);
        }

        if (allPermissionsGranted()) {
            startDetect();
        }
        else {
            ActivityCompat.requestPermissions(
                    this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            );
        }


        cameraExecutor = Executors.newSingleThreadExecutor();



    }


    private void startDetect() {

        
        
        LifecycleCameraController cameraController = new LifecycleCameraController(getBaseContext());

        FaceDetectorOptions faceNoneOpt = new FaceDetectorOptions.Builder()
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                .build();

        faceDetector = FaceDetection.getClient(faceNoneOpt);

        AtomicBoolean blinkCheck = new AtomicBoolean(false);

        cameraController.setImageAnalysisAnalyzer(ContextCompat.getMainExecutor(this),
                new MlKitAnalyzer(List.of(faceDetector), 1, ContextCompat.getMainExecutor(this), result -> {

                    List<Face> faceResult = result.getValue(faceDetector);

                    if (faceResult == null || faceResult.isEmpty()) {
                        previewView.getOverlay().clear();
                        graphicOverlay.clear();
                        text_msg.setText("사용자의 얼굴이 감지되고 있지 않습니다.");
                    } else {
                        Bitmap fullImage = previewView.getBitmap();
                        Metadata metadata = new Metadata(faceResult.get(0));
                        DrawOverlay drawOverlay = new DrawOverlay(metadata);

                        text_msg.setText("사용자의 얼굴이 잘 감지되고 있습니다.");
                        next_btn.setEnabled(true);
                        Bitmap croppedFace = cropFaceResize(fullImage, faceResult.get(0).getBoundingBox());
                        float[] normalizedFace = normalize(croppedFace);

                        TensorImage inputImageBuffer = new TensorImage(FLOAT32);
                        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(normalizedFace.length * 4);
                        byteBuffer.order(ByteOrder.nativeOrder());
                        FloatBuffer floatBuffer = byteBuffer.asFloatBuffer();
                        floatBuffer.put(normalizedFace);
                        //inputImageBuffer.load(floatBuffer);

                        TensorBuffer outputBuffer2 = TensorBuffer.createFixedSize(new int[]{1, 136}, FLOAT32);
                        interpreter.run(floatBuffer, outputBuffer2.getBuffer());

                        float[] flatArray = (outputBuffer2.getFloatArray());

                        //Log.d(TAG, Arrays.toString(flatArray));
                        int[] resultArr = new int[136];
                        for (int i = 0; i < flatArray.length; i++) {
                            resultArr[i] = (int)(flatArray[i]*previewView.getWidth()/2);
                            if(i%2 == 0)
                                resultArr[i] += cropoffsetx;
                            else
                                resultArr[i] += cropoffsety;
                        }
                        Log.d(TAG, Arrays.toString(resultArr));
                        LandmarkData landmark = new LandmarkData(resultArr, metadata);

                        previewView.getOverlay().clear();
                        graphicOverlay.clear();
                        previewView.getOverlay().add(drawOverlay);
                        //previewView.getOverlay().add(drawLandmark);

                        //graphicOverlay.add(new DrawLandmarkGraphic(graphicOverlay, landmark));

                    }
                }));
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                .build();
        cameraController.setCameraSelector(cameraSelector);
        cameraController.bindToLifecycle(this);

        previewView.setController(cameraController);

    }
    public static float[] normalize(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float[] normalizedPixels = new float[width * height * 3];
        float[] imgMean = {0.485f, 0.456f, 0.406f};
        float[] imgStd = {0.229f, 0.224f, 0.225f};

        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        for (int i = 0; i < pixels.length; i++) {
            int pixel = pixels[i];
            float r = Color.red(pixel) / 255.0f;
            float g = Color.green(pixel) / 255.0f;
            float b = Color.blue(pixel) / 255.0f;

            normalizedPixels[i * 3] = (r - imgMean[0]) / imgStd[0];
            normalizedPixels[i * 3 + 1] = (g - imgMean[1]) / imgStd[1];
            normalizedPixels[i * 3 + 2] = (b - imgMean[2]) / imgStd[2];
        }

        return normalizedPixels;
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

        float scaleFactor = 1.4f;
        float offsetX = faceWidth * 0;
        float offsetY = faceHeight * 0.13f;

        //int expandedWidth = (int) (faceWidth * scaleFactor);
        //int expandedHeight = (int) (faceHeight * scaleFactor);

        int faceCenterX = (left + right) / 2 + (int)offsetX;
        int faceCenterY = (top + bottom) / 2 + (int)offsetY;

        int margin = (int)((Math.max(faceWidth, faceHeight) * scaleFactor) / 2);

//        int expandedLeft = Math.max(faceCenterX - (expandedWidth / 2), 0);
//        int expandedTop = Math.max(faceCenterY - (expandedHeight / 2), 0);
//        int expandedRight = Math.min(faceCenterX + (expandedWidth / 2), width);
//        int expandedBottom = Math.min(faceCenterY + (expandedHeight / 2), height);

//        Bitmap faceBitmap = Bitmap.createBitmap(fullImage,
//                expandedLeft,
//                expandedTop,
//                expandedRight - expandedLeft,
//                expandedBottom - expandedTop);
        int expendedLeft = faceCenterX - margin;
        expendedLeft = Math.max(expendedLeft, 0);
        int expendedRight = faceCenterX + margin;
        expendedRight = Math.min(expendedRight, width);
        int expendedTop = faceCenterY - margin;
        expendedTop = Math.max(expendedTop, 0);
        int expendedBottom = faceCenterY + margin;
        expendedBottom = Math.min(expendedBottom, height);
        Bitmap faceBitmap = Bitmap.createBitmap(fullImage,
                expendedLeft, expendedTop,expendedRight - expendedLeft,expendedBottom - expendedTop);

        Bitmap resizedBitmap = Bitmap.createScaledBitmap(faceBitmap, 256, 256, true);

        faceBitmap.recycle();

        //cropoffsetx = (float)(faceCenterX - faceWidth)/2;
        //cropoffsety = (float)(faceCenterY - faceHeight)/2;

        return resizedBitmap;
    }
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
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
        faceDetector.close();
    }
}