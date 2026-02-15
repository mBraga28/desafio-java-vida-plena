package com.mv.appointment.controllers.impl;

import com.mv.appointment.controllers.AppointmentController;
import com.mv.appointment.domain.entities.Appointment;
import com.mv.appointment.dtos.AppointmentDTO;
import com.mv.appointment.services.AppointmentService;

import java.net.URI;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
// import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping(value = "appointment")
public class AppointmentControllerImpl implements AppointmentController {

    private final AppointmentService appointmentService;

    private final ModelMapper modelMapper;

    public AppointmentControllerImpl(AppointmentService appointmentService, ModelMapper modelMapper) {
        this.appointmentService = appointmentService;
        this.modelMapper = modelMapper;
    }

    @Override
    @PostMapping
    public ResponseEntity<AppointmentDTO> create(@RequestBody AppointmentDTO appointmentDTO) {
        Appointment createdAppointment = modelMapper.map(appointmentDTO, Appointment.class);
        Appointment savedAppointment = appointmentService.create(createdAppointment);
        return ResponseEntity.status(HttpStatus.CREATED).body(modelMapper.map(savedAppointment, AppointmentDTO.class));
    }
}
