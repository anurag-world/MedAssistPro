package com.kodebloc.hospitalmanagementproject.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Appointment {
    private String id;
    private String patientName;
    private String doctor;
    private String appointmentDate;
    private String appointmentTime;
    private String userId;

    // Default constructor required for calls to DataSnapshot.getValue(Appointment.class)
    public Appointment() {
    }

    public Appointment(String id, String patientName, String doctor, String appointmentDate, String appointmentTime, String userId) {
        this.id = id;
        this.patientName = patientName;
        this.doctor = doctor;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getDoctor() {
        return doctor;
    }

    public void setDoctor(String doctor) {
        this.doctor = doctor;
    }

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(String appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getAppointmentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        try {
            return sdf.parse(appointmentDate + " " + appointmentTime);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
