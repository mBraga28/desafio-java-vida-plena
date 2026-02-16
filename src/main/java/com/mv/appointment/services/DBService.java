package com.mv.appointment.services;

import com.mv.appointment.domain.enums.Role;
import com.mv.appointment.domain.entities.User;
import com.mv.appointment.domain.entities.Appointment;
import com.mv.appointment.domain.enums.AppointmentStatus;
import com.mv.appointment.repositories.UserRepository;
import com.mv.appointment.repositories.AppointmentRepository;
// RoleRepository removed: roles are enums, not JPA entities
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Profile({"dev"})
public class DBService {

    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;
    // roleRepository removed
    private final PasswordEncoder passwordEncoder;

    public DBService(UserRepository userRepository,
                     AppointmentRepository appointmentRepository,
                     PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.appointmentRepository = appointmentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void instantiateDatabase() {
        if(userRepository.count() > 0) return;

        // =====================
        // USERS
        // =====================

        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("123456"));
        admin.getRoles().add(Role.ROLE_ADMIN);

        User receptionist = new User();
        receptionist.setUsername("reception");
        receptionist.setPassword(passwordEncoder.encode("123456"));
        receptionist.getRoles().add(Role.ROLE_RECEPTIONIST);

        User doctor = new User();
        doctor.setUsername("doctor");
        doctor.setPassword(passwordEncoder.encode("123456"));
        doctor.getRoles().add(Role.ROLE_DOCTOR);

        userRepository.saveAll(List.of(admin, receptionist, doctor));


        // =====================
        // APPOINTMENTS
        // =====================
        if (appointmentRepository.count() == 0) {
            Appointment a1 = new Appointment();
            a1.setPatientName("Maria Silva");
            a1.setDoctorName("Dr. João");
            a1.setSpecialty("Cardiology");
            // Scheduled to the day 23/03/2026 at 10:00 a.m.
            a1.setAppointmentDateTime(LocalDateTime.of(2026, 3, 23, 10, 0));
            a1.setStatus(AppointmentStatus.SCHEDULED);

            Appointment a2 = new Appointment();
            a2.setPatientName("Pedro Souza");
            a2.setDoctorName("Dra. Ana");
            a2.setSpecialty("Pediatrics");
            a2.setAppointmentDateTime(LocalDateTime.of(2026, 3, 10, 14, 30));
            a2.setStatus(AppointmentStatus.SCHEDULED);

            Appointment a3 = new Appointment();
            a3.setPatientName("João Pereira");
            a3.setDoctorName("Dr. Carlos");
            a3.setSpecialty("General Practice");
            a3.setAppointmentDateTime(LocalDateTime.of(2025, 4, 1, 8, 0));
            a3.setStatus(AppointmentStatus.COMPLETED);

            appointmentRepository.saveAll(java.util.List.of(a1, a2, a3));
        }
    }
}
