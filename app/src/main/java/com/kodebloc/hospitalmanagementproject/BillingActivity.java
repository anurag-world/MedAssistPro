package com.kodebloc.hospitalmanagementproject;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
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

public class BillingActivity extends AppCompatActivity {
    private static final String TAG = "BillingActivity";
    private TextView tvBillingInfo;
    private Button btnPayNow;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_billing);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_billing), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top + 120, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set up the action bar with title and back button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Billing");
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

        // Initialize Firebase instances
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize UI elements
        tvBillingInfo = findViewById(R.id.tvBillingInfo);
        btnPayNow = findViewById(R.id.btnPayNow);

        // Get current user
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            fetchPatientName(userId);
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
        }

        // Set click listener for the pay now button
        btnPayNow.setOnClickListener(v -> {
            // Placeholder for future implementation of payment gateway integration
            initiatePayment();
        });
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
                            String fullName = document.getString("fullName");
                            updateBillingInfo(fullName);
                        } else {
                            Log.d(TAG, "No such document");
                            Toast.makeText(BillingActivity.this, "No user data found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                        Toast.makeText(BillingActivity.this, "Failed to fetch user data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateBillingInfo(String fullName) {
        String billingInfo = "Billing Information:\n\n" +
                "Patient Name: " + fullName + "\n\n" +
                "Doctor Fees: ₹15,000\n" +
                "Lab Tests: ₹11,250\n" +
                "Medications: ₹3,750\n" +
                "Room Charges: ₹22,500";

        tvBillingInfo.setText(billingInfo);
    }

    private void initiatePayment() {
        // Placeholder method for future implementation
    }
}
