package com.example.spda_app;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.spda_app.databinding.AlarmSettingBinding;

public class AlarmSettingActivity extends AppCompatActivity {

    private PermissionSupport permission;
    private AlarmSettingBinding binding1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding1 = AlarmSettingBinding.inflate(getLayoutInflater());
        setContentView(binding1.getRoot());
        //permissionCheck();

        // 노래 선택 레이아웃 클릭 이벤트
        binding1.songLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated smethod stub
                Intent intent = new Intent(v.getContext(), SongSettingActivity.class);
                startActivity(intent);
            }
        });

        // 진동 선택 레이아웃 클릭 이벤트
        binding1.vibrationLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(v.getContext(), VibrationSettingActivity.class);
                startActivity(intent);
            }
        });

        // 알람벨 선택 레이아웃 클릭 이벤트
        binding1.alrambellLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(v.getContext(), AlarmbellSettingActivity.class);
                startActivity(intent);
            }
        });

        //사용자 설정 레아아웃 클릭 이벤트
        binding1.usersettingLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(v.getContext(), UserSettingActivity.class);
                startActivity(intent);
            }
        });

        binding1.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), StepSettingActivity.class);
                startActivity(intent);
            }
        });
    }

    /*
    필요한 권한은 총 4가지 이다.
    READ_EXTERNAL_STORAGE : 외부 저장소 권한
    ACTION_OPEN_DOCUMENT : 파일 읽기 권한
    READ_MEDIA_AUDIO : 오디오 권한
    VIBRATE_SERVICE : 진동 권한
    */

    private void permissionCheck(){
        if (Build.VERSION.SDK_INT>=23){
            permission = new PermissionSupport(this, this);
            //권한이 취소된 경우 다시 요청
            if(!permission.checkPermission()){
                Toast toast = Toast.makeText(this,"권한이 설정되지 않았습니다.", Toast.LENGTH_SHORT);
                permission.requestPermission();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestcode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestcode, permissions, grantResults);

        // return이 false인 경우 다시 권한 요청
        if (!permission.permissionResult(requestcode,permissions,grantResults)){
            Toast toast = Toast.makeText(this,"권한이 설정되지 않았습니다.", Toast.LENGTH_SHORT);
            permission.requestPermission();
        }
    }
    // 외부 저장소 사용 가능 여부 판단
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            Log.i("SongSettingActivity", "외부 사운드 읽기 쓰기 가능");
            return true;
        }
        return false;
    }

    // 오디오 권한이 허용되었을 때 실행할 작업
    private void GrantedAudioPermission() {
        Log.i("SongSettingActivty", "권한 허용 가능");
    }

    // 오디오 권한이 거부되었을 때 실행할 작업
    private void DeniedAudioPermission() {
        // 오디오 권한이 거부되었을 때 사용자에게 알림 또는 대응 처리

        Log.i("SongSettingActivty", "권한 허용 불가능");
    }


}
