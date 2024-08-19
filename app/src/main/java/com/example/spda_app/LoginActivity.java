package com.example.spda_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CheckBox;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener{

    private static final String TAG = "LoginActivity";
    public static final int sub = 1001;
    private FirebaseAuth mAuth;

    private FirebaseFirestore fdatabase;
    private Button btnSign, button2;
    private EditText edtEmail, edtPasswd;
    private TextView registerQuestion, resetQuestion;
    private CheckBox chkMailBox;
    private SharedPreferences appData;


    // 사용자 정보 저장

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        btnSign = findViewById(R.id.btnSign);
        edtEmail = findViewById(R.id.edtMail);
        edtPasswd = findViewById(R.id.edtPasswd);
        chkMailBox = findViewById(R.id.chkMailBox);
        registerQuestion = findViewById(R.id.registerQuestion);
        resetQuestion = findViewById(R.id.resetQuestion);
        button2 = findViewById(R.id.button2);


        btnSign.setOnClickListener(this);
        registerQuestion.setOnTouchListener(this);
        registerQuestion.setOnClickListener(this);
        resetQuestion.setOnTouchListener(this);
        resetQuestion.setOnClickListener(this);

        fdatabase = FirebaseFirestore.getInstance();


        appData = getSharedPreferences("appData", MODE_PRIVATE);
        // UI 설정 정보 불러오기
        loadInfo();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        FirebaseFirestore fdatabase = FirebaseFirestore.getInstance();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btnSign) {
            String email = edtEmail.getText().toString().trim();
            String passwd = edtPasswd.getText().toString().trim();
            if (!email.isEmpty() && !passwd.isEmpty()) {
                getLogin();
            }
            else {
                Toast.makeText(this,"Enter your Email and Password", Toast.LENGTH_SHORT).show();
                Log.d(TAG,"Empty Email or Password");
            }
        } else if (id == R.id.registerQuestion) {
            gotoRegi();
        }
        else if (id == R.id.resetQuestion) {
            showResetDialog();
        }
        else if (id == R.id.button2) {
            // mAuth = FirebaseAuth.getInstance();
            fdatabase = FirebaseFirestore.getInstance();

            String test_uid = "A1B2C3D4E5";

            Map<String, Object> user = new HashMap<>();
            user.put("userEmail", "testuser@example.com");
            user.put("userId", "123456789");
            user.put("userPasswd", "testpassword");

            fdatabase.collection("users").document(test_uid)
                    .set(user)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "DocumentSnapshot successfully written!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error writing document", e);
                        }
                    });


        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event){

        int id = view.getId();
        if (id == R.id.registerQuestion) {
            switch(event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    registerQuestion.setTextColor(Color.parseColor("#8B2991"));
                    gotoRegi();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    registerQuestion.setTextColor(Color.BLACK);
                    break;
            }
            return true;
        }

        else if (id == R.id.resetQuestion) {
            switch(event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    resetQuestion.setTextColor(Color.parseColor("#8B2991"));
                    showResetDialog();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    resetQuestion.setTextColor(Color.BLACK);
                    break;
            }
            return true;
        }

        return false;
    }

    /*
    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success");
                    Toast.makeText(LoginActivity.this, "SignIn successes.",
                            Toast.LENGTH_SHORT).show();
                    FirebaseUser user = mAuth.getCurrentUser();
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                    Toast.makeText(LoginActivity.this, "Authentication failed."+task.getException(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    */

    private void showResetDialog() {
        Dialog dialog = new Dialog(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.reset_layout, null);

        // dialog title
        dialog.setTitle("reset password");

        // dialog layout
        dialog.setContentView(dialogView);

        // dialog component
        EditText emailInput = dialogView.findViewById(R.id.emailInput);
        Button findPasswdBtn = dialogView.findViewById(R.id.findPasswdBtn);
        findPasswdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailInput.getText().toString().trim();
                if (email.isEmpty()) {
                    emailInput.setError("Enter the Email");
                } else {
                    resetPasswd(email, dialog);

                }
            }
        });

        // display Dialog
        dialog.show();
    }

    private void resetPasswd(String email, Dialog dialog) {
        mAuth = FirebaseAuth.getInstance();
        // 해당 이메일로 재설정 이메일 전송
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> resetTask) {
                        if (resetTask.isSuccessful()) {
                            dialog.dismiss();
                            showResetDialog2();
                            Log.d(TAG, "Email Send Success : " + email);
                        } else {
                            Toast.makeText(LoginActivity.this, "이메일 전송에 실패했습니다. 가입하신 이메일 주소를 확인해주세요.", Toast.LENGTH_SHORT).show();
                            Log.w(TAG, "Email Send failure : " + email);

                        }
                    }
                });
    }
    private void showResetDialog2() {
        Dialog dialog = new Dialog(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.reset_layout2, null);

        // dialog title
        dialog.setTitle("reset success");

        // dialog layout
        dialog.setContentView(dialogView);

        Button okBtn = dialogView.findViewById(R.id.okBtn);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        // display Dialog
        dialog.show();
    }


    // 비밀번호 재설정 기능

    private void reload() {
        Toast.makeText(LoginActivity.this, "Already SignIn",
                Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(LoginActivity.this, OndeviceActivity.class);
        startActivity(intent);
        finish();
    }

    private void updateUI(FirebaseUser user) {
        if (user == null) {
            edtPasswd.setText("");
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("user", user);
            finish();
        }
    }

    private void getLogin(){
        String email = edtEmail.getText().toString().trim();
        String passwd = edtPasswd.getText().toString().trim();
        mAuth.signInWithEmailAndPassword(email, passwd).addOnCompleteListener(LoginActivity.this,
                new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success");
                    Toast.makeText(LoginActivity.this, "SignIn successes.",
                            Toast.LENGTH_SHORT).show();
                    FirebaseUser user = mAuth.getCurrentUser();
                    edtEmail.setText("");
                    edtPasswd.setText("");
                    Intent intent = new Intent(LoginActivity.this, MainActivity2.class);
                    startActivity(intent);
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                    Toast.makeText(LoginActivity.this, "SignIn failed. : " + task.getException(),
                            Toast.LENGTH_SHORT).show();
                }

            }
        });
        saveInfo();
    }

    private void saveInfo() {
        // Editor를 활용한 객체 저장
        SharedPreferences.Editor editor = appData.edit();

        editor.putBoolean("SAVE_LOGIN_DATA", chkMailBox.isChecked());
        editor.putString("ID", edtEmail.getText().toString().trim());

        // apply, commit 을 안하면 변경된 내용이 저장되지 않음
        editor.apply();

    }

    private void loadInfo(){
        // saveInfo 값 불러오기
        boolean saveInfoData = appData.getBoolean("SAVE_LOGIN_DATA", false);
        String id = appData.getString("ID", "");

        // 불러온 값을 UI에 설정
        chkMailBox.setChecked(saveInfoData);
        edtEmail.setText(id);
    }

    private void gotoRegi(){
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }
}