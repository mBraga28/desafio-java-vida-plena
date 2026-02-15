package com.mv.appointment.services;

import com.mv.appointment.domain.entities.Appointment;
import com.mv.appointment.domain.enums.AppointmentStatus;
import com.mv.appointment.dtos.AppointmentDTO;
import com.mv.appointment.exceptions.BusinessException;
import com.mv.appointment.repositories.AppointmentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;

    public AppointmentService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

   public Appointment create(Appointment appointment) {

    if (appointment.getAppointmentDateTime() == null) {
        throw new BusinessException("Appointment date/time must be provided.");
    }

    if (appointment.getAppointmentDateTime().isBefore(LocalDateTime.now())) {
        throw new BusinessException("Appointment date/time cannot be in the past.");
    }

        appointment.setStatus(AppointmentStatus.SCHEDULED);

        return appointmentRepository.save(appointment);
    } 
}
