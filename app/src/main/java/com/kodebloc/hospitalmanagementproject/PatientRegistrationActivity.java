package com.kodebloc.hospitalmanagementproject;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PatientRegistrationActivity extends AppCompatActivity {
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

        // Initialize UI elements
        etFullName = findViewById(R.id.etFullName);
        etAge = findViewById(R.id.etAge);
        etMedicalHistory = findViewById(R.id.etMedicalHistory);
        etInsuranceInfo = findViewById(R.id.etInsuranceInfo);
        etEmergencyContact = findViewById(R.id.etEmergencyContact);
        btnRegister = findViewById(R.id.btnRegister);

        // Register patient button click listener
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle patient registration logic here
                String fullName = etFullName.getText().toString().trim();
                String age = etAge.getText().toString().trim();
                String medicalHistory = etMedicalHistory.getText().toString().trim();
                String insuranceInfo = etInsuranceInfo.getText().toString().trim();
                String emergencyContact = etEmergencyContact.getText().toString().trim();

                // Perform validation and database operations here
                // For simplicity, let's just display the entered data
                displayPatientInfo(fullName, age, medicalHistory, insuranceInfo, emergencyContact);
            }
        });
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
}
