package com.kodebloc.hospitalmanagementproject.profile;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.kodebloc.hospitalmanagementproject.R;

import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kodebloc.hospitalmanagementproject.util.SecurityUtils;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvFullName, tvAge, tvEmail, tvEmergencyContact, tvInsuranceInfo;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_profile), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top + 120, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set up the action bar with title and back button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Profile");
            actionBar.setDisplayHomeAsUpEnabled(true); // Enable back button
        }

        // Handle the back button press
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Navigate back to the previous activity
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize UI elements
        tvFullName = findViewById(R.id.tvFullName);
        tvAge = findViewById(R.id.tvAge);
        tvEmail = findViewById(R.id.tvEmail);
        tvEmergencyContact = findViewById(R.id.tvEmergencyContact);
        tvInsuranceInfo = findViewById(R.id.tvInsuranceInfo);

        // Get current user ID
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            fetchAndDisplayUserData(uid);
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if no user is logged in
        }
    }

    // Handle back button press
    @Override
    public boolean onSupportNavigateUp() {
        // Handle the action bar's up button
        finish();
        return true;
    }

    private void fetchAndDisplayUserData(String uid) {
        db.collection("users").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        try {
                            // Get and decrypt data
                            String fullName = documentSnapshot.getString("fullName");
                            String age = documentSnapshot.getString("age");
                            String email = documentSnapshot.getString("email");
                            String encryptedEmergencyContact = documentSnapshot.getString("emergencyContact");
                            String encryptedInsuranceInfo = documentSnapshot.getString("insuranceInfo");

                            String emergencyContact = SecurityUtils.decryptData(encryptedEmergencyContact);
                            String insuranceInfo = SecurityUtils.decryptData(encryptedInsuranceInfo);

                            // Display data
                            tvFullName.setText("Full Name: " + fullName);
                            tvAge.setText("Age: " + age);
                            tvEmail.setText("Email: " + email);
                            tvEmergencyContact.setText("Emergency Contact: " + emergencyContact);
                            tvInsuranceInfo.setText("Insurance Info: " + insuranceInfo);
                        } catch (Exception e) {
                            Log.e("DecryptError", "Error decrypting data", e);
                            Toast.makeText(this, "Error displaying profile data", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "No profile data found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreError", "Error fetching data", e);
                    Toast.makeText(this, "Error fetching profile data", Toast.LENGTH_SHORT).show();
                });
    }
}
