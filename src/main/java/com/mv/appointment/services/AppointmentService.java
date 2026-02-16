package com.mv.appointment.services;

import com.mv.appointment.domain.entities.Appointment;
import com.mv.appointment.domain.enums.AppointmentStatus;
import com.mv.appointment.exceptions.BusinessConflictException;
import com.mv.appointment.exceptions.BusinessException;
import com.mv.appointment.exceptions.ObjectNotFoundException;
import com.mv.appointment.repositories.AppointmentRepository;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    public Appointment create(Appointment appointment) {

        validateAppointment(appointment);
        validateAppointmentDateTime(appointment);

        appointment.setStatus(AppointmentStatus.SCHEDULED);

        return appointmentRepository.save(appointment);
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST','DOCTOR')")
    @Transactional(readOnly = true)
    public List<Appointment> findAll() {

        List<Appointment> list = appointmentRepository.findAll();

        if (list.isEmpty()) {
            throw new ObjectNotFoundException("Appointment not found");
        }

        return list;
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST','DOCTOR')")
    public Appointment findById(Long id) {

        Optional<Appointment> appointment = appointmentRepository.findById(id);
        return appointment.orElseThrow(() -> new ObjectNotFoundException("Appointment not found with the ID:" + id));
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    public Appointment update(Appointment appointment) {

        Appointment existing = findById(appointment.getId());

        validateAppointment(appointment);

        if (existing.getStatus() != AppointmentStatus.SCHEDULED) {
            throw new BusinessConflictException("Only SCHEDULED appointments can be updated.");
        }

        validateAppointmentDateTime(appointment);

        existing.setAppointmentDateTime(appointment.getAppointmentDateTime());
        existing.setDoctorName(appointment.getDoctorName());
        existing.setSpecialty(appointment.getSpecialty());

        // only allow status update if it's provided in the request and is different from the current status
        if (appointment.getStatus() != null) {
            existing.setStatus(appointment.getStatus());
        }

        return appointmentRepository.save(existing);
    }

    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
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

    @PreAuthorize("hasRole('ADMIN')")
    public void delete(Long id) {

        Appointment existing = findById(id);

        if (existing.getStatus() == AppointmentStatus.COMPLETED) {
            throw new BusinessConflictException("Completed appointments cannot be removed.");
        }

        appointmentRepository.deleteById(id);
    }

    private void validateAppointment(Appointment appointment) {

        Optional<Appointment> existingAppointment = appointmentRepository.findAll().stream()
                .filter(a -> a.getDoctorName().equals(appointment.getDoctorName())
                        && a.getSpecialty().equals(appointment.getSpecialty())
                        && a.getAppointmentDateTime().equals(appointment.getAppointmentDateTime()))
                .findFirst();

        if (existingAppointment.isPresent()) {
            throw new BusinessConflictException("An appointment already exists for the same doctor, specialty, and date/time.");
        }

    }

    private void validateAppointmentDateTime(Appointment appointment) {
        if (appointment.getAppointmentDateTime() == null) {
            throw new BusinessException("Appointment date/time must be provided.");
        }

        if (appointment.getAppointmentDateTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Appointment date/time cannot be in the past.");
        }
    }
}
