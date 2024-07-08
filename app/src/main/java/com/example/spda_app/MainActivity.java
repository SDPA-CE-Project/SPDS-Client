package com.example.spda_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.spda_app.databinding.ActivityMainBinding;
import com.google.firebase.FirebaseApp;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private BannerAdapter adapter;
    private int[] images = {R.drawable.image1, R.drawable.image2, R.drawable.image3};
    private ImageView dot1, dot2, dot3;
    private boolean isDot1 = true;
    private boolean isDot2 = false;
    private boolean isDot3 = false;
    private ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        dot1 = binding.dot1;
        dot2 = binding.dot2;
        dot3 = binding.dot3;

        FirebaseApp.initializeApp(this);

        viewPager = findViewById(R.id.viewPager);
        adapter = new BannerAdapter(this, images);
        viewPager.setAdapter(adapter);
        viewPager.setUserInputEnabled(false);

        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // dot 이미지 변경 로직
                if (isDot1) {
                    viewPager.setCurrentItem(1, true);
                    dot1.setImageResource(R.drawable.button); // 버튼 이미지 변경
                    dot2.setImageResource(R.drawable.onbutton); // 다른 버튼 이미지로 변경
                    dot3.setImageResource(R.drawable.button); // 다른 버튼 이미지로 변경
                    isDot1 = false;
                    isDot2 = true;
                } else if(isDot2) {
                    viewPager.setCurrentItem(2, true);
                    dot1.setImageResource(R.drawable.button); // 버튼 이미지 변경
                    dot2.setImageResource(R.drawable.button); // 다른 버튼 이미지로 변경
                    dot3.setImageResource(R.drawable.onbutton); // 다른 버튼 이미지로 변경
                    isDot2 = false;
                    isDot3 = true;
                } else {
                    Intent intent = new Intent(MainActivity.this, AlarmSettingActivity.class);
                    startActivity(intent);
                }
            }
        });
        binding.btnSkipDemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, OndeviceActivity.class);
                startActivity(intent);
            }
        });
    }
}