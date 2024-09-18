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

import com.example.spda_app.DAO.DBManager;
import com.example.spda_app.databinding.AlarmStepSetting2Binding;
import com.example.spda_app.databinding.AlarmStepSettingBinding;

public class StepSettingActivity extends AppCompatActivity {

    private @NonNull AlarmStepSetting2Binding binding;

    private Spinner spinner01;
    private Spinner spinner02;
    private Spinner spinner03;
    private TextView tv_result;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




        binding = AlarmStepSetting2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        spinner01 = (Spinner) binding.spinner1;
        spinner02 = (Spinner) binding.spinner2;
        spinner03 = (Spinner) binding.spinner3;

        spinner01.setDropDownVerticalOffset(130);
        spinner02.setDropDownVerticalOffset(130);
        spinner03.setDropDownVerticalOffset(130);


        switch (DBManager.GetInstance().accountInfo.getLevelIndex())
        {
            case 0:
                spinner01.setSelection(0);
                spinner02.setSelection(1);
                spinner03.setSelection(2);
                break;
            case 1:
                spinner01.setSelection(0);
                spinner02.setSelection(2);
                spinner03.setSelection(1);
                break;
            case 2:
                spinner01.setSelection(1);
                spinner02.setSelection(0);
                spinner03.setSelection(2);
                break;
            case 3:
                spinner01.setSelection(1);
                spinner02.setSelection(2);
                spinner03.setSelection(0);
                break;
            case 4:
                spinner01.setSelection(2);
                spinner02.setSelection(0);
                spinner03.setSelection(1);
                break;
            case 5:
                spinner01.setSelection(2);
                spinner02.setSelection(1);
                spinner03.setSelection(0);
                break;
        }



        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String selectedItem1 = spinner01.getSelectedItem().toString();
                String selectedItem2 = spinner02.getSelectedItem().toString();
                String selectedItem3 = spinner03.getSelectedItem().toString();


                int selectedIndex1 = spinner01.getSelectedItemPosition();
                int selectedIndex2 = spinner02.getSelectedItemPosition();
                int selectedIndex3 = spinner03.getSelectedItemPosition();


                if (selectedIndex1 != selectedIndex2 && selectedIndex1 != selectedIndex3 && selectedIndex2 != selectedIndex3) {
                    if (selectedIndex1 == 0 && selectedIndex2 == 1 && selectedIndex3 == 2) {
                        // 처리: selectedIndex1 == 0, selectedIndex2 == 1, selectedIndex3 == 2
                        DBManager.GetInstance().accountInfo.setLevelIndex(0);

                    } else if (selectedIndex1 == 0 && selectedIndex2 == 2 && selectedIndex3 == 1) {
                        // 처리: selectedIndex1 == 0, selectedIndex2 == 2, selectedIndex3 == 1

                        DBManager.GetInstance().accountInfo.setLevelIndex(1);

                    } else if (selectedIndex1 == 1 && selectedIndex2 == 0 && selectedIndex3 == 2) {
                        // 처리: selectedIndex1 == 1, selectedIndex2 == 0, selectedIndex3 == 2

                        DBManager.GetInstance().accountInfo.setLevelIndex(2);

                    } else if (selectedIndex1 == 1 && selectedIndex2 == 2 && selectedIndex3 == 0) {
                        // 처리: selectedIndex1 == 1, selectedIndex2 == 2, selectedIndex3 == 0

                        DBManager.GetInstance().accountInfo.setLevelIndex(3);

                    } else if (selectedIndex1 == 2 && selectedIndex2 == 0 && selectedIndex3 == 1) {
                        // 처리: selectedIndex1 == 2, selectedIndex2 == 0, selectedIndex3 == 1


                        DBManager.GetInstance().accountInfo.setLevelIndex(4);
                    } else if (selectedIndex1 == 2 && selectedIndex2 == 1 && selectedIndex3 == 0) {
                        // 처리: selectedIndex1 == 2, selectedIndex2 == 1, selectedIndex3 == 0

                        DBManager.GetInstance().accountInfo.setLevelIndex(5);

                    }
                } else {
                    // 중복된 값이 있을 경우 처리 (필요한 경우)
                }


                // 모든 항목이 서로 다른지 확인
                if (!selectedItem1.equals(selectedItem2) && !selectedItem1.equals(selectedItem3)
                        && !selectedItem2.equals(selectedItem3)
                        ) {
                    // 모두 다를 경우 다음 화면으로 이동하는 Intent 생성
                    Intent intent = new Intent(StepSettingActivity.this, MainActivity2.class);
                    // 선택된 항목들을 다음 화면으로 전달
                    intent.putExtra("selectedItem1", selectedItem1);
                    intent.putExtra("selectedItem2", selectedItem2);
                    intent.putExtra("selectedItem3", selectedItem3);

                    DBManager.GetInstance().updateUserDataDB();


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