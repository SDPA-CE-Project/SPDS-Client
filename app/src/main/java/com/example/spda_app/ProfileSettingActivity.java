package com.example.spda_app;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.spda_app.databinding.ProfileSettingBinding;

public class ProfileSettingActivity extends AppCompatActivity {

    private ProfileSettingBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ProfileSettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("프로필 편집");
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // 뒤로 가기 버튼이 눌렸을 때 기본 뒤로 가기 동작 수행
        return true;
    }
}
