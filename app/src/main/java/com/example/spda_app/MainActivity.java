package com.example.spda_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.spda_app.databinding.ActivityLogin2Binding;
import com.example.spda_app.databinding.ActivityMainBinding;
import com.google.firebase.FirebaseApp;
import android.content.SharedPreferences;

public class MainActivity extends AppCompatActivity {

    private ActivityLogin2Binding binding;
    //private ActivityMainBinding mainBinding;

    private BannerAdapter adapter;
    private int[] images = {R.drawable.image1, R.drawable.image2, R.drawable.image3};
    private ImageView dot1, dot2, dot3;
    private boolean isDot1 = true;
    private boolean isDot2 = false;
    private boolean isDot3 = false;
    private ViewPager2 viewPager;

    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String PREF_FIRST_RUN = "firstRun";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        boolean firstRun = settings.getBoolean(PREF_FIRST_RUN, true);

        if (firstRun) {
            // 첫 실행일 경우 설명 화면으로 이동
            Intent intent = new Intent(this, ExplanationActivity.class);
            startActivity(intent);
            finish();
        }

        binding = ActivityLogin2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
//        setContentView(mainBinding.getRoot());

        binding.login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                startActivity(intent);
            }
        });
//        mainBinding.btnSkipDemo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(MainActivity.this, OndeviceActivity.class);
//                startActivity(intent);
//            }
//        });
    }
}