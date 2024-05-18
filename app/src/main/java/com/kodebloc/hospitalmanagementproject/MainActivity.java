package com.kodebloc.hospitalmanagementproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.FirebaseApp;
import com.kodebloc.hospitalmanagementproject.login.LoginActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top + 120, systemBars.right, systemBars.bottom);
            return insets;
        });

        FirebaseApp.initializeApp(this);

        // Initialize button
        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnAppointmentScheduling = findViewById(R.id.btnAppointmentScheduling);

        // Set click listener to redirect to Login Screen
        btnLogin.setOnClickListener(v -> {
            // Open Login Activity
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        });

        // Set click listener to redirect to Booking Appointment Screen
        btnAppointmentScheduling.setOnClickListener(v -> {
            // Open Appointment Scheduling Activity
            startActivity(new Intent(MainActivity.this, AppointmentSchedulingActivity.class));
        });
    }
}
