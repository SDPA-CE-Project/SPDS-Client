package com.example.spda_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.spda_app.databinding.AlarmStepSettingBinding;

public class StepSettingActivity extends AppCompatActivity {

    private AlarmStepSettingBinding binding;
    LinearLayout firstLayout;
    LinearLayout secondLayout;
    LinearLayout thirdLayout;
    LinearLayout fourthLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AlarmStepSettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firstLayout = binding.first;
        secondLayout = binding.second;
        thirdLayout = binding.third;
        fourthLayout = binding.fourth;

        firstLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 클릭 이벤트 발생 시 컨텍스트 메뉴를 표시하는 코드를 여기에 작성합니다.
                // registerForContextMenu() 및 onCreateContextMenu()는 여전히 필요합니다.
                // 특정 뷰에 대한 컨텍스트 메뉴를 표시하려면 해당 뷰를 매개변수로 사용하여
                // openContextMenu() 메서드를 호출합니다.
                openContextMenu(v);
            }
        });

        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), CameraStreamActivity.class);
                startActivity(intent);
            }
        });
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_edit) {
            // Edit action
            return true;
        } else if (id == R.id.action_delete) {
            // Delete action
            return true;
        } else {
            return super.onContextItemSelected(item);
        }
    }
}
