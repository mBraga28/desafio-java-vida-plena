package com.mv.appointment.services;

import com.mv.appointment.domain.entities.Appointment;
import com.mv.appointment.domain.enums.AppointmentStatus;
import com.mv.appointment.dtos.AppointmentDTO;
import com.mv.appointment.exceptions.BusinessConflictException;
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

    //@Transactional(readOnly = true)
    public Appointment findById(Long id) {
        Optional<Appointment> appointment = appointmentRepository.findById(id);
        return appointment.orElseThrow(() -> new ObjectNotFoundException("Appointment not found with the ID:" + id));
    }

    public Appointment update(Appointment appointment) {
        Appointment existing = findById(appointment.getId());

        if (existing.getStatus() != AppointmentStatus.SCHEDULED) {
            throw new BusinessConflictException("Only SCHEDULED appointments can be updated.");
        }

        if (appointment.getAppointmentDateTime() == null) {
            throw new BusinessException("Appointment date/time must be provided.");
        }
        if (appointment.getAppointmentDateTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Appointment date/time cannot be in the past.");
        }

        existing.setAppointmentDateTime(appointment.getAppointmentDateTime());
        existing.setDoctorName(appointment.getDoctorName());
        existing.setSpecialty(appointment.getSpecialty());

        // só atualiza o status se vier no payload (evita sobrescrever com null)
        if (appointment.getStatus() != null) {
            existing.setStatus(appointment.getStatus());
        }

        return appointmentRepository.save(existing);
    }

    public Appointment updateStatus(Long id, AppointmentStatus newStatus) {
        Appointment existing = findById(id);

        if (existing.getStatus() == AppointmentStatus.COMPLETED) {
            throw new BusinessConflictException("Completed appointments cannot be updated.");
        }

        if (existing.getStatus() == AppointmentStatus.CANCELED &&
                (newStatus == AppointmentStatus.IN_PROGRESS || newStatus == AppointmentStatus.COMPLETED)) {
            throw new BusinessConflictException("Canceled appointments cannot be moved to IN_PROGRESS or COMPLETED.");
        }

        existing.setStatus(newStatus);
        return appointmentRepository.save(existing);
    }

    public void delete(Long id) {
        Appointment existing = findById(id);

        if (existing.getStatus() == AppointmentStatus.COMPLETED) {
            throw new BusinessConflictException("Completed appointments cannot be removed.");
        }

        appointmentRepository.deleteById(id);
    }
}
