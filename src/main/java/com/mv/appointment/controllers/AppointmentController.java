package com.mv.appointment.controllers;

import com.mv.appointment.dtos.AppointmentDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface AppointmentController {
    ResponseEntity<AppointmentDTO> create(AppointmentDTO appointmentDTO);
    ResponseEntity<List<AppointmentDTO>> findAll();
    ResponseEntity<AppointmentDTO> findById(Long id);
}
