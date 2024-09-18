package com.example.spda_app.DAO;

import static com.pedro.rtsp.utils.ExtensionsKt.getData;
import static java.security.AccessController.getContext;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.spda_app.LoginActivity;
import com.example.spda_app.RegisterActivity;
import com.example.spda_app.UserAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.auth.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class DBManager {

    private static DBManager instance;

    private static final String TAG = "DBManager";
    public UserAccount accountInfo;
    public FirebaseAuth mAuth;

    public FirebaseUser fUser;
    public FirebaseFirestore fDatabase;



    public DBManager() {
        accountInfo = new UserAccount();
        mAuth = FirebaseAuth.getInstance();
        fDatabase = FirebaseFirestore.getInstance();
        fUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    public static DBManager GetInstance()
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

    public void createAccount(String email, String password, CreateAccountCallback callback) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            DBManager.GetInstance().fUser = task.getResult().getUser();
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            if (DBManager.GetInstance().fUser != null) {
                                String uid = DBManager.GetInstance().fUser.getUid();
                                // UID를 사용할 수 있습니다.
                                Log.d("FirebaseAuth", "User UID: " + uid);

                                Map<String, Object> user = new HashMap<>();
                                user.put("userEmail", email);
                                user.put("userId", uid);
                                user.put("userPasswd", password);
                                user.put("userName", "홍길동");
                                user.put("levelIndex",0);
                                user.put("vibrationIndex",0);
                                user.put("alarmBellIndex",0);
                                user.put("songIndex",0);

                                DBManager.GetInstance().fDatabase.collection("users").document(uid)
                                        .set(user)

                                        .addOnSuccessListener(aVoid-> callback.onResult(1)) // success == 1
                                        .addOnFailureListener(e -> callback.onResult(0)); // failure == 0
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            callback.onResult(0); // 실패 시 0 반환
                        }
                    }
                });
    }

    public void loadUserDataDB() {
        String uid = fUser.getUid();
        DocumentReference docRef = fDatabase.collection("users").document(uid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        accountInfo.setEmail(document.get("userEmail").toString());
                        accountInfo.setPassword(document.get("userPasswd").toString());
                        accountInfo.setUserId(document.get("userId").toString());
                        accountInfo.setUserName(document.get("userName").toString());
                        accountInfo.setAlarmBellIndex(Integer.parseInt(document.get("alarmBellIndex").toString()));
                        accountInfo.setLevelIndex(Integer.parseInt(document.get("levelIndex").toString()));
                        accountInfo.setVibrationIndex(Integer.parseInt(document.get("vibrationIndex").toString()));
                        accountInfo.setSongIndex(Integer.parseInt(document.get("songIndex").toString()));

//                        accountInfo.setTestdata(document.get("testdata").toString());

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }

    public void updateUserDataDB() {
        String uid = fUser.getUid();
        DocumentReference docRef = fDatabase.collection("users").document(uid);

        Map<String, Object> updates = new HashMap<>();
        updates.put("userName", accountInfo.getUserName());
        updates.put("userEmail", accountInfo.getEmail());
        updates.put("alarmBellIndex",accountInfo.getAlarmBellIndex());
        updates.put("levelIndex",accountInfo.getLevelIndex());
        updates.put("vibrationIndex",accountInfo.getVibrationIndex());
        updates.put("songIndex",accountInfo.getSongIndex());


//        docRef
//                .update("testdata", accountInfo.getTestdata())
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        Log.d(TAG, "DocumentSnapshot successfully updated!");
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.w(TAG, "Error updating document", e);
//                    }
//                });
        docRef
                .update(updates)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully updated!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error updating document", e));
    }

    // Create Callback Interface
    public interface CreateAccountCallback {
        void onResult(int result);
    }

    public void saveUserToDatabase(FirebaseUser user, String email, String password) {


        String userId = user.getUid();
        UserAccount useraccount = new UserAccount();
        useraccount.setUserId(userId);
        useraccount.setEmail(email);
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

    public void AddAlarmHistory(int alarmLevel)
    {
        Log.w("history","History called");
        Map<String,Object> historyData = new HashMap<>();
        historyData.put(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), alarmLevel);

        String uid = fUser.getUid();
        DocumentReference docRef = fDatabase.collection("history").document(uid);
        docRef.set(historyData, SetOptions.merge())
                .addOnFailureListener(e -> {Log.w("Firestore", "Error writing document", e);})
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "DocumentSnapshot successfully written!");
                });


    }

}
