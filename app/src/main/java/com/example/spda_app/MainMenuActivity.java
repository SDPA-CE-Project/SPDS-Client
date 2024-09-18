package com.example.spda_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.view.PreviewView;

import com.example.spda_app.DAO.DBManager;
import com.google.firebase.auth.FirebaseAuth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.ImageView;

public class MainMenuActivity extends AppCompatActivity {


    private Button start;
    private ImageButton profile, setting;
    private ImageView logStatus;

    private TextView log;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        start = findViewById(R.id.start);
        log = findViewById(R.id.log);
        profile = findViewById(R.id.profile);
        setting = findViewById(R.id.setting);
        logStatus = findViewById(R.id.logStatus);

        // 현재 로그아웃 상태
        if(DBManager.GetInstance().mAuth.getCurrentUser()==null) {
            // 텍스트 변경 로그인
            logStatus.setImageResource(R.drawable.lock);
            // 이미지 변경 로그인 이미지
            log.setText("로그인");
        }
        // 현재 로그인 상태
        else {
            logStatus.setImageResource(R.drawable.unlock);
            log.setText("로그아웃");
        }

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(DBManager.GetInstance().mAuth.getCurrentUser()==null) {
                    Toast.makeText(MainMenuActivity.this, "먼저 로그인을 해주십시오.", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent intent = new Intent(MainMenuActivity.this, OndeviceActivity.class);
                    startActivity(intent);
                }

            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(DBManager.GetInstance().mAuth.getCurrentUser()==null) {
                    Toast.makeText(MainMenuActivity.this, "먼저 로그인을 해주십시오.", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent intent = new Intent(MainMenuActivity.this, ProfileSettingActivity.class);
                    startActivity(intent);
                }
            }
        });

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainMenuActivity.this, AlarmSettingActivity.class);
                startActivity(intent);
            }
        });
        logStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 현재 사용자 로그아웃 상태일경우
                if(DBManager.GetInstance().mAuth.getCurrentUser()==null) {
                    Intent intent = new Intent(MainMenuActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                else{
                    DBManager.GetInstance().mAuth.signOut();
                    Toast.makeText(MainMenuActivity.this, "로그아웃 성공", Toast.LENGTH_SHORT).show();

                    // 앱 재시작
                    Intent intent = new Intent(MainMenuActivity.this, MainMenuActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish(); // 현재 액티비티 종료
                }
            }
        });
    }
}