package com.example.spda_app;

import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Color;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.graphics.RegionKt;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.spda_app.DAO.DBManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;


public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "RegisterActivity";
    public static final int sub = 1002;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Button btnChkMail, btnRegister;
    private EditText makeEmail, makePasswd, makePasswdAgain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //mAuth = FirebaseAuth.getInstance();
        btnChkMail = findViewById(R.id.btnChkMail);
        btnRegister = findViewById(R.id.btnRegister);
        makeEmail = findViewById(R.id.makeEmail);
        makePasswd = findViewById(R.id.makePasswd);
        makePasswdAgain = findViewById(R.id.makePasswdAgain);

        btnChkMail.setOnClickListener(this);
        btnRegister.setOnClickListener(this);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null){
                    Log.d(TAG, "User is signed in with Email : " + user.getEmail());
                }
                else {
                    Log.d(TAG, "No User is signed in,");
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        // FirebaseUser currentUser = mAuth.getCurrentUser();
        btnRegister.setEnabled(false);
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        String makeemail = makeEmail.getText().toString().trim();
        String makepasswd = makePasswd.getText().toString().trim();
        String makepasswd2 = makePasswdAgain.getText().toString().trim();

        if (id == R.id.btnChkMail) {
            isCheckemail(makeemail);
        } else if (id == R.id.btnRegister) {
            if(validateInputs(makeemail, makepasswd, makepasswd2)) {
                //if(makepasswd == makepasswd2) {
                createAccount(makeemail, makepasswd);
                updateUI(DBManager.GetInstance().fUser);
            }
        }
    }



    private boolean validateInputs(String email, String passwd, String passwd2) {
        if (email.isEmpty()) {
            Toast.makeText(this, "Email을 입력하세요.", Toast.LENGTH_LONG).show();
            Log.d(TAG, "Email input field is empty.");
            makeEmail.requestFocus();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "유효한 이메일을 입력하세요.", Toast.LENGTH_LONG).show();
            Log.d(TAG, "Invalid email format: " + email);
            makeEmail.requestFocus();
            return false;
        }
        if (passwd.isEmpty()) {
            Toast.makeText(this, "비밀번호를 입력하세요.", Toast.LENGTH_LONG).show();
            Log.d(TAG, "Password input field is empty.");
            makePasswd.requestFocus();
            return false;
        }
        if (passwd.length() < 6) {
            Toast.makeText(this, "비밀번호는 최소 6자 이상이어야 합니다.", Toast.LENGTH_LONG).show();
            Log.d(TAG, "Password is too short: " + passwd);
            makePasswd.requestFocus();
            return false;
        }
        if (!passwd.equals(passwd2)) {
            Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_LONG).show();
            Log.d(TAG, "Passwords do not match: " + passwd + " vs " + passwd2);
            makePasswdAgain.requestFocus();
            return false;
        }
        return true;
    }



    @Override
    protected void onResume() {
        super.onResume();
        // Register your activity or listener here

    }

    @Override
    protected void onPause() {
        super.onPause();

    }
    private void createAccount(String email, String password) {

        DBManager.GetInstance().createAccount(email, password);

    }
    private void updateUI(FirebaseUser user) {
        if (user == null) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
            finish();
        } else {
            makeEmail.setText("");
            makePasswd.setText("");
            makePasswdAgain.setText("");
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void isCheckemail(String email) {

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(RegisterActivity.this, "유효한 이메일을 입력하세요.", Toast.LENGTH_LONG).show();
            Log.d(TAG, "Invalid email format: " + email);
            return;
        }
        DBManager.GetInstance().mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                if(task.isSuccessful())
                {
                    //FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    SignInMethodQueryResult result = task.getResult();
                    List<String> signInMethods = result.getSignInMethods();

                    Log.d(TAG, "result: " + result);
                    Log.d(TAG, "signInMethods: " + signInMethods);

                    
                    // 이메일 중복
                    if(signInMethods != null && ! signInMethods.isEmpty()) {
                        Toast.makeText(RegisterActivity.this, "이미 가입된 Email 입니다.",Toast.LENGTH_LONG).show();
                        Log.d(TAG, "Already Registered Email : " + email);
                        btnRegister.setEnabled(false);
                        
                    } 
                    // 신규 유저
                    else {
                        Toast.makeText(RegisterActivity.this, "사용 가능한 Email 입니다.",Toast.LENGTH_LONG).show();
                        Log.d(TAG, "Avilable Email : " + email);
                        btnRegister.setEnabled(true);
                    }
                }
                else{
                    // 오류 처리
                    Toast.makeText(RegisterActivity.this, "이메일 확인 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error checking Email : ", task.getException());
                }
            }
        });
    }

}

