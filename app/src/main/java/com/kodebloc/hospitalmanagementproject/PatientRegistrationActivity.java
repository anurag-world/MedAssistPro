package com.kodebloc.hospitalmanagementproject;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
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
    private EditText etFullName, etAge, etMedicalHistory, etInsuranceInfo, etEmergencyContact;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_patient_registration);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_patient_registration), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase instance
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize UI elements
        etFullName = findViewById(R.id.etFullName);
        etAge = findViewById(R.id.etAge);
        etMedicalHistory = findViewById(R.id.etMedicalHistory);
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
    }

    // Method to validate user input
    private boolean validateInput() {
        String fullName = etFullName.getText().toString().trim();
        String age = etAge.getText().toString().trim();
        String medicalHistory = etMedicalHistory.getText().toString().trim();
        String insuranceInfo = etInsuranceInfo.getText().toString().trim();
        String emergencyContact = etEmergencyContact.getText().toString().trim();

        // Check if any required field is empty
        if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(age) ||
                TextUtils.isEmpty(medicalHistory) || TextUtils.isEmpty(insuranceInfo) ||
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
        String medicalHistory = etMedicalHistory.getText().toString().trim();
        String insuranceInfo = etInsuranceInfo.getText().toString().trim();
        String emergencyContact = etEmergencyContact.getText().toString().trim();

        try {
            // Encrypt sensitive data
            String encryptedMedicalHistory = SecurityUtils.encryptData(medicalHistory);
            String encryptedInsuranceInfo = SecurityUtils.encryptData(insuranceInfo);
            String encryptedEmergencyContact = SecurityUtils.encryptData(emergencyContact);

            // Perform patient registration with encrypted data
            // For now, display encrypted data for testing purposes
            displayPatientInfo(fullName, age, encryptedMedicalHistory, encryptedInsuranceInfo, encryptedEmergencyContact);
        } catch (Exception e) {
            Log.e("RegisterPatientError", "An error occurred:", e);
            Toast.makeText(this, "Error encrypting data", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to display patient information (for testing purposes)
    private void displayPatientInfo(String fullName, String age, String medicalHistory,
                                    String insuranceInfo, String emergencyContact) {
        String patientInfo = "Full Name: " + fullName + "\n" +
                "Age: " + age + "\n" +
                "Medical History: " + medicalHistory + "\n" +
                "Insurance Information: " + insuranceInfo + "\n" +
                "Emergency Contact: " + emergencyContact;

        // For testing, display patient info in a toast
        // Replace with appropriate UI updates or database operations
        // Toast.makeText(this, patientInfo, Toast.LENGTH_LONG).show();
        // For now, just print to logcat
        Toast.makeText(this, patientInfo, Toast.LENGTH_LONG).show();
        System.out.println(patientInfo);
    }

    private void addPatientData(String fullName, String age, String medicalHistory, String insuranceInfo, String emergencyContact) {
        // Get the current user's ID & email
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            String userEmail = currentUser.getEmail();

            // Create a map to store patient data
            Map<String, Object> patientData = new HashMap<>();

            // Add patient data to the map
            patientData.put("id", userId);
            patientData.put("email", userEmail);
            patientData.put("fullName", fullName);
            patientData.put("age", age);
            patientData.put("medicalHistory", medicalHistory);
            patientData.put("insuranceInfo", insuranceInfo);
            patientData.put("emergencyContact", emergencyContact);

            db.collection("users")
                    .add(patientData)
                    .addOnSuccessListener(documentReference -> {
                        // Patient data added successfully
                        Toast.makeText(this, "Patient data added successfully", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        // Error adding patient data
                        Toast.makeText(this, "Error adding patient data", Toast.LENGTH_SHORT).show();
                        Log.e("AddPatientDataError", "Error adding patient data", e);
                    });
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }
}
