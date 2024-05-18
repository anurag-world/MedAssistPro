package com.kodebloc.hospitalmanagementproject;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.kodebloc.hospitalmanagementproject.model.UserCallback;
import com.kodebloc.hospitalmanagementproject.model.UsersData;

import java.util.Map;
import java.util.Objects;

public class DashboardActivity extends AppCompatActivity {
    private TextView dashboardWelcomeText;
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
    }
}
