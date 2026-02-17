package com.mv.appointment.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mv.appointment.domain.enums.AppointmentStatus;

import java.time.LocalDateTime;

public class AppointmentUpdateDTO {

    private String doctorName;
    private String specialty;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime appointmentDateTime;

    public AppointmentUpdateDTO() {
    }

    public AppointmentUpdateDTO(String doctorName, String specialty, LocalDateTime appointmentDateTime) {
        this.doctorName = doctorName;
        this.specialty = specialty;
        this.appointmentDateTime = appointmentDateTime;
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
