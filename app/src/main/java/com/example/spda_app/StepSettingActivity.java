package com.example.spda_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.spda_app.databinding.AlarmStepSetting2Binding;
import com.example.spda_app.databinding.AlarmStepSettingBinding;

public class StepSettingActivity extends AppCompatActivity {

    private @NonNull AlarmStepSetting2Binding binding;

    private Spinner spinner01;
    private Spinner spinner02;
    private Spinner spinner03;
    private Spinner spinner04;
    private TextView tv_result;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AlarmStepSetting2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        spinner01 = (Spinner) binding.spinner1;
        spinner02 = (Spinner) binding.spinner2;
        spinner03 = (Spinner) binding.spinner3;
        spinner04 = (Spinner) binding.spinner4;

        spinner01.setDropDownVerticalOffset(130);
        spinner02.setDropDownVerticalOffset(130);
        spinner03.setDropDownVerticalOffset(130);
        spinner04.setDropDownVerticalOffset(130);

        spinner01.setSelection(0);
        spinner02.setSelection(1);
        spinner03.setSelection(2);
        spinner04.setSelection(3);

        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), CameraStreamActivity.class);
                startActivity(intent);
            }
        });
    }
}