package com.example.spda_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.spda_app.databinding.ActivityExplanationBinding;
import com.google.firebase.FirebaseApp;

public class ExplanationActivity extends AppCompatActivity {

    private ActivityExplanationBinding binding;

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

        binding = ActivityExplanationBinding.inflate(getLayoutInflater());
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
                    // 첫 실행이 아님을 저장하는 코드
                    SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean(PREF_FIRST_RUN, false);
                    editor.apply();

                    Intent intent = new Intent(ExplanationActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
}
