package com.example.spda_app;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.spda_app.databinding.ActivityCameraBinding;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.pedro.encoder.input.video.CameraOpenException;
import com.pedro.library.rtsp.RtspCamera1;
import com.pedro.common.ConnectChecker;
import com.pedro.library.util.FpsListener;
import com.pedro.rtsp.rtsp.Protocol;

import java.io.File;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.os.Bundle;

import org.checkerframework.checker.units.qual.N;

public class CameraStreamActivity extends AppCompatActivity implements ConnectChecker, View.OnClickListener, SurfaceHolder.Callback{

    private ActivityCameraBinding binding;

    //뒤로가기 클릭 시 종료
    private long backpressedTime = 0;  // 뒤로가기 버튼 클릭한 시간
    private static final String TAG = "MainActivity";
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private RtspCamera1 rtspCamera1;
    private Button btnRecord, btnSwitch, btnAuth, btnDebug;
    private TextView txtValue, txtValue2, txtAvg, txtUser, txtChkConnReq, txtChkConnRes, txtUid, txtHash,
            txtBitrate, txtSleepLevel, txtSleepStat;
    private String currentDateAndTime = "";
    private SurfaceView surfaceView;
    private boolean isPlayingAudio = false;

    private DatabaseReference DBReference;
    //private DatabaseReference DBRefData;
    FirebaseDatabase database;
    private DBReset dbReset = new DBReset();


    NotificationManager manager;
    NotificationCompat.Builder builder;

    private static String CHANNEL_ID = "SDPS1";
    private static String CHANEL_NAME = "SDPS1";


    private final String[] PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private final String[] PERMISSIONS_A_13 = {
            Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA,
            Manifest.permission.POST_NOTIFICATIONS
    };

    private int dzLevelCount = 50;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCameraBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, 1);
        }

        //dbReset = (DBReset)getApplicationContext();

        surfaceView = findViewById(R.id.surfaceView);
        btnRecord = findViewById(R.id.btnRecord);
        btnSwitch = findViewById(R.id.btnSwitchCam);
        btnAuth = findViewById(R.id.btnAuth);
        btnDebug = findViewById(R.id.btnDebug);
        btnRecord.setOnClickListener(this);;
        btnSwitch.setOnClickListener(this);
        btnAuth.setOnClickListener(this);
        btnDebug.setOnClickListener(this);


        txtValue = findViewById(R.id.txtValue);
        txtValue2 = findViewById(R.id.txtValue2);
        txtAvg = findViewById(R.id.txtAvg);
        txtUser = findViewById(R.id.txtUser);
        txtChkConnReq = findViewById(R.id.txtChkConnReq);
        txtChkConnRes = findViewById(R.id.txtChkConnRes);
        txtUid = findViewById(R.id.txtUid);
        txtHash = findViewById(R.id.txtHash);
        txtBitrate = findViewById(R.id.txtBitrate);
        txtSleepLevel = findViewById(R.id.txtSleepLevel);
        txtSleepStat = findViewById(R.id.txtSleepStat);

        rtspCamera1 = new RtspCamera1(surfaceView, this);
        rtspCamera1.switchCamera();
        surfaceView.getHolder().addCallback(this);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DBReference = database.getReference();

        mAuth = FirebaseAuth.getInstance();

    }
    // 뒤로가기 버튼 처리
    @Override
    public void onBackPressed() {
        // 2초 이내에 뒤로가기 버튼을 재 클릭 시 앱 종료
        if (System.currentTimeMillis() - backpressedTime < 2000) {
            super.onBackPressed();
            finishAffinity();
            System.runFinalization();
            System.exit(0);
        } else {
            // backPressedTime에 '뒤로' 버튼이 눌린 시간을 기록
            backpressedTime = System.currentTimeMillis();
            // '뒤로' 버튼 한번 클릭 시 메시지
            Toast.makeText(this, "'뒤로' 버튼을 한번 더 누르시면 앱이 종료됩니다.", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        showNoti();
        currentUser = mAuth.getCurrentUser();
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference();

        if(currentUser != null) { //Signed in
            String uid = currentUser.getUid();
            txtUid.setText(getString(R.string.uid, uid));
            dbReset.resetUserDataDB(ref, currentUser, this);
            DatabaseReference refEvent = ref.child("data").child(uid).child("ratio");
            DatabaseReference refConnection = ref.child("users").child(uid);

            btnAuth.setText(R.string.signout);
            txtUser.setText(currentUser.getEmail());

            refEvent.addValueEventListener(new ValueEventListener() {
                @SuppressLint("DefaultLocale")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(rtspCamera1.isStreaming()) {
                        Value value = snapshot.getValue(Value.class);
                        // **********************************************************
                        // **********************************************************
                        // **********************************************************
                        // **********************************************************

                        Log.d(TAG, "Data changed left : " + value.left);
                        Log.d(TAG, "Data changed right : " + value.right);
                        Log.d(TAG, "Data changed AVG : " + value.avg);

                        txtValue.setText(String.format("%.4f", value.right));
                        txtValue2.setText(String.format("%.4f", value.left));
                        txtAvg.setText(String.format("%.2f", value.avg));

                        if (value.avg > 0.25 && dzLevelCount <= 50) {
                            dzLevelCount = dzLevelCount + 1;
                        }
                        else if (value.avg <= 0.25 && dzLevelCount >= -50){
                            dzLevelCount = dzLevelCount - 1;
                        }
                        if (isPlayingAudio) {
                            return;
                        }
                        if (dzLevelCount > 0) {
                            txtSleepLevel.setText(R.string.level_1);
                        }
                        else if (dzLevelCount > -30) {
                            txtSleepLevel.setText(R.string.level_2);
                            // 일단 2단계에서만 audio가 발생하도록 설정해두었음.
                            // 이미 다른 파일에서 테스트해봐서 문제는 없을것으로 예상됨.
                            if (!isPlayingAudio) {
                                isPlayingAudio = true;
                                playAudio1();
                            }
                        }
                        else {
                            txtSleepLevel.setText(R.string.level_3);
                            if (!isPlayingAudio) {
                                isPlayingAudio = true;
                                playAudio2();
                            }
                        }
                        txtSleepStat.setText(getString(R.string.sleepStat, dzLevelCount));
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d(TAG, "EventListener error", error.toException());
                }
            });
            refConnection.addValueEventListener(new ValueEventListener() {
                @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean req = Boolean.TRUE.equals(snapshot.child("chkConnectRequest").getValue(Boolean.TYPE));
                    boolean res = Boolean.TRUE.equals(snapshot.child("chkConnectResponse").getValue(Boolean.TYPE));
                    txtChkConnReq.setText(getString(R.string.ConnectionRequest, String.valueOf(req)));
                    txtChkConnRes.setText(getString(R.string.ConnectionResponse, String.valueOf(res)));
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d(TAG, "EventListener error", error.toException());
                }
            });
        } else { // Not Signed in
            btnAuth.setText(R.string.signin);
            txtUser.setText(R.string.noSignin);
            txtUid.setText(getString(R.string.uid, "idle"));
            txtHash.setText(getString(R.string.hash, "idle"));
            txtChkConnReq.setText(getString(R.string.ConnectionRequest, "idle"));
            txtChkConnRes.setText(getString(R.string.ConnectionResponse, "idle"));


        }
    }

    // 상단 알림창 현재 실행중 띄우기
    public void showNoti(){
        builder = null;
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // 버전이 오레오 이상일경우
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(
                    new NotificationChannel(CHANNEL_ID, CHANEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            );

            builder = new NotificationCompat.Builder(this, CHANNEL_ID);

            // 하위 버전일 경우
        }else{
            builder = new NotificationCompat.Builder(this);
        }
        // 알림창 제목
        builder.setContentTitle("SDPS 알림");
        // 알림창 메시지
        builder.setContentText("카메라가 동작 중 입니다");
        builder.setSmallIcon(R.drawable.baseline_info_24);

        Notification notification = builder.build();

        // 알림창 실행
        manager.notify(1,notification);
    }

    // playAudio --> 알람 발생을 위한 함수
    // 이후 다른 비슷한 형식의 다른 함수들을 추가해서 알람 발생 가능
    MediaPlayer player;
    public void playAudio1() {
        try {
            closePlayer();
            player = MediaPlayer.create(this, R.raw.test1);
            player.start();
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    closePlayer();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void playAudio2() {
        try {
            closePlayer();
            player = MediaPlayer.create(this, R.raw.test2);
            player.start();
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    closePlayer();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void closePlayer() {
        if (player != null) {
            player.release();
            player = null;
            isPlayingAudio = false;
        }
    }


    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        rtspCamera1.startPreview();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        rtspCamera1.startPreview();

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
        rtspCamera1.stopPreview();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference();

        if (id == R.id.btnRecord && currentUser != null) {
            if (!rtspCamera1.isStreaming()) {
                if (rtspCamera1.isRecording() || rtspCamera1.prepareAudio() && rtspCamera1.prepareVideo()) {
                    btnRecord.setText(R.string.stop);

                    rtspCamera1.setLimitFPSOnFly(20);
                    rtspCamera1.setVideoBitrateOnFly(10);
                    rtspCamera1.getStreamClient().setAuthorization(BuildConfig.rtsp_user, BuildConfig.rtsp_pass);
                    rtspCamera1.getStreamClient().setProtocol(Protocol.TCP);

                    String uid = currentUser.getUid();
                    MessageDigest md;
                    try {
                        md = MessageDigest.getInstance("SHA-256");
                    } catch (NoSuchAlgorithmException e) {
                        Toast.makeText(this, "Key Encryption has throw RuntimeException (NoSuchAlgorithmException)",
                                Toast.LENGTH_SHORT).show();
                        throw new RuntimeException(e);
                    }
                    md.update(uid.getBytes(StandardCharsets.UTF_8));
                    byte[] bytes = md.digest();
                    String hash = String.format("%64x", new BigInteger(1, bytes));
                    txtHash.setText(getString(R.string.hash, hash));
                    rtspCamera1.startStream("rtsp://"+BuildConfig.rtsp_url+"/live/"+hash);



                } else {
                    Toast.makeText(this, "Error preparing stream",
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                btnRecord.setText(R.string.start);
                rtspCamera1.stopStream();
                dbReset.resetConnectionFalse(ref, currentUser);
            }
        } else if (id == R.id.btnSwitchCam) {
            try {
                rtspCamera1.switchCamera();
            } catch (CameraOpenException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.btnAuth) {
            if (currentUser == null) {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
            }
            else {
                mAuth.signOut();
                Toast.makeText(CameraStreamActivity.this, "Sign Out", Toast.LENGTH_SHORT).show();
                currentUser = mAuth.getCurrentUser();

                btnAuth.setText(R.string.signin);
                txtUser.setText(R.string.noSignin);
            }

        } else if (id == R.id.btnDebug) {
            if(txtUid.getVisibility() == View.INVISIBLE) {
                txtUid.setVisibility(View.VISIBLE);
                txtHash.setVisibility(View.VISIBLE);
                txtChkConnReq.setVisibility(View.VISIBLE);
                txtChkConnRes.setVisibility(View.VISIBLE);
                txtBitrate.setVisibility(View.VISIBLE);
            } else {
                txtUid.setVisibility(View.INVISIBLE);
                txtHash.setVisibility(View.INVISIBLE);
                txtChkConnReq.setVisibility(View.INVISIBLE);
                txtChkConnRes.setVisibility(View.INVISIBLE);
                txtBitrate.setVisibility(View.INVISIBLE);
            }
        }
    }


    @Override
    public void onAuthError() {
        Toast.makeText(CameraStreamActivity.this, "Auth error", Toast.LENGTH_SHORT).show();
        rtspCamera1.stopStream();
        btnRecord.setText(R.string.start);

        dbReset.resetConnectionFalse(DBReference, currentUser);
    }

    @Override
    public void onAuthSuccess() {
        Toast.makeText(CameraStreamActivity.this, "Auth success", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull String s) {
        Toast.makeText(CameraStreamActivity.this, "Connection failed. " + s, Toast.LENGTH_SHORT)
                .show();
        rtspCamera1.stopStream();
        btnRecord.setText(R.string.start);

        dbReset.resetConnectionFalse(DBReference, currentUser);
    }

    @Override
    public void onConnectionStarted(@NonNull String s) {

    }

    @Override
    public void onConnectionSuccess() {
        Toast.makeText(CameraStreamActivity.this, "Connection success", Toast.LENGTH_SHORT).show();
        dbReset.resetConnectionTrue(DBReference, currentUser);
    }

    @Override
    public void onDisconnect() {
        Toast.makeText(CameraStreamActivity.this, "Disconnected", Toast.LENGTH_SHORT).show();
        dbReset.resetConnectionFalse(DBReference, currentUser);
    }

    @Override
    public void onNewBitrate(long bitrate) {txtBitrate.setText(getString(R.string.bitrate, bitrate));}
}

