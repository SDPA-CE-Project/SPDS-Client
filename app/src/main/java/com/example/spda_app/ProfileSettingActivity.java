package com.example.spda_app;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.spda_app.DAO.DBManager;
import com.example.spda_app.databinding.ProfileSettingBinding;

public class ProfileSettingActivity extends AppCompatActivity {

    private ProfileSettingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ProfileSettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("프로필 편집");
        }

        // 프로필 이미지 변경 버튼 리스너
        binding.cameraIcon.setOnClickListener(view -> {
            // 카메라나 갤러리에서 이미지 가져오기 로직 구현 가능
            Toast.makeText(this, "현재 지원되지 않는 시스템입니다", Toast.LENGTH_SHORT).show();
        });

        // 이름 클릭 리스너 - 다이얼로그를 띄워 변경
        binding.nameLayout.setOnClickListener(view -> {
            showEditDialog("이름", binding.nameText.getText().toString(), newName -> {
                binding.nameText.setText(newName);
                DBManager.GetInstance().accountInfo.setUserName(newName); // DBManager 싱글톤에 저장
            });
        });

        // 이메일 클릭 리스너 - 다이얼로그를 띄워 변경
        binding.emailLayout.setOnClickListener(view -> {
            showEditDialog("이메일", binding.emailText.getText().toString(), newEmail -> {
                binding.emailText.setText(newEmail);
                DBManager.GetInstance().accountInfo.setEmail(newEmail); // DBManager 싱글톤에 저장
            });
        });

        // 완료 버튼 클릭 리스너
        binding.completeButton.setOnClickListener(view -> {
            // Firebase Firestore에 저장하는 로직
            DBManager.GetInstance().updateUserDataDB();
            Toast.makeText(this, "설정값이 저장되었습니다.", Toast.LENGTH_SHORT).show();
            finish(); // 액티비티 종료
        });
    }

    // 뒤로 가기 버튼 처리
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // 뒤로 가기 버튼이 눌렸을 때 기본 뒤로 가기 동작 수행
        return true;
    }

    // 다이얼로그로 입력값 받기
    private void showEditDialog(String field, String currentValue, EditCallback callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(field + " 변경");

        final View customLayout = getLayoutInflater().inflate(R.layout.dialog_edit, null);
        builder.setView(customLayout);

        // 입력 필드 설정
        EditText editText = customLayout.findViewById(R.id.editText);
        editText.setText(currentValue);

        builder.setPositiveButton("저장", (dialog, which) -> {
            String newValue = editText.getText().toString();
            callback.onEdit(newValue);
        });

        builder.setNegativeButton("취소", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    // 콜백 인터페이스
    interface EditCallback {
        void onEdit(String newValue);
    }
}
