package com.example.spda_app.DAO;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.spda_app.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChangePasswd extends Dialog {
    private EditText newPasswd, newPasswdAgain;
    private Button btnOk;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String userId;

    public ChangePasswd(@NonNull Context context, String userId) {
        super(context);
        userId = mAuth.getUid();
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.changepasswd_layout);
        //newPasswd = findViewById(R.id.newPasswd);
        //newPasswdAgain = findViewById(R.id.newPasswdAgain);
        btnOk = findViewById(R.id.okBtn);


    }

    private void updatePasswd(String newPasswd) {
        mDatabase.child("users").child(userId).child("password").setValue(newPasswd)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "비밀번호가 변경되었습니다.", Toast.LENGTH_SHORT).show();
                        dismiss();
                    } else {
                        Toast.makeText(getContext(), "비밀번호 변경 실패.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    // 두 비밀번호 입력 값이 동일한지 확인
    private void checkPasswords() {
        String passwd = newPasswd.getText().toString();
        String passwdAgain = newPasswdAgain.getText().toString();
        btnOk.setEnabled(!passwd.isEmpty() && passwd.equals(passwdAgain));
    }

}