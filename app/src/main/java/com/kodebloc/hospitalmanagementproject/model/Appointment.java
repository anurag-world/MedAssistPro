package com.kodebloc.hospitalmanagementproject.model;

public class Appointment {
    private String patientName;
    private String doctor;
    private String appointmentDate;
    private String appointmentTime;

    // Default constructor required for calls to DataSnapshot.getValue(Appointment.class)
    public Appointment() {
    }

    public Appointment(String patientName, String doctor, String appointmentDate, String appointmentTime) {
        this.patientName = patientName;
        this.doctor = doctor;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
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
}
