package com.mv.appointment.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
// import com.mv.appointment.domain.entities.Appointment;

import java.time.LocalDateTime;

public class AppointmentDTO {

    private Long id;

    private String patientName;
    private String doctorName;
    private String specialty;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime appointmentDateTime;

    public AppointmentDTO() {
    }

    public AppointmentDTO(Long id, String patientName, String doctorName, String specialty, LocalDateTime appointmentDateTime) {
        this.id = id;
        this.patientName = patientName;
        this.doctorName = doctorName;
        this.specialty = specialty;
        this.appointmentDateTime = appointmentDateTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public LocalDateTime getAppointmentDateTime() {
        return appointmentDateTime;
    }

    public void setAppointmentDateTime(LocalDateTime appointmentDateTime) {
        this.appointmentDateTime = appointmentDateTime;
    }
}
