package com.kodebloc.hospitalmanagementproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kodebloc.hospitalmanagementproject.util.SecurityUtils;

import java.util.HashMap;
import java.util.Map;

public class PatientRegistrationActivity extends AppCompatActivity {
    // Firebase instance
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;


    // UI elements
    private EditText etFullName, etAge, etInsuranceInfo, etEmergencyContact;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_patient_registration);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_patient_registration), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top + 120, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set up the action bar with title and back button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Patient Registration");
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

        // Initialize Firebase instance
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize UI elements
        etFullName = findViewById(R.id.etFullName);
        etAge = findViewById(R.id.etAge);
        etInsuranceInfo = findViewById(R.id.etInsuranceInfo);
        etEmergencyContact = findViewById(R.id.etEmergencyContact);
        btnRegister = findViewById(R.id.btnRegister);

        try {
            SecurityUtils.generateKey(); // Generate the encryption key once
        } catch (Exception e) {
            Log.e("GenerateEncryptionError", "An error occurred:", e);
            Toast.makeText(this, "Error initializing encryption key", Toast.LENGTH_SHORT).show();
        }

        // Register patient button click listener
        btnRegister.setOnClickListener(v -> {
            // Validate user input before registration
            if (validateInput()) {
                // If input is valid, encrypt sensitive data and register patient
                registerPatient();
            }
        });

        // Set up touch listener to hide the keyboard when touching outside the input fields
        View rootLayout = findViewById(R.id.patient_registration_root_layout);
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

    // Method to hide the keyboard
    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    // Method to validate user input
    private boolean validateInput() {
        String fullName = etFullName.getText().toString().trim();
        String age = etAge.getText().toString().trim();
        String insuranceInfo = etInsuranceInfo.getText().toString().trim();
        String emergencyContact = etEmergencyContact.getText().toString().trim();

        // Check if any required field is empty
        if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(age) ||
                TextUtils.isEmpty(insuranceInfo) ||
                TextUtils.isEmpty(emergencyContact)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validate age format
        try {
            int ageValue = Integer.parseInt(age);
            if (ageValue <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid age", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validate emergency contact format
        if (!Patterns.PHONE.matcher(emergencyContact).matches()) {
            Toast.makeText(this, "Please enter a valid emergency contact number", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validation passed
        return true;
    }

    // Method to register patient after validation and encryption
    private void registerPatient() {
        // Get user input
        String fullName = etFullName.getText().toString().trim();
        String age = etAge.getText().toString().trim();
        String insuranceInfo = etInsuranceInfo.getText().toString().trim();
        String emergencyContact = etEmergencyContact.getText().toString().trim();

        try {
            // Encrypt sensitive data
            String encryptedInsuranceInfo = SecurityUtils.encryptData(insuranceInfo);
            String encryptedEmergencyContact = SecurityUtils.encryptData(emergencyContact);

            // Perform patient registration with encrypted data
            addPatientData(fullName, age, encryptedInsuranceInfo, encryptedEmergencyContact);

            // For now, display encrypted data for testing purposes
            displayPatientInfo(fullName, age, encryptedInsuranceInfo, encryptedEmergencyContact);

        } catch (Exception e) {
            Log.e("RegisterPatientError", "An error occurred:", e);
            Toast.makeText(this, "Error encrypting data", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to add patient data to Firestore
    private void addPatientData(String fullName, String age, String insuranceInfo, String emergencyContact) {
        // Get the current user's data
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            //Log.d("currentUser", Objects.requireNonNull(currentUser.getEmail()));
            // Get the user's ID and email
            String userId = currentUser.getUid();
            String userEmail = currentUser.getEmail();

            // Create a map to store patient data
            Map<String, Object> patientData = new HashMap<>();

            // Add patient data to the map
            patientData.put("uid", userId);
            patientData.put("email", userEmail);
            patientData.put("fullName", fullName);
            patientData.put("age", age);
            patientData.put("insuranceInfo", insuranceInfo);
            patientData.put("emergencyContact", emergencyContact);

            db.collection("users")
                    .document(userId)
                    .set(patientData)
                    .addOnSuccessListener(documentReference -> {
                        // Patient data added successfully
                        // Display a success message
                        Toast.makeText(this, "Patient data added successfully", Toast.LENGTH_SHORT).show();
                        // Log the added patient data
                        Log.d("AddPatientDataSuccess", "Patient data added with ID: " + userId);
                        // Redirect to dashboard
                        redirectToDashboard();
                    })
                    .addOnFailureListener(e -> {
                        // Error adding patient data
                        // Display an error message
                        Toast.makeText(this, "Error adding patient data", Toast.LENGTH_SHORT).show();
                        // Log the error
                        Log.e("AddPatientDataError", "Error adding patient data", e);
                    });
        } else {
            // User is not logged in
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }

    private void redirectToDashboard() {
        // Redirect to DashboardActivity
        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
        finish();
    }

    // Method to display patient information (for testing purposes)
    private void displayPatientInfo(String fullName, String age, String insuranceInfo, String emergencyContact) {
        String patientInfo = "Full Name: " + fullName + "\n" +
                "Age: " + age + "\n" +
                "Insurance Information: " + insuranceInfo + "\n" +
                "Emergency Contact: " + emergencyContact;

        // Display patient information in Logcat
        System.out.println(patientInfo);
    }
}
