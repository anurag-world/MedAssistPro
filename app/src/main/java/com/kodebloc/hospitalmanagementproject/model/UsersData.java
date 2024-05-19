package com.kodebloc.hospitalmanagementproject.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class UsersData {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    public UsersData() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    public FirebaseFirestore getDb() {
        return db;
    }

    public void logout() {
        mAuth.signOut();
    }

    public void getUsers(UserCallback callback) {
        // Get the authenticated user
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String uid = currentUser.getUid();
            db.collection("users")
                    .whereEqualTo("uid", uid)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d("UsersData", document.getId() + " => " + document.getData());
                                    callback.onCallback(document.getData());
                                }
                            } else {
                                Log.d("TAG", "Error getting documents: ", task.getException());
                                callback.onCallback(null);
                            }
                        }
                    });
        } else {
            Log.d("TAG", "No authenticated user found");
            callback.onCallback(null);
        }
    }
}
