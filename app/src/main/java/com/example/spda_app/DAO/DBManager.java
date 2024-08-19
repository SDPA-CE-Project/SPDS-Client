package com.example.spda_app.DAO;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.spda_app.LoginActivity;
import com.example.spda_app.UserAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

public class DBManager {

    private static DBManager instance;



    private static final String TAG = "DBManager";

    public UserAccount accountInfo;
    private FirebaseAuth mAuth;

    private FirebaseFirestore fDatabase;


    public DBManager() {
        accountInfo = new UserAccount();
        mAuth = FirebaseAuth.getInstance();
        fDatabase = FirebaseFirestore.getInstance();
    }

    private static DBManager GetInstance()
    {
        if(instance == null)
            instance = new DBManager();
        return instance;
    }



    public void deleteUserByEmail(String email) {
        fDatabase.collection("users")
                .whereEqualTo("emailId", email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String docId = document.getId();
                                fDatabase.collection("users").document(docId)
                                        .delete()
                                        .addOnSuccessListener(aVoid -> {
                                            Log.d(TAG, "successfully deleted! email"+ email);
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.w(TAG, "Error deleting document", e);
                                        });
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void logout(Context context) {
        mAuth.signOut();
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "User Logout: No user is logged in.");
            Intent intent = new Intent(context, LoginActivity.class);
            context.startActivity(intent);
        } else {
            Toast.makeText(context, "Logout failed", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "User Logout: " + mAuth.getCurrentUser().toString());
        }
    }

    public void createAccount(String email, String password, OnCompleteListener<AuthResult> listener) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(listener);
    }

    public void saveUserToDatabase(FirebaseUser user, String email, String password) {


        String userId = user.getUid();
        UserAccount useraccount = new UserAccount();
        useraccount.setUserId(userId);
        useraccount.setEmailId(email);
        useraccount.setPassword(password);


        // mDatabase.child("users").child(userId).setValue(useraccount);


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

    private void updatePasswd(String newPasswd) {



/*        fDatabase.child("users").child(userId).child("password").setValue(newPasswd)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "비밀번호가 변경되었습니다.", Toast.LENGTH_SHORT).show();
                        dismiss();
                    } else {
                        Toast.makeText(getContext(), "비밀번호 변경 실패.", Toast.LENGTH_SHORT).show();
                    }
                });*/



    }



}
