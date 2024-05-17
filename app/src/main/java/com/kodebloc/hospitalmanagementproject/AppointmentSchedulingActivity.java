package com.kodebloc.hospitalmanagementproject;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;
import java.util.Calendar;

public class AppointmentSchedulingActivity extends AppCompatActivity {

    private EditText etPatientName, etAppointmentDate, etAppointmentTime;
    private Spinner spinnerDoctor;
    private Button btnBookAppointment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_appointment_scheduling);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_appointment_scheduling), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI elements
        etPatientName = findViewById(R.id.etPatientName);
        spinnerDoctor = findViewById(R.id.spinnerDoctor);
        etAppointmentDate = findViewById(R.id.etAppointmentDate);
        etAppointmentTime = findViewById(R.id.etAppointmentTime);
        btnBookAppointment = findViewById(R.id.btnBookAppointment);

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
                    System.out.println(hour);
                    System.out.println(hourOfDay);
                    if (hourOfDay < hour || (hourOfDay == hour && minute1 < minute)) {
                        Toast.makeText(this, "Please select a future time", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        String ampm = hourOfDay < 12 ? "AM" : "PM";
                        String timeString = getString(R.string.time_format, hourOfDay, minute1, ampm);
                        etAppointmentTime.setText(timeString);
                    }
                }, hour, minute, false);

        timePickerDialog.show();
    }

    // Method to book an appointment
    private void bookAppointment() {
        String patientName = etPatientName.getText().toString().trim();
        String doctor = spinnerDoctor.getSelectedItem().toString();
        String appointmentDate = etAppointmentDate.getText().toString().trim();
        String appointmentTime = etAppointmentTime.getText().toString().trim();

        // Validate input
        if (patientName.isEmpty() || appointmentDate.isEmpty() || appointmentTime.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Simulate booking appointment (in a real app, you would save this to a database)
        String appointmentInfo = "Appointment booked for " + patientName + "\n" +
                "With: " + doctor + "\n" +
                "On: " + appointmentDate + " at " + appointmentTime;
        System.out.println(appointmentInfo);
        Toast.makeText(this, appointmentInfo, Toast.LENGTH_LONG).show();
    }

}
