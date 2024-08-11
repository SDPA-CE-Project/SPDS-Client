package com.example.spda_app.DAO;


import com.example.spda_app.UserAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateAccount {

    private static final String TAG = "CreateAccount";
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    public CreateAccount() {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
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
        mDatabase.child("users").child(userId).setValue(useraccount);
    }


}
