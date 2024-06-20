package com.example.spda_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener{

    private static final String TAG = "LoginActivity";
    public static final int sub = 1001;
    private FirebaseAuth mAuth;
    private Button btnSign;
    private EditText edtEmail, edtPasswd;
    private TextView registerQuestion;
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
        btnSign.setOnClickListener(this);
        registerQuestion.setOnTouchListener(this);
        registerQuestion.setOnClickListener(this);


        appData = getSharedPreferences("appData", MODE_PRIVATE);
        // UI 설정 정보 불러오기
        loadInfo();

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.btnSign) {
            getLogin();
        } else if (id == R.id.registerQuestion) {
            gotoRegi();
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
                    registerQuestion.setTextColor(Color.BLACK);
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
                    Intent intent = new Intent(LoginActivity.this, OndeviceActivity.class);
                    startActivity(intent);
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                    Toast.makeText(LoginActivity.this, "SignIn failed." + task.getException(),
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