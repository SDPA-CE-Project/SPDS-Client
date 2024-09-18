package com.example.spda_app.DAO;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.spda_app.UserAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class DeleteAccount {

    private static final String TAG = "DeleteAccount";
    private static FirebaseFirestore fDatabase;


    public DeleteAccount() {
        fDatabase = FirebaseFirestore.getInstance();

    }

    public static void deleteUserByEmail(String email) {
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


}
