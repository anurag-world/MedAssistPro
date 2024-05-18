package com.kodebloc.hospitalmanagementproject;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.kodebloc.hospitalmanagementproject.model.UserCallback;
import com.kodebloc.hospitalmanagementproject.model.UsersData;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AppointmentSchedulingActivity extends AppCompatActivity {

    private TextView patientNameUI;
    private EditText etAppointmentDate, etAppointmentTime;
    private Spinner spinnerDoctor;
    private Button btnBookAppointment;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private UsersData usersData;
    private String fullName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_appointment_scheduling);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_appointment_scheduling), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top + 120, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set up the action bar with title and back button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Schedule an Appointment");
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

        // Initialize Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Get the current user's data
        currentUser = mAuth.getCurrentUser();
        usersData = new UsersData();

        // Initialize UI elements
        patientNameUI = findViewById(R.id.patientNameUI);
        spinnerDoctor = findViewById(R.id.spinnerDoctor);
        etAppointmentDate = findViewById(R.id.etAppointmentDate);
        etAppointmentTime = findViewById(R.id.etAppointmentTime);
        btnBookAppointment = findViewById(R.id.btnBookAppointment);

        // Retrieve data and handle it using a callback
        usersData.getUsers(new UserCallback() {
            @Override
            public void onCallback(Map<String, Object> userData) {
                if (userData != null) {
                    // Handle the retrieved data
                    Log.d("TAG", "User Data: " + userData);
                    // Example: Get full name
                    fullName = Objects.requireNonNull(userData.get("fullName")).toString();
                    // Set the patient name in the UI
                    String getName = getString(R.string.booking_patient_name, fullName);
                    patientNameUI.setText(getName);
                } else {
                    Log.d("TAG", "No user data found");
                }
            }
        });

        // Set up spinner with doctors/specialists/departments
        String[] doctors = {"Dr. Smith - Cardiology", "Dr. Jones - Neurology", "Dr. Brown - Orthopedics"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, doctors);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDoctor.setAdapter(adapter);

        // Set up date picker dialog
        etAppointmentDate.setOnClickListener(v -> showDatePickerDialog());

        // Set up time picker dialog
        etAppointmentTime.setOnClickListener(v -> showTimePickerDialog());

        // Book appointment button click listener
        btnBookAppointment.setOnClickListener(v -> bookAppointment());

    }

    // Handle back button press
    @Override
    public boolean onSupportNavigateUp() {
        // Handle the action bar's up button
        finish();
        return true;
    }

    // Method to show date picker dialog
    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    String dateString = getString(R.string.date_format, day, month + 1, year);
                    etAppointmentDate.setText(dateString);
                }, year, month, day);
        datePickerDialog.show();
    }

    // Method to show time picker dialog
    private void showTimePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute1) -> {
                    if (hourOfDay < hour || (hourOfDay == hour && minute1 < minute)) {
                        Toast.makeText(this, "Please select a future time", Toast.LENGTH_SHORT).show();
                    } else {
                        String ampm = hourOfDay < 12 ? "AM" : "PM";
                        String timeString = getString(R.string.time_format, hourOfDay, minute1, ampm);
                        etAppointmentTime.setText(timeString);
                    }
                }, hour, minute, false);

        timePickerDialog.show();
    }

    // Method to book an appointment
    private void bookAppointment() {
        String doctor = spinnerDoctor.getSelectedItem().toString();
        String appointmentDate = etAppointmentDate.getText().toString().trim();
        String appointmentTime = etAppointmentTime.getText().toString().trim();

        // Validate input
        if (fullName.isEmpty() || appointmentDate.isEmpty() || appointmentTime.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Create a map to hold appointment details
            Map<String, Object> appointmentData = new HashMap<>();

            // Add appointment details to the map
            appointmentData.put("userId", userId);
            appointmentData.put("patientName", fullName);
            appointmentData.put("doctor", doctor);
            appointmentData.put("appointmentDate", appointmentDate);
            appointmentData.put("appointmentTime", appointmentTime);

            // Save appointment details to Firestore
            db.collection("appointments")
                    .add(appointmentData)
                    .addOnSuccessListener(documentReference -> {
                        Log.d("AppointmentSchedulingActivity", "Appointment booked successfully");
                        Toast.makeText(this, "Appointment booked successfully", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Log.e("AppointmentSchedulingActivity", "Error booking appointment", e);
                        Toast.makeText(this, "Error booking appointment", Toast.LENGTH_SHORT).show();
                    });

        } else {
            // User is not signed in, show a message
            Toast.makeText(this, "Please sign in to book an appointment", Toast.LENGTH_SHORT).show();
        }

        // Log the booked appointment details
        String appointmentInfo = "Appointment booked for " + fullName + "\n" +
                "With: " + doctor + "\n" +
                "On: " + appointmentDate + " at " + appointmentTime;
        System.out.println(appointmentInfo);
    }

}
