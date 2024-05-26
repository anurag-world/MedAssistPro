package com.kodebloc.hospitalmanagementproject;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.kodebloc.hospitalmanagementproject.adapters.AppointmentsAdapter;
import com.kodebloc.hospitalmanagementproject.model.Appointment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class ViewAllAppointmentsActivity extends AppCompatActivity {

    private static final String TAG = "ViewAllAppointmentsActivity";
    private RecyclerView recyclerViewUpcomingAppointments;
    private RecyclerView recyclerViewCompletedAppointments;
    private AppointmentsAdapter upcomingAdapter;
    private AppointmentsAdapter completedAdapter;
    private List<Appointment> upcomingAppointmentsList;
    private List<Appointment> completedAppointmentsList;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_all_appointments);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_view_all_appointments), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top + 120, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set up the action bar with title and back button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("All Appointments");
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

        // Initialize Firestore and Auth
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Initialize UI elements
        recyclerViewUpcomingAppointments = findViewById(R.id.recyclerViewUpcomingAppointments);
        recyclerViewCompletedAppointments = findViewById(R.id.recyclerViewCompletedAppointments);
        recyclerViewUpcomingAppointments.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCompletedAppointments.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the appointment list and adapter
        upcomingAppointmentsList = new ArrayList<>();
        completedAppointmentsList = new ArrayList<>();
        upcomingAdapter = new AppointmentsAdapter(this, upcomingAppointmentsList);
        completedAdapter = new AppointmentsAdapter(this, completedAppointmentsList);
        recyclerViewUpcomingAppointments.setAdapter(upcomingAdapter);
        recyclerViewCompletedAppointments.setAdapter(completedAdapter);

        // Fetch appointments for the current user
        fetchAppointments();
    }

    // Handle back button press
    @Override
    public boolean onSupportNavigateUp() {
        // Handle the action bar's up button
        finish();
        return true;
    }

    // Fetch appointments from Firestore
    private void fetchAppointments() {
        String userId = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        db.collection("appointments")
                .whereEqualTo("uid", userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Appointment appointment = document.toObject(Appointment.class);
                                appointment.setId(document.getId()); // Set the document ID
                                categorizeAppointment(appointment);
                            }

                            // Sort the list by appointment date and time
                            sortAppointments(upcomingAppointmentsList);
                            sortAppointments(completedAppointmentsList);

                            // Notify the adapter that the data has changed
                            upcomingAdapter.notifyDataSetChanged();
                            completedAdapter.notifyDataSetChanged();
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                            Toast.makeText(ViewAllAppointmentsActivity.this, "Error getting appointments.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // Categorize appointments based on their date and time
    private void categorizeAppointment(Appointment appointment) {
        Date now = new Date();
        if (appointment.getAppointmentDateTime().before(now)) {
            completedAppointmentsList.add(appointment);
        } else {
            upcomingAppointmentsList.add(appointment);
        }
    }

    // Sort the list of appointments by date and time
    private void sortAppointments(List<Appointment> appointments) {
        Collections.sort(appointments, new Comparator<Appointment>() {
            @Override
            public int compare(Appointment a1, Appointment a2) {
                return a1.getAppointmentDateTime().compareTo(a2.getAppointmentDateTime());
            }
        });
    }
}
