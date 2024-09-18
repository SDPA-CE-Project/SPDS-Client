package com.example.spda_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.spda_app.DAO.DBManager;
import com.example.spda_app.databinding.MainPageBinding;

public class MainActivity2 extends AppCompatActivity {
    private MainPageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = MainPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if(DBManager.GetInstance().mAuth.getCurrentUser()==null) {
            // 텍스트 변경 로그인
            binding.logStatus.setImageResource(R.drawable.lock);
            // 이미지 변경 로그인 이미지
            binding.log.setText("로그인");
        }
        // 현재 로그인 상태
        else {
            binding.logStatus.setImageResource(R.drawable.unlock);
            binding.log.setText("로그아웃");
        }

        binding.start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(DBManager.GetInstance().mAuth.getCurrentUser()==null) {
                    Toast.makeText(MainActivity2.this, "먼저 로그인을 해주십시오.", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent intent = new Intent(MainActivity2.this, OndeviceActivity.class);
                    startActivity(intent);
                }
            }
        });
        binding.setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity2.this, AlarmSettingActivity.class);
                startActivity(intent);
            }
        });
        binding.profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity2.this, ProfileSettingActivity.class);
                startActivity(intent);
            }
        });
        binding.logStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 현재 사용자 로그아웃 상태일경우
                if(DBManager.GetInstance().mAuth.getCurrentUser()==null) {
                    Intent intent = new Intent(MainActivity2.this, LoginActivity.class);
                    startActivity(intent);
                }
                else{
                    DBManager.GetInstance().mAuth.signOut();
                    Toast.makeText(MainActivity2.this, "로그아웃 성공", Toast.LENGTH_SHORT).show();

                    // 앱 재시작
                    Intent intent = new Intent(MainActivity2.this, MainActivity2.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish(); // 현재 액티비티 종료
                }
            }
        });
//        binding.exit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //finish();
//                System.exit(1);
//            }
//        });
    }
}
