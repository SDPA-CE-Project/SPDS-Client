package com.example.spda_app;

import androidx.appcompat.app.AppCompatActivity;
import com.example.spda_app.databinding.ActivityMainMenuBinding;
import com.google.firebase.auth.FirebaseAuth;

import android.os.Bundle;
import android.view.View;

public class MainMenuActivity extends AppCompatActivity {
    private ActivityMainMenuBinding binding;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainMenuBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        binding.btnStartDetect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        binding.btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        binding.btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}