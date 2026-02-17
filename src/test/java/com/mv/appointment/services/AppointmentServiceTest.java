package com.mv.appointment.services;

import com.mv.appointment.domain.entities.Appointment;
import com.mv.appointment.domain.enums.AppointmentStatus;
import com.mv.appointment.exceptions.BusinessConflictException;
import com.mv.appointment.exceptions.BusinessException;
import com.mv.appointment.repositories.AppointmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
public class AppointmentServiceTest {

    @InjectMocks
    private AppointmentService appointmetService;

    @Mock
    private AppointmentRepository repository;

    private Long existingId;
    private Long nonExistingId;

    private Appointment appointment;

    @BeforeEach
    void setUp() {

        existingId = 1L;
        nonExistingId = 2L;

        appointment = new Appointment(
                existingId,
                "João Silva",
                "Dr. Carlos",
                "Cardiology",
                LocalDateTime.now().plusDays(2),
                AppointmentStatus.SCHEDULED);

        Mockito.when(repository.findById(existingId))
                .thenReturn(Optional.of(appointment));

        Mockito.when(repository.findById(nonExistingId))
                .thenReturn(Optional.empty());

        Mockito.when(repository.save(any()))
                .thenReturn(appointment);
    }

    // =========================
    // TEST 1 - Schedule with past date
    // =========================

    @Test
    public void createShouldThrowBusinessExceptionWhenDateIsInPast() {

        Appointment pastAppointment = new Appointment(
                null,
                "Maria",
                "Dr. Ana",
                "Dermatology",
                LocalDateTime.now().minusDays(1),
                AppointmentStatus.SCHEDULED);

        assertThrows(BusinessException.class, () -> {
            appointmetService.create(pastAppointment);
        });
    }

    // =========================
    // TEST 2 - Create valid appointment
    // =========================

    @Test
    public void createShouldReturnAppointmentWhenValid() {

        Appointment result = appointmetService.create(appointment);

        assertNotNull(result);
        assertEquals("João Silva", result.getPatientName());
        Mockito.verify(repository).save(any());
    }

    // =========================
    // TEST 3 - Block status update when appointment is completed
    // =========================

    @Test
    public void updateShouldThrowBusinessConflictExceptionWhenStatusIsCompleted() {

        appointment.setStatus(AppointmentStatus.COMPLETED);

        assertThrows(BusinessConflictException.class, () -> {
            appointmetService.updateStatus(existingId, appointment.getStatus());
        });
    }

    // =========================
    // TEST 4 - Update valid status
    // =========================

    @Test
    public void updateShouldReturnAppointmentWhenValid() {

        Appointment result = appointmetService.updateStatus(existingId, appointment.getStatus());

        assertNotNull(result);
        assertEquals(existingId, result.getId());
    }

    // =========================
    // TEST 5 - Id not found when updating status
    // =========================

    @Test
    public void updateShouldThrowExceptionWhenIdDoesNotExist() {

        assertThrows(RuntimeException.class, () -> {
            appointmetService.updateStatus(nonExistingId, appointment.getStatus());
        });
    }
}
