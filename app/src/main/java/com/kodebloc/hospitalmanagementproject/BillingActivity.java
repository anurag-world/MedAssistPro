package com.kodebloc.hospitalmanagementproject;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class BillingActivity extends AppCompatActivity {
    private TextView tvBillingInfo;
    private Button btnPayNow;

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

        // Initialize UI elements
        tvBillingInfo = findViewById(R.id.tvBillingInfo);
        btnPayNow = findViewById(R.id.btnPayNow);

        // Set the billing information
        String billingInfo = "Invoice for Services Rendered:\n\n" +
                "Patient Name: John Doe\n" +
                "Service Date: 2024-05-01\n\n" +
                "Services:\n" +
                "1. Consultation with Dr. Smith - Cardiology: $150\n" +
                "2. X-Ray: $100\n" +
                "3. Blood Test: $50\n\n" +
                "Total Amount: $300\n" +
                "Insurance Coverage: $200\n\n" +
                "Outstanding Bill Amount: $100";

        tvBillingInfo.setText(billingInfo);

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

    private void initiatePayment() {
        // Placeholder method for future implementation
    }
}
