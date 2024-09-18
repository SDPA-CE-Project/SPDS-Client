package com.example.spda_app;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

public class DBReset extends Application {
    public void resetUserDataDB(DatabaseReference ref, FirebaseUser user, Context activity) {
        String uid = user.getUid();
        Map<String, Object> set = new HashMap<>();
        set.put("data/"+uid+"/ratio", new Value(0.0, 0.0, 0.0));
        ref.updateChildren(set);
        Toast.makeText(activity, "key set : "+uid, Toast.LENGTH_SHORT).show();
        set.put("users/"+uid, new UserStatus(uid, false, false));
        ref.updateChildren(set);
    }
    public void resetConnectionFalse(DatabaseReference ref, FirebaseUser user) {
        String uid = user.getUid();
        Map<String, Object> check = new HashMap<>();
        check.put("users/"+uid, new UserStatus(uid, false));
        ref.updateChildren(check);
    }
    public void resetConnectionTrue(DatabaseReference ref, FirebaseUser user) {
        String uid = user.getUid();
        Map<String, Object> check = new HashMap<>();
        check.put("users/"+uid, new UserStatus(uid, true));
        ref.updateChildren(check);
    }
}
