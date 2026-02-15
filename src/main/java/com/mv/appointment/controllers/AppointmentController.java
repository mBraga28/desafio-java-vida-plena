package com.mv.appointment.controllers;

import com.mv.appointment.dtos.AppointmentDTO;
import org.springframework.http.ResponseEntity;

public interface AppointmentController {
    ResponseEntity<AppointmentDTO> create(AppointmentDTO appointmentDTO);
}
