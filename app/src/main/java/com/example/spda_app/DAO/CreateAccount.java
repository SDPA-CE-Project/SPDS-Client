package com.example.spda_app.DAO;


import android.util.Log;

import com.example.spda_app.UserAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

public class CreateAccount {

    private static final String TAG = "CreateAccount";
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private FirebaseFirestore fDatabase;




    public CreateAccount() {
        mAuth = FirebaseAuth.getInstance();
        fDatabase = FirebaseFirestore.getInstance();

    }

    public void createAccount(String email, String password, OnCompleteListener<AuthResult> listener) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(listener);

    }

    public void saveUserToDatabase(FirebaseUser user, String email, String password) {
        String userId = user.getUid();
        UserAccount useraccount = new UserAccount();
        useraccount.setUserId(userId);
        useraccount.setEmail(email);
        useraccount.setPassword(password);


        // Firestore에 저장
        fDatabase.collection("users").document(userId).set(useraccount)
                .addOnSuccessListener(aVoid -> {
                    // Firestore에 저장 성공
                    Log.d(TAG, "User data successfully written to Firestore!");
                })
                .addOnFailureListener(e -> {
                    // Firestore에 저장 실패
                    Log.w(TAG, "Error writing user data to Firestore", e);
                });
    }
}
