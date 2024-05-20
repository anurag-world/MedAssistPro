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
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kodebloc.hospitalmanagementproject.util.SecurityUtils;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private EditText etFullName, etAge, etEmail, etEmergencyContact, etInsuranceInfo;
    private Button btnSave;
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
        etFullName = findViewById(R.id.etFullName);
        etAge = findViewById(R.id.etAge);
        etEmail = findViewById(R.id.etEmail);
        etEmergencyContact = findViewById(R.id.etEmergencyContact);
        etInsuranceInfo = findViewById(R.id.etInsuranceInfo);
        btnSave = findViewById(R.id.btnSave);

        // Get current user ID
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            loadUserData(uid);
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if no user is logged in
        }

        // Set save button click listener
        btnSave.setOnClickListener(v -> saveUserData());

        // Set up touch listener to hide the keyboard when touching outside the input fields
        View rootLayout = findViewById(R.id.profile_root_layout);
        rootLayout.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                hideKeyboard();
                v.performClick();
                return true;
            }
            return false;
        });

        // Ensure the root view handles click event for accessibility
        rootLayout.setOnClickListener(v -> {
            // Handle click event
        });
    }

    // Handle back button press
    @Override
    public boolean onSupportNavigateUp() {
        // Handle the action bar's up button
        finish();
        return true;
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void loadUserData(String uid) {
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
                            etFullName.setText(fullName);
                            etAge.setText(age);
                            etEmail.setText(email);
                            etEmergencyContact.setText(emergencyContact);
                            etInsuranceInfo.setText(insuranceInfo);
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

    private void saveUserData() {
        // Get user input
        String fullName = etFullName.getText().toString().trim();
        String age = etAge.getText().toString().trim();
        String emergencyContact = etEmergencyContact.getText().toString().trim();
        String insuranceInfo = etInsuranceInfo.getText().toString().trim();

        // Encrypt sensitive data
        try {
            String encryptedEmergencyContact = SecurityUtils.encryptData(emergencyContact);
            String encryptedInsuranceInfo = SecurityUtils.encryptData(insuranceInfo);

            // Get the current user's ID
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                String uid = currentUser.getUid();

                // Create a map to store updated user data
                Map<String, Object> updatedData = new HashMap<>();
                updatedData.put("fullName", fullName);
                updatedData.put("age", age);
                updatedData.put("emergencyContact", encryptedEmergencyContact);
                updatedData.put("insuranceInfo", encryptedInsuranceInfo);

                // Save updated data to Firestore
                db.collection("users").document(uid).update(updatedData)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Log.e("FirestoreError", "Error updating data", e);
                            Toast.makeText(this, "Error updating profile data", Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("EncryptError", "Error encrypting data", e);
            Toast.makeText(this, "Error encrypting data", Toast.LENGTH_SHORT).show();
        }
    }
}
