package com.mv.appointment.controllers;

import com.mv.appointment.dtos.AppointmentDTO;
import com.mv.appointment.dtos.AppointmentCreateDTO;
import com.mv.appointment.dtos.AppointmentStatusDTO;
import com.mv.appointment.dtos.AppointmentUpdateDTO;

import org.springframework.http.ResponseEntity;

import java.util.List;

public interface AppointmentController {
    ResponseEntity<AppointmentDTO> create(AppointmentCreateDTO appointmentDTO);
    ResponseEntity<List<AppointmentDTO>> findAll();
    ResponseEntity<AppointmentDTO> findById(Long id);
    ResponseEntity<AppointmentDTO> update(Long id, AppointmentUpdateDTO appointmentDto);
    ResponseEntity<AppointmentDTO> updateStatus(Long id, AppointmentStatusDTO newStatus);
    ResponseEntity<Void> delete(Long id);
}
