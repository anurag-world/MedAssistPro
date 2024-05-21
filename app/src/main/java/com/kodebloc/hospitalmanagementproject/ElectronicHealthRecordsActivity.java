package com.kodebloc.hospitalmanagementproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kodebloc.hospitalmanagementproject.util.SecurityUtils;

import java.util.HashMap;
import java.util.Map;

public class ElectronicHealthRecordsActivity extends AppCompatActivity {
    private static final String TAG = "ElectronicHealthRecordsActivity";
    String fullName;
    private TextView tvPatientName;
    private EditText etMedicalHistory, etDiagnoses, etMedications, etLabResults, etTreatmentPlans;
    private Button btnSave;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_electronic_health_records);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_electronic_health_records), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top + 120, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set up the action bar with title and back button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("All Appointments");
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

        // Initialize Firestore and Auth
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize UI elements
        tvPatientName = findViewById(R.id.tvPatientName);
        etMedicalHistory = findViewById(R.id.etMedicalHistory);
        etDiagnoses = findViewById(R.id.etDiagnoses);
        etMedications = findViewById(R.id.etMedications);
        etLabResults = findViewById(R.id.etLabResults);
        etTreatmentPlans = findViewById(R.id.etTreatmentPlans);
        btnSave = findViewById(R.id.btnSave);

        // Get current user
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
            fetchPatientName(userId);
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
        }

        // Initialize encryption key
        try {
            SecurityUtils.generateKey(); // Generate the encryption key once
        } catch (Exception e) {
            Log.e("GenerateEncryptionError", "An error occurred:", e);
            Toast.makeText(this, "Error initializing encryption key", Toast.LENGTH_SHORT).show();
        }

        btnSave.setOnClickListener(v -> saveEHR());
    }

    // Handle back button press
    @Override
    public boolean onSupportNavigateUp() {
        // Handle the action bar's up button
        finish();
        return true;
    }

    private void fetchPatientName(String userId) {
        db.collection("users").document(userId).get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        fullName = document.getString("fullName");
                        tvPatientName.setText(fullName);
                    } else {
                        Log.d(TAG, "No such document");
                        Toast.makeText(ElectronicHealthRecordsActivity.this, "No user data found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                    Toast.makeText(ElectronicHealthRecordsActivity.this, "Failed to fetch user data", Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void saveEHR() {
        String medicalHistory = etMedicalHistory.getText().toString().trim();
        String diagnoses = etDiagnoses.getText().toString().trim();
        String medications = etMedications.getText().toString().trim();
        String labResults = etLabResults.getText().toString().trim();
        String treatmentPlans = etTreatmentPlans.getText().toString().trim();

        // Validate input
        if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(medicalHistory) || TextUtils.isEmpty(diagnoses) ||
                TextUtils.isEmpty(medications) || TextUtils.isEmpty(labResults) ||
                TextUtils.isEmpty(treatmentPlans)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Encrypt the data
            String encryptedMedicalHistory = SecurityUtils.encryptData(medicalHistory);
            String encryptedDiagnoses = SecurityUtils.encryptData(diagnoses);
            String encryptedMedications = SecurityUtils.encryptData(medications);
            String encryptedLabResults = SecurityUtils.encryptData(labResults);
            String encryptedTreatmentPlans = SecurityUtils.encryptData(treatmentPlans);

            // Create a map to hold EHR details
            Map<String, Object> ehr = new HashMap<>();
            ehr.put("patientName", fullName);
            ehr.put("medicalHistory", encryptedMedicalHistory);
            ehr.put("diagnoses", encryptedDiagnoses);
            ehr.put("medications", encryptedMedications);
            ehr.put("labResults", encryptedLabResults);
            ehr.put("treatmentPlans", encryptedTreatmentPlans);
            ehr.put("uid", userId);

            // Save EHR details to Firestore
            db.collection("ehr")
                    .add(ehr)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(ElectronicHealthRecordsActivity.this, "EHR saved successfully", Toast.LENGTH_LONG).show();
                        // Redirect to DashboardActivity
                        redirectToDashboard();
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure
                        Toast.makeText(ElectronicHealthRecordsActivity.this, "Error saving EHR", Toast.LENGTH_SHORT).show();
                    });
        } catch (Exception e) {
            Toast.makeText(this, "Error encrypting data", Toast.LENGTH_SHORT).show();
            Log.e("SaveEHRError", "An error occurred:", e);
        }
    }

    private void redirectToDashboard() {
        // Redirect to View All Appointments Activity
        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
        finish();
    }
}
