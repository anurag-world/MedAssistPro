package com.kodebloc.hospitalmanagementproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize button
        Button btnPatientRegister = findViewById(R.id.btnPatientRegister);
        Button btnAppointmentScheduling = findViewById(R.id.btnAppointmentScheduling);

        // Set click listener to redirect to Patient Registration Screen
        btnPatientRegister.setOnClickListener(v -> {
            // Open Patient Registration Activity
            startActivity(new Intent(MainActivity.this, PatientRegistrationActivity.class));
        });

        // Set click listener to redirect to Booking Appointment Screen
        btnAppointmentScheduling.setOnClickListener(v -> {
            // Open Appointment Scheduling Activity
            startActivity(new Intent(MainActivity.this, AppointmentSchedulingActivity.class));
        });
    }
}
