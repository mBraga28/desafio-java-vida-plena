package com.mv.appointment.controllers;

import com.mv.appointment.dtos.AppointmentDTO;
import com.mv.appointment.dtos.AppointmentStatusDTO;

import org.springframework.http.ResponseEntity;

import java.util.List;

public interface AppointmentController {
    ResponseEntity<AppointmentDTO> create(AppointmentDTO appointmentDTO);
    ResponseEntity<List<AppointmentDTO>> findAll();
    ResponseEntity<AppointmentDTO> findById(Long id);
    ResponseEntity<AppointmentDTO> update(Long id, AppointmentDTO newStatus);
    ResponseEntity<AppointmentDTO> updateStatus(Long id, AppointmentStatusDTO newStatus);
    ResponseEntity<Void> delete(Long id);
}
