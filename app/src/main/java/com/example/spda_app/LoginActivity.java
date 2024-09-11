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

import com.example.spda_app.DAO.DBManager;
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
    private Button btnLogin, btnSign;
    private EditText edtEmail, edtPasswd;
    private TextView resetQuestion;
    private SharedPreferences appData;


    // 사용자 정보 저장

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
        btnLogin = findViewById(R.id.btnLogin);
        btnSign = findViewById(R.id.btnSign);
        edtEmail = findViewById(R.id.edtEmail);
        edtPasswd = findViewById(R.id.edtPasswd);
        resetQuestion = findViewById(R.id.resetQuestion);

        btnLogin.setOnClickListener(this);
        btnSign.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                gotoRegi();
                Log.d(TAG,"onClick 성공");
            }

        });
        resetQuestion.setOnClickListener(this);


        resetQuestion.setOnTouchListener(this);


        appData = getSharedPreferences("appData", MODE_PRIVATE);
        // UI 설정 정보 불러오기
        //loadInfo();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onClick(View view) {
        Log.d(TAG, "일단 뭔가 눌러지고 있음");
        int id = view.getId();
        if (id == R.id.btnLogin) {
            String email = edtEmail.getText().toString().trim();
            String passwd = edtPasswd.getText().toString().trim();
            if (!email.isEmpty() && !passwd.isEmpty()) {
                getLogin();
            }
            else {
                Toast.makeText(this,"Enter your Email and Password", Toast.LENGTH_SHORT).show();
                Log.d(TAG,"Empty Email or Password");
            }
        }
        else if (id == R.id.resetQuestion) {
            showResetDialog();
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event){

        int id = view.getId();
        if (id == R.id.resetQuestion) {
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
        // 해당 이메일로 재설정 이메일 전송
        DBManager.GetInstance().mAuth.sendPasswordResetEmail(email)
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
        DBManager.GetInstance().mAuth.signInWithEmailAndPassword(email, passwd).addOnCompleteListener(LoginActivity.this,
                new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success");
                    Toast.makeText(LoginActivity.this, "SignIn successes.",
                            Toast.LENGTH_SHORT).show();
                    DBManager.GetInstance().fUser = DBManager.GetInstance().mAuth.getCurrentUser();
                    edtEmail.setText("");
                    edtPasswd.setText("");
                    DBManager.GetInstance().loadUserDataDB();
                    Intent intent = new Intent(LoginActivity.this, MainMenuActivity.class);
                    startActivity(intent);
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                    Toast.makeText(LoginActivity.this, "SignIn failed. : " + task.getException(),
                            Toast.LENGTH_SHORT).show();
                }

            }
        });
    }



    private void loadInfo(){
        // saveInfo 값 불러오기
        boolean saveInfoData = appData.getBoolean("SAVE_LOGIN_DATA", false);
        String id = appData.getString("ID", "");

        // 불러온 값을 UI에 설정
        edtEmail.setText(id);
    }

    private void gotoRegi(){
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }
}