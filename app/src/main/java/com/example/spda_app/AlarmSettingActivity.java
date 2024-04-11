package com.example.spda_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.spda_app.databinding.ActivityMainBinding;
import com.example.spda_app.databinding.AlarmSettingBinding;

public class AlarmSettingActivity extends AppCompatActivity {
    private AlarmSettingBinding binding1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding1 = AlarmSettingBinding.inflate(getLayoutInflater());
        setContentView(binding1.getRoot());

        // 노래 선택 레이아웃 클릭 이벤트
        binding1.songLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(v.getContext(), SongSettingActivity.class);
                startActivity(intent);
            }
        });

        // 진동 선택 레이아웃 클릭 이벤트
        binding1.vibrationLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(v.getContext(), SongSettingActivity.class);
                startActivity(intent);
            }
        });

        // 알람벨 선택 레이아웃 클릭 이벤트
        binding1.alrambellLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(v.getContext(), SongSettingActivity.class);
                startActivity(intent);
            }
        });

        //사용자 설정 레아아웃 클릭 이벤트
        binding1.usersettingLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(v.getContext(), SongSettingActivity.class);
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
}
