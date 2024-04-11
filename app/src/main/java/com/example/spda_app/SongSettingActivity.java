package com.example.spda_app;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.spda_app.databinding.SongSettingBinding;


public class SongSettingActivity extends AppCompatActivity {

    private SongSettingBinding binding2;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding2 = SongSettingBinding.inflate(getLayoutInflater()); // 체크할것
        setContentView(binding2.getRoot());
        setTitle("실험제목");

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
//        액션바를 이용한 방법
//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.setDisplayHomeAsUpEnabled(true);
//        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

//    액션바를 이용한 방법
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == android.R.id.home) {
//            onBackPressed(); // 기본 뒤로가기 동작 실행
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
}
