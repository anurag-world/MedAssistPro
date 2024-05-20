package com.kodebloc.hospitalmanagementproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kodebloc.hospitalmanagementproject.login.LoginActivity;
import com.kodebloc.hospitalmanagementproject.model.UserCallback;
import com.kodebloc.hospitalmanagementproject.model.UsersData;
import com.kodebloc.hospitalmanagementproject.profile.ProfileActivity;

import java.util.Map;
import java.util.Objects;

public class DashboardActivity extends AppCompatActivity {
    private TextView dashboardWelcomeText;
    private Button btnProfile;
    private Button btnEHR;
    private Button btnAppointmentScheduling;
    private Button btnViewAllAppointments;
    private Button btnLogout;
    private UsersData usersData;
    private String fullName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_dashboard), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top + 120, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set up the action bar with title and back button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Dashboard");
        }

        // Initialize Firebase Auth
        usersData = new UsersData();

        // Initialize UI elements
        dashboardWelcomeText = findViewById(R.id.dashboardWelcomeText);
        btnAppointmentScheduling = findViewById(R.id.btnAppointmentScheduling);
        btnViewAllAppointments = findViewById(R.id.btnViewAllAppointments);
        btnLogout = findViewById(R.id.btnLogout);
        btnEHR = findViewById(R.id.btnEHR);
        btnProfile = findViewById(R.id.btnProfile);

        // Retrieve data and handle it using a callback
        usersData.getUsers(new UserCallback() {
            @Override
            public void onCallback(Map<String, Object> userData) {
                if (userData != null) {
                    // Handle the retrieved data
                    Log.d("TAG", "User Data: " + userData);
                    // Example: Get full name
                    fullName = Objects.requireNonNull(userData.get("fullName")).toString();

                    // Update UI with full name
                    String welcomeText = getString(R.string.welcome_text, fullName);
                    dashboardWelcomeText.setText(welcomeText);
                } else {
                    Log.e("Error", "No user data found");
                }
            }
        });

        // Set click listener to redirect to Profile Screen
        btnProfile.setOnClickListener(v -> {
            // Open Profile Activity
            Intent intent = new Intent(DashboardActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        // Set click listener to check EHR and redirect
        btnEHR.setOnClickListener(v -> {
            checkEHRAndRedirect();
        });

        // Set click listener to redirect to Booking Appointment Screen
        btnAppointmentScheduling.setOnClickListener(v -> {
            // Open Appointment Scheduling Activity
            Intent intent = new Intent(DashboardActivity.this, AppointmentSchedulingActivity.class);
            startActivity(intent);
        });

        // Set click listener to redirect to View All Appointments Screen
        btnViewAllAppointments.setOnClickListener(v -> {
            // Open View All Appointments Activity
            Intent intent = new Intent(DashboardActivity.this, ViewAllAppointmentsActivity.class);
            startActivity(intent);
        });

        // Set click listener for the logout button
        btnLogout.setOnClickListener(v -> {
            usersData.logout();
            // After signing out, redirect the user to the login activity
            Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish(); // Close the current activity
        });
    }

    private void checkEHRAndRedirect() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("ehr").whereEqualTo("uid", userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            Toast.makeText(DashboardActivity.this, "Health records already exist", Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intent = new Intent(DashboardActivity.this, ElectronicHealthRecordsActivity.class);
                            startActivity(intent);
                        }
                    } else {
                        Log.e("Error", "Error checking EHR", task.getException());
                        Toast.makeText(DashboardActivity.this, "Error checking EHR", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
