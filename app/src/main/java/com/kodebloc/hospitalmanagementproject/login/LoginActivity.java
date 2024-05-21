package com.kodebloc.hospitalmanagementproject.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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
import com.kodebloc.hospitalmanagementproject.DashboardActivity;
import com.kodebloc.hospitalmanagementproject.PatientRegistrationActivity;
import com.kodebloc.hospitalmanagementproject.R;

public class LoginActivity extends AppCompatActivity {
    // Declare variables
    private static final String TAG = "LoginActivity";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_login), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top + 120, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set up the action bar with title and back button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Login");
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


        // Initialize views
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        progressBar = findViewById(R.id.progressBar);

        // Set click listeners for login and register buttons
        loginButton.setOnClickListener(v -> loginUser());
    }

    // Handle back button press
    @Override
    public boolean onSupportNavigateUp() {
        // Handle the action bar's up button
        finish();
        return true;
    }

    // Check if user is already logged in
    private void loginUser() {
        // Get user input
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Check if email and password are empty
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show progress bar
        progressBar.setVisibility(View.VISIBLE);

        // Sign in user
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    progressBar.setVisibility(View.GONE);
                    // Check if sign in was successful
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with signed-in user's information
                        Log.d(TAG, "signInWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        // User does not exist, register new user
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        registerNewUser(email, password);
                    }
                });
    }

    // Register new user
    private void registerNewUser(String email, String password) {
        progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        // New user registered, redirect to patient registration
                        Log.d(TAG, "createUserWithEmail:success");
                        Intent intent = new Intent(LoginActivity.this, PatientRegistrationActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // Registration failed, display error message
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(LoginActivity.this, "Registration Failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Update UI based on user status
    private void updateUI(FirebaseUser user) {
        if (user != null) {
            String uid = user.getUid();
            Log.v(TAG, uid);

            // Check if user data exists in Firestore
            db.collection("users").document(uid).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // User data exists, redirect to Dashboard
                            Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // User data does not exist, redirect to Patient Registration
                            Intent intent = new Intent(LoginActivity.this, PatientRegistrationActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error checking user data", e);
                        Toast.makeText(LoginActivity.this, "Error checking user data", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Log.v(TAG, "User is null");
        }
    }
}
