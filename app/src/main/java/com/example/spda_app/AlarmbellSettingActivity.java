package com.example.spda_app;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.spda_app.databinding.AlarmbellSettingBinding;

import java.util.ArrayList;

public class AlarmbellSettingActivity extends AppCompatActivity {
    private static int selectedIndex = 0;
    private ListView listView;
    private ArrayList<String> List;
    private AlarmbellSettingBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AlarmbellSettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.number.setText(String.valueOf(getSelectedIndex()));

        // 음악 리스트 초기화
        List = new ArrayList<>();
        List.add("bell 1");
        List.add("bell 2");
        List.add("bell 3");

        // 리스트뷰 설정 및 어댑터 설정...
        listView = binding.listView;
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, List);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setAdapter(adapter);

        // 리스트뷰 아이템 클릭 리스너 설정
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 클릭한 아이템의 인덱스 값을 selectedMP3Index에 저장
                setSelectedIndex(position);
                binding.number.setText(String.valueOf(getSelectedIndex()));
            }
        });
        Button btnConfirm = binding.confirm;
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 확인 버튼을 클릭했을 때 이전 화면으로 돌아가는 로직을 추가
                finish();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    // 인덱스 번호 설정 메서드
    public static void setSelectedIndex(int index) {
        selectedIndex = index;
    }

    // 현재 설정된 인덱스 번호 반환 메서드
    public static int getSelectedIndex() {
        return selectedIndex;
    }
}
