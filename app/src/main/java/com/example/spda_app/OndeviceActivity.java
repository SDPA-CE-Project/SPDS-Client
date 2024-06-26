package com.example.spda_app;

import static org.tensorflow.lite.DataType.FLOAT32;

import android.Manifest;
import android.content.Intent;
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
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.data.CombinedData;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.google.mlkit.vision.objects.ObjectDetector;
import com.google.mlkit.vision.objects.custom.CustomObjectDetectorOptions;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;


import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

public class OndeviceActivity extends AppCompatActivity implements View.OnClickListener{
    private PreviewView previewView;
    private GraphicOverlay graphicOverlay;
    private ImageView imgView;
    private ExecutorService cameraExecutor;
    private Button btnLogout;
    private FaceDetector faceDetector;
    private TextView txtLeftEAR, txtRightEAR, txtAvgEAR, txtMar, txtSleepCount, txtBlinkCount, txtBlinkAvg, txtCloseTimeAvg, txtAlarmLevel, txtNoseMouthRatio;
    private Button toggleButton;
    private LineChart lineChart, totalChart;
    private LineData lineData, totalData;
    private ArrayList<Entry> eyesChartDataList, nodChartDataList, totalChartDataList;
    private LineDataSet eyesLineDataSet, nodLineDataSet,totalChartDataSet;



    private static final String TAG = "onDeviceTest";
    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private static final String model_1 = "FL16_default.tflite";
    private static final String model_2 = "model.tflite";
    private static final String model_3 = "upgraded_model_quantizated_dynamic.tflite";
    private static final String model_4 = "upgraded_model_quantizated_f16.tflite";
    private Interpreter interpreter;

    private boolean debugTextVisible = true;
    private double NMRatio = 0;
    private int lowerHead = 0;

    private int sleepCount = 0;

    private float eyeMultiplier = 1.0f;
    private float angleMultiplier = 1.0f;
    private int totalSleepCount;
    private float closeTimeAvg = 0;
    private float blinkAvg = 0;
    private PlaySong playSong;
    private PlayMedia playMedia;
    private PlayVibrate playVibrate;
    private static final String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.CAMERA
    };
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;

    private final boolean returnBiggest = false;
    private CustomObjectDetectorOptions customObjectDetectorOptions;
    private ObjectDetector objectDetector;
    private int blinkCountPer10s = 0;
    private int blinkCount = 0;
    private FirebaseAuth mAuth;

    //BackgroundTreadTime threadTime = new BackgroundTreadTime();

    BlinkCountThread blinkCountThread = new BlinkCountThread();
    DetectDrowzThread detectDrowzThread = new DetectDrowzThread();
    PlayAlarmThread alarmThread = new PlayAlarmThread(this);



    private void ChartInit()
    {
        lineChart.getAxisLeft().setAxisMinimum(0f);
        lineChart.getAxisLeft().setAxisMaximum(1.8f);
        LimitLine limitLine = new LimitLine(0.3f, "Blink Threshold");
        limitLine.setLineWidth(2f);
        limitLine.setLineColor(android.graphics.Color.RED);
        limitLine.enableDashedLine(10f, 10f, 0f);
        lineChart.getAxisLeft().addLimitLine(limitLine);

        limitLine = new LimitLine(1.2f, "Nod Threshold");
        limitLine.setLineWidth(2f);
        limitLine.setLineColor(android.graphics.Color.BLUE);
        limitLine.enableDashedLine(10f, 10f, 0f);
        lineChart.getAxisLeft().addLimitLine(limitLine);
        lineChart.getAxisRight().setEnabled(false);
        totalChart.getAxisRight().setEnabled(false);
        totalChart.getAxisLeft().setAxisMaximum(0f);
        totalChart.getAxisLeft().setAxisMaximum(500);

        limitLine = new LimitLine(150f, "level 1");
        limitLine.setLineColor(Color.YELLOW);
        limitLine.enableDashedLine(10f, 10f, 0f);
        totalChart.getAxisLeft().addLimitLine(limitLine);
        limitLine = new LimitLine(300f, "level 2");
        limitLine.setLineColor(Color.rgb(255, 165, 0));//Orange color
        limitLine.enableDashedLine(10f, 10f, 0f);
        totalChart.getAxisLeft().addLimitLine(limitLine);
        limitLine = new LimitLine(450f, "level 3");
        limitLine.setLineColor(Color.RED);
        limitLine.enableDashedLine(10f, 10f, 0f);
        totalChart.getAxisLeft().addLimitLine(limitLine);
    }


    private int GetTotalSleepCount()
    {
        if(returnBiggest)
        {
            return Math.max((int) (eyeMultiplier * sleepCount), (int) (lowerHead * angleMultiplier));
        }
        else
        {
            return (int)(eyeMultiplier * sleepCount)+(int)(lowerHead * angleMultiplier);
        }

    }


    private  void updateChart(float newEyeValue, float newNodValue)
    {
       // Log.i(TAG, "updateChart: "+ newEyeValue + '/' + newNodValue);


       if(eyesChartDataList.size() > 15)
       {
           eyesChartDataList.remove(0);
       }

        for (Entry e: eyesChartDataList) {
            e.setX(eyesChartDataList.indexOf(e));
        }
        eyesChartDataList.add(new Entry(eyesChartDataList.size(),newEyeValue));

        if(nodChartDataList.size() > 15)
        {
            nodChartDataList.remove(0);
        }
        for(Entry e: nodChartDataList)
        {
            e.setX(nodChartDataList.indexOf(e));
        }
        nodChartDataList.add(new Entry(nodChartDataList.size(),newNodValue));

        eyesLineDataSet.notifyDataSetChanged();
        nodLineDataSet.notifyDataSetChanged();

        lineData.notifyDataChanged();
        lineChart.notifyDataSetChanged();




        lineChart.invalidate();






        if(totalChartDataList.size() > 15)
        {
            totalChartDataList.remove(0);
        }
        for (Entry e: totalChartDataList) {
            e.setX(totalChartDataList.indexOf(e));
        }
        totalChartDataList.add(new Entry(totalChartDataList.size(),GetTotalSleepCount()));

        totalChartDataSet.notifyDataSetChanged();
        totalData.notifyDataChanged();
        totalChart.notifyDataSetChanged();
        totalChart.invalidate();
    }
    private  void toggleDebugingTextVisiblity()
    {
        debugTextVisible = !debugTextVisible;


        if(debugTextVisible)
        {
            imgView.setVisibility(View.VISIBLE);
            txtLeftEAR.setVisibility(View.VISIBLE);
            txtRightEAR.setVisibility(View.VISIBLE);
            txtAvgEAR.setVisibility(View.VISIBLE);
            txtMar.setVisibility(View.VISIBLE);
            txtSleepCount.setVisibility(View.VISIBLE);
            txtBlinkCount.setVisibility(View.VISIBLE);
            txtBlinkAvg.setVisibility(View.VISIBLE);
            txtCloseTimeAvg.setVisibility(View.VISIBLE);
            txtAlarmLevel.setVisibility(View.VISIBLE);
            txtNoseMouthRatio.setVisibility(View.VISIBLE);
            graphicOverlay.setVisibility(View.VISIBLE);
        }
        else
        {
            imgView.setVisibility(View.GONE);
            txtLeftEAR.setVisibility(View.GONE);
            txtRightEAR.setVisibility(View.GONE);
            txtAvgEAR.setVisibility(View.GONE);
            txtMar.setVisibility(View.GONE);
            txtSleepCount.setVisibility(View.GONE);
            txtBlinkCount.setVisibility(View.GONE);
            txtBlinkAvg.setVisibility(View.GONE);
            txtCloseTimeAvg.setVisibility(View.GONE);
            txtAlarmLevel.setVisibility(View.GONE);
            txtNoseMouthRatio.setVisibility(View.GONE);
            graphicOverlay.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ondevice);
        btnLogout = findViewById(R.id.btnLogout);
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

        txtNoseMouthRatio = findViewById(R.id.txtNMRatio);
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        txtAlarmLevel = findViewById(R.id.txtDrozeWarn);
        lineChart = findViewById(R.id.lineChart);
        totalChart = findViewById(R.id.totallineChart);
        playSong = new PlaySong(this);
        playMedia = new PlayMedia(this);
        playVibrate = new PlayVibrate(this);


        totalChartDataList = new ArrayList<Entry>();
        eyesChartDataList = new ArrayList<Entry>();
        nodChartDataList = new ArrayList<Entry>();
//        playSong = new PlaySong(this);
        playMedia = new PlayMedia(this);
        toggleButton = findViewById(R.id.debugingToggle);
        ChartInit();



        // Logout 전용 firebase 연동
        mAuth = FirebaseAuth.getInstance();

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
        lineData = new LineData();
        //combinedData = new CombinedData();


        eyesLineDataSet = new LineDataSet(eyesChartDataList, "eyes");
        eyesLineDataSet.setColor(Color.RED);
        eyesLineDataSet.setCircleColor(Color.RED);
        nodLineDataSet = new LineDataSet(nodChartDataList, "nod");
        lineData.addDataSet(eyesLineDataSet);
        lineData.addDataSet(nodLineDataSet);


        totalData = new LineData();
        totalChartDataSet = new LineDataSet(totalChartDataList,"totalSleep");
        totalChartDataSet.setColor(Color.GREEN);
        totalChartDataSet.setCircleColor(Color.GREEN);
        totalData.addDataSet(totalChartDataSet);

        //combinedData.addDataSet(eyesLineDataSet);
        //combinedData.addDataSet(nodLineDataSet);


        //totalChart.setData( new LineDataSet(totalChartDataList,"total"));

        lineChart.setData(lineData);
        totalChart.setData(totalData);
        totalChart.getDescription().setEnabled(false);
        totalChart.getLegend().setForm(Legend.LegendForm.LINE);
        lineChart.getDescription().setEnabled(false);
        Legend legend = lineChart.getLegend();
        legend.setForm(Legend.LegendForm.LINE);

        toggleButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                toggleDebugingTextVisiblity();
            }
        });

        toggleDebugingTextVisiblity();
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClick(view);
            }
        });

    }
    public void onClick(View view){
        mAuth.signOut();
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "User Logout: No user is logged in.");
            Intent intent = new Intent(OndeviceActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Optional: Close the current activity or redirect to a login screen
        }
        else {
            Toast.makeText(this, "Logout failed", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "User Logout: " + mAuth.getCurrentUser().toString());
        }
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
                        NMRatio = landmark.noseMouthDistanceRatio();
                        txtNoseMouthRatio.setText(String.format("%.4f", NMRatio));
                        detectDrowzThread.setAvg(avg);
                        imgView.setImageBitmap(croppedFace);
                        //imgView.setVisibility(View.VISIBLE);
                        updateChart(avg,(float)NMRatio);
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

        private void headAngleDetect()
        {
            if(NMRatio > 1.2f)  //고개 내림 감지, 해당 임계 값은 모델 개선 테스트 후 수정 되어야 하거나 사용자 마다 다르게 해야할 필요성이 있음
            {
                lowerHead += 1;
            }
            else {
                lowerHead -= 1;
                if(lowerHead > 50)  //위험 단계 일때 고개가 정면을 볼 경우 더 빨리 경고에서 빠져 나오도록 해줌
                    lowerHead -= 1;
                if(lowerHead < 0)
                    lowerHead = 0;
            }
        }

        @Override
        public void run() {
            while (running) {

                //updateChart();
                headAngleDetect();




                if (avg < 0.3f && sleepCount < 500) { //눈 0.3 미만 sleepCount 증가, 눈 감음 확인
                    sleepCount += 2;
                    if(!blinkCheck.get()) {
                        blinkCheck.set(true);
                        blinkCountThread.recordCount();
                    }
                }
                else if (avg < 0.6f && sleepCount < 500) { //눈 0.6 미만 sleepCount 증가
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
        if(GetTotalSleepCount() > 450){
            //알람 3단계
            txtAlarmLevel.setText(getString(R.string.level_3));
            playSong.stopSound();
            playVibrate.playVibrate();
        }
        else if((blinkCountPer10s > (blinkAvg*2) && timeCount > 6) || GetTotalSleepCount() > 300) {
            //알람 2단계
            txtAlarmLevel.setText(getString(R.string.level_2));
            playSong.playMusic();
            playMedia.stopMusic();
            playVibrate.stopVibration();
        }
        else if ((blinkCountPer10s > (blinkAvg*1.5) && timeCount > 6 && !playSong.isPlaying()) || GetTotalSleepCount() > 150) {
            //알람 1단계
            txtAlarmLevel.setText(getString(R.string.level_1));
            playSong.stopSound();
            playMedia.playMusic();
            playVibrate.stopVibration();
        }
        else {
            txtAlarmLevel.setText(getString(R.string.standby));
            playMedia.stopMusic();
            playSong.stopSound();
            playVibrate.stopVibration();
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