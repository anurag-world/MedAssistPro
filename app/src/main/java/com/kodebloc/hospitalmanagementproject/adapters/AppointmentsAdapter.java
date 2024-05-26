package com.kodebloc.hospitalmanagementproject.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kodebloc.hospitalmanagementproject.R;
import com.kodebloc.hospitalmanagementproject.model.Appointment;

import java.util.List;

public class AppointmentsAdapter extends RecyclerView.Adapter<AppointmentsAdapter.AppointmentViewHolder> {

    private List<Appointment> appointmentList;
    private Context context;

    public AppointmentsAdapter(Context context, List<Appointment> appointmentList) {
        this.context = context;
        this.appointmentList = appointmentList;
    }

    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_appointment, parent, false);
        return new AppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        Appointment appointment = appointmentList.get(position);
        holder.tvPatientName.setText(appointment.getPatientName());
        holder.tvDoctor.setText(appointment.getDoctor());
        holder.tvAppointmentDate.setText(appointment.getAppointmentDate());
        holder.tvAppointmentTime.setText(appointment.getAppointmentTime());

        // Set click listener for delete icon
        holder.ivDelete.setOnClickListener((View.OnClickListener) v -> new AlertDialog.Builder(context)
                .setTitle("Delete Appointment")
                .setMessage("Are you sure you want to delete this appointment?")
                .setPositiveButton("Yes", (dialog, which) -> deleteAppointment(appointment, position))
                .setNegativeButton("No", null)
                .show());
    }

    private void deleteAppointment(Appointment appointment, int position) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("appointments").document(appointment.getId())
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            appointmentList.remove(position);
                            notifyItemRemoved(position);
                            Toast.makeText(context, "Appointment deleted successfully.", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(context, "Error deleting appointment", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }

    static class AppointmentViewHolder extends RecyclerView.ViewHolder {
        TextView tvPatientName, tvDoctor, tvAppointmentDate, tvAppointmentTime;
        ImageView ivDelete;

        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPatientName = itemView.findViewById(R.id.tvPatientName);
            tvDoctor = itemView.findViewById(R.id.tvDoctor);
            tvAppointmentDate = itemView.findViewById(R.id.tvAppointmentDate);
            tvAppointmentTime = itemView.findViewById(R.id.tvAppointmentTime);
            ivDelete = itemView.findViewById(R.id.ivDelete);
        }
    }
}
