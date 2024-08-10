package com.example.spda_app;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.spda_app.databinding.VibrationSettingBinding;

import java.util.ArrayList;

public class VibrationSettingActivity extends AppCompatActivity {
    private VibrationSettingBinding binding;
    private ListView listViewVibration;
    private ArrayList<String> vibrationList;
    private static int selectedVibrationIndex = 0; // 초기값 설정
    private PlayVibrate playVibrate; // PlayVibrate 인스턴스

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = VibrationSettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        playVibrate = new PlayVibrate(this); // PlayVibrate 초기화

        binding.number.setText(String.valueOf(getSelectedVibrationIndex()));

        vibrationList = new ArrayList<>();
        vibrationList.add("Vibration Pattern 1");
        vibrationList.add("Vibration Pattern 2");
        vibrationList.add("Vibration Pattern 3");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, vibrationList);
        listViewVibration = binding.listViewVibration;
        listViewVibration.setAdapter(adapter);
        listViewVibration.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        listViewVibration.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setSelectedVibrationIndex(position);
                binding.number.setText(String.valueOf(getSelectedVibrationIndex()));
                if(playVibrate.isPlaying()) {
                    playVibrate.stopAlarm();
                    playVibrate = new PlayVibrate(VibrationSettingActivity.this);
                    playVibrate.playAlarm();
                }
                playVibrate.playAlarm(); // 진동 재생
            }
        });

        Button btnConfirm = binding.confirm;
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playVibrate.stopAlarm(); // 진동 중지
                finish();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public static void setSelectedVibrationIndex(int index) {
        selectedVibrationIndex = index;
    }

    public static int getSelectedVibrationIndex() {
        return selectedVibrationIndex;
    }
}
