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
import androidx.annotation.NonNull;
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

import com.example.spda_app.threads.PlayAlarmThread;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.google.mlkit.vision.objects.ObjectDetector;
import com.google.mlkit.vision.objects.custom.CustomObjectDetectorOptions;

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
import java.util.concurrent.atomic.AtomicBoolean;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;


public class OndeviceActivity extends AppCompatActivity {
    private PreviewView previewView;
    private GraphicOverlay graphicOverlay;
    private ImageView imgView;
    private ExecutorService cameraExecutor;
    private FaceDetector faceDetector;
    private TextView txtLeftEAR, txtRightEAR, txtAvgEAR, txtMar, txtSleepCount, txtBlinkCount, txtBlinkAvg, txtCloseTimeAvg, txtAlarmLevel;
    private static final String TAG = "onDeviceTest";
    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private static final String model_1 = "FL16_default.tflite";
    private static final String model_2 = "model.tflite";
    private static final String model_3 = "upgraded_model_quantizated_dynamic.tflite";
    private static final String model_4 = "upgraded_model_quantizated_f16.tflite";
    private Interpreter interpreter;
    private int sleepCount = 0;
    private float closeTimeAvg = 0;
    private float blinkAvg = 0;
    private PlaySong playSong;
    private PlayMedia playMedia;
    private static final String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.CAMERA
    };
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;

    private CustomObjectDetectorOptions customObjectDetectorOptions;
    private ObjectDetector objectDetector;
    private int blinkCountPer10s = 0;
    private int blinkCount = 0;
    BlinkCountThread blinkCountThread = new BlinkCountThread();
    DetectDrowzThread detectDrowzThread = new DetectDrowzThread();
    PlayAlarmThread alarmThread = new PlayAlarmThread(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ondevice);
        previewView = findViewById(R.id.vw_Preview);
        imgView = findViewById(R.id.imgview);
        graphicOverlay = findViewById(R.id.vw_overlay);
        txtLeftEAR = findViewById(R.id.txtLeftEAR);
        txtRightEAR = findViewById(R.id.txtRightEAR);
        txtAvgEAR = findViewById(R.id.txtAvgEAR);
        txtMar = findViewById(R.id.txtMAR);
        txtSleepCount = findViewById(R.id.txtStatCount);
        txtBlinkCount = findViewById(R.id.txtBlinkCount);
        txtBlinkAvg = findViewById(R.id.txtBlinkAvg);
        txtCloseTimeAvg = findViewById(R.id.txtCloseTimeAvg);
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        txtAlarmLevel = findViewById(R.id.txtDrozeWarn);

//        playSong = new PlaySong(this);
        playMedia = new PlayMedia(this);

        try {
            interpreter = new Interpreter(loadModelFile(model_4));
        } catch (IOException e) {
            e.getMessage();
            throw new RuntimeException(e);
        }

        if (allPermissionsGranted()) {
            startDetect();
            blinkCountThread.start();
            detectDrowzThread.start();
            blinkCountThread.setThread();
            detectDrowzThread.setThread();

        } else {
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
                        sleepCount = 0;

                    } else {
                        Bitmap fullImage = previewView.getBitmap();
                        Metadata metadata = new Metadata(faceResult.get(0));
                        DrawOverlay drawOverlay = new DrawOverlay(metadata);
                        Log.d(TAG, "face res: " + faceResult.get(0));


                        Bitmap croppedFace = cropFaceResize(fullImage, faceResult.get(0).getBoundingBox());
                        TensorImage inputImageBuffer = new TensorImage(FLOAT32);
                        inputImageBuffer.load(croppedFace);

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
                        float leftEye = faceResult.get(0).getLeftEyeOpenProbability().floatValue();
                        float rightEye = faceResult.get(0).getRightEyeOpenProbability().floatValue();
                        float avg = (leftEye + rightEye)/2;
                        previewView.getOverlay().clear();
                        graphicOverlay.clear();
                        previewView.getOverlay().add(drawOverlay);
                        //previewView.getOverlay().add(drawLandmark);

                        graphicOverlay.add(new DrawLandmarkGraphic(graphicOverlay, landmark));
//                        txtLeftEAR.setText(String.format("%.4f", landmark.earLeft()));
//                        txtRightEAR.setText(String.format("%.4f", landmark.earRight()));
//                        txtAvgEAR.setText(String.format("%.4f", landmark.earAvg()));
                        txtLeftEAR.setText(String.format("%.4f", leftEye));
                        txtRightEAR.setText(String.format("%.4f", rightEye));
                        txtAvgEAR.setText(String.format("%.4f", avg));
                        txtMar.setText(String.format("%.4f", landmark.marAvg()));

                        txtSleepCount.setText(getString(R.string.sleepStat, sleepCount));
                        txtBlinkCount.setText(getString(R.string.blinkCount, blinkCount, blinkCountPer10s));
                        txtBlinkAvg.setText(getString(R.string.blinkAvg, blinkAvg));
                        txtCloseTimeAvg.setText(getString(R.string.closeTime, closeTimeAvg));

                        detectDrowzThread.setAvg(avg);
                        imgView.setImageBitmap(croppedFace);
                        imgView.setVisibility(View.VISIBLE);
                    }
                    sleepAlarm();
                }));
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                .build();
        cameraController.setCameraSelector(cameraSelector);
        cameraController.bindToLifecycle(this);

        previewView.setController(cameraController);

    }

    private class BlinkCountThread extends Thread {
        private boolean running = false;
        private int timeCount = 0;
        private int blinkCountPer60s = 1;
        private int runCount = 0;
        public void setThread() {
            running = true;
        }
        public int getBlinkRunCount() {
            return timeCount;
        }
        public void stopThread() {
            running = false;
        }

        public void recordCount() {
            blinkCount++;
        }
        @Override
        public void run() {
            while (running) {
                try {
                    sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                blinkCountPer10s = blinkCount;
                blinkCount = 0;
                runCount++;
                if(runCount <= 6) {
                    blinkCountPer60s += blinkCountPer10s;
                    blinkAvg = (float) blinkCountPer60s / runCount;
                }
                else if(runCount == 30) {
                    runCount = 0;
                    blinkCountPer60s = (int) blinkAvg;
                }
            }
        }
    }
    private class DetectDrowzThread extends Thread {
        private boolean running = false;
        private int count = 0;
        private int blinkInterval = 0;
        AtomicBoolean blinkCheck = new AtomicBoolean(false);
        private float avg = 0;
        public void setThread() {
            running = true;
        }
        public void stopThread() {
            running = false;
        }
        public void setAvg(float avg) {
            this.avg = avg;
        }
        @Override
        public void run() {
            while (running) {
                if (avg < 0.3f && sleepCount < 200) { //눈 0.3 미만 sleepCount 증가, 눈 감음 확인
                    sleepCount += 2;
                    if(!blinkCheck.get()) {
                        blinkCheck.set(true);
                        blinkCountThread.recordCount();
                    }
                }
                else if (avg < 0.6f && sleepCount < 200) { //눈 0.6 미만 sleepCount 증가
                    sleepCount += 1;
                }
                else if (avg >= 0.7f) { // 눈 0.7 이상 눈 뜸 확인, 깜빡임 증가, 감은 시간 평균, sleepCount 초기화
                    blinkCheck.set(false);
                    count += 1;
                    closeTimeAvg = closeTimeAvg + (float)((float)(sleepCount-closeTimeAvg)/(count+1));
                    sleepCount = 0;
                }
                if (count > 200) {
                    count = 0;
                    closeTimeAvg = 1;
                }

                try {
                    sleep(50); //0.05sec
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void sleepAlarm() {
//        txtSleepCount.setText(getString(R.string.sleepStat, sleepCount));
        int timeCount = blinkCountThread.getBlinkRunCount();
        if(sleepCount > 150){
            //알람 3단계
            txtAlarmLevel.setText(getString(R.string.level_3));
        }
        else if((blinkCountPer10s > (blinkAvg*2) && timeCount > 6) || sleepCount > 100) {
            //알람 2단계
            txtAlarmLevel.setText(getString(R.string.level_2));
            playMedia.stopMusic();
        }
        else if ((blinkCountPer10s > (blinkAvg*1.5) && timeCount > 6 && !playSong.isPlaying()) || sleepCount > 50) {
            //알람 1단계
            txtAlarmLevel.setText(getString(R.string.level_1));
            playMedia.playMusic();
        }
        else {
            txtAlarmLevel.setText(getString(R.string.standby));
            playMedia.stopMusic();
        }
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
        blinkCountThread.stopThread();
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