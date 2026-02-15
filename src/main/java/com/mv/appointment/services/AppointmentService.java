package com.mv.appointment.services;

import com.mv.appointment.domain.entities.Appointment;
import com.mv.appointment.domain.enums.AppointmentStatus;
import com.mv.appointment.dtos.AppointmentDTO;
import com.mv.appointment.exceptions.BusinessException;
import com.mv.appointment.exceptions.ObjectNotFoundException;
import com.mv.appointment.repositories.AppointmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

    @Transactional(readOnly = true)
    public List<Appointment> findAll() {
        List<Appointment> list = appointmentRepository.findAll();

        if (list.isEmpty()) {
            throw new ObjectNotFoundException("Appointment not found");
        }

        return list;
    }

    @Transactional(readOnly = true)
    public Appointment findById(Long id) {
        Optional<Appointment> appointment = appointmentRepository.findById(id);
        return appointment.orElseThrow(() -> new ObjectNotFoundException("Appointment not found with the ID:" + id));
    }

}
