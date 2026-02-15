package com.mv.appointment.dtos;

import com.mv.appointment.domain.enums.AppointmentStatus;

public class AppointmentStatusDTO {

    private AppointmentStatus status;

    public AppointmentStatus getStatus() { return status; }
    public void setStatus(AppointmentStatus status) { this.status = status; }
}
