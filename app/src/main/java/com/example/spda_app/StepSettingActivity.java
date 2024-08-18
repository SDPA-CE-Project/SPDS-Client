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
import android.widget.Toast;

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
                String selectedItem1 = spinner01.getSelectedItem().toString();
                String selectedItem2 = spinner02.getSelectedItem().toString();
                String selectedItem3 = spinner03.getSelectedItem().toString();
                String selectedItem4 = spinner04.getSelectedItem().toString();

                // 모든 항목이 서로 다른지 확인
                if (!selectedItem1.equals(selectedItem2) && !selectedItem1.equals(selectedItem3) && !selectedItem1.equals(selectedItem4)
                        && !selectedItem2.equals(selectedItem3) && !selectedItem2.equals(selectedItem4)
                        && !selectedItem3.equals(selectedItem4)) {
                    // 모두 다를 경우 다음 화면으로 이동하는 Intent 생성
                    Intent intent = new Intent(StepSettingActivity.this, MainActivity2.class);
                    // 선택된 항목들을 다음 화면으로 전달
                    intent.putExtra("selectedItem1", selectedItem1);
                    intent.putExtra("selectedItem2", selectedItem2);
                    intent.putExtra("selectedItem3", selectedItem3);
                    intent.putExtra("selectedItem4", selectedItem4);
                    // 다음 화면으로 이동
                    startActivity(intent);
                } else {
                    // 모두 다르지 않을 경우 토스트 메시지 출력
                    Toast.makeText(StepSettingActivity.this, "항목을 모두 다르게 체크해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}