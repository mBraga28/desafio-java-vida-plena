package com.mv.appointment;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mv.appointment.config.TestSecurityConfig;
import com.mv.appointment.domain.entities.Appointment;
import com.mv.appointment.domain.enums.AppointmentStatus;
import com.mv.appointment.repositories.AppointmentRepository;

import jakarta.transaction.Transactional;

@Import(TestSecurityConfig.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AppointmentIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AppointmentRepository repository;

    @Test
    public void shouldReturnBadRequestWhenSchedulingInPast() throws Exception {

        String json = """
                {
                    "patientName": "João",
                    "doctorName": "Dr. Carlos",
                    "specialty": "Cardiology",
                    "appointmentDateTime": "%s"
                }
                """.formatted(LocalDateTime.now().minusDays(1));

        mockMvc.perform(post("/appointment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldNotAllowUpdateWhenAppointmentIsCompleted() throws Exception {

        Appointment appointment = new Appointment(
                null,
                "Maria",
                "Dr. Ana",
                "Dermatology",
                LocalDateTime.now().plusDays(1),
                AppointmentStatus.COMPLETED);

        repository.save(appointment);

        String json = """
                {
                    "patientName": "Maria",
                    "doctorName": "Dr. Ana",
                    "specialty": "Dermatology",
                    "appointmentDateTime": "%s"
                }
                """.formatted(LocalDateTime.now().plusDays(2));

        mockMvc.perform(put("/appointment/" + appointment.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }

}
