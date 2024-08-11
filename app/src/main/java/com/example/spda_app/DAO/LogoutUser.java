package com.example.spda_app.DAO;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.spda_app.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LogoutUser {
    private static final String TAG = "LogoutUser";
    private FirebaseAuth mAuth;

    public LogoutUser() {
        mAuth = FirebaseAuth.getInstance();
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
}