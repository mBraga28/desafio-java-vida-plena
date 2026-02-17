package com.mv.appointment.controllers.impl;

import com.mv.appointment.controllers.AppointmentController;
import com.mv.appointment.domain.entities.Appointment;
import com.mv.appointment.dtos.AppointmentDTO;
import com.mv.appointment.dtos.AppointmentStatusDTO;
import com.mv.appointment.services.AppointmentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
// import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Tag(name = "Appointments", description = "Gerenciamento de atendimentos")
@RestController
@RequestMapping(value = "appointment")
public class AppointmentControllerImpl implements AppointmentController {

    private final AppointmentService appointmentService;

    private final ModelMapper modelMapper;

    public AppointmentControllerImpl(AppointmentService appointmentService, ModelMapper modelMapper) {
        this.appointmentService = appointmentService;
        this.modelMapper = modelMapper;
    }

    @Operation(summary = "Criar um novo atendimento")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Atendimento criado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    @Override
    @PostMapping
    public ResponseEntity<AppointmentDTO> create(@RequestBody AppointmentDTO appointmentDTO) {
        Appointment createdAppointment = modelMapper.map(appointmentDTO, Appointment.class);
        Appointment savedAppointment = appointmentService.create(createdAppointment);
        return ResponseEntity.status(HttpStatus.CREATED).body(modelMapper.map(savedAppointment, AppointmentDTO.class));
    }

    @Operation(summary = "Listar todos os atendimentos")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    @Override
    @GetMapping
    public ResponseEntity<List<AppointmentDTO>> findAll() {
        List<Appointment> list = appointmentService.findAll();

        return ResponseEntity.ok().body(list.stream()
                .map(x -> modelMapper.map(x, AppointmentDTO.class))
                .collect(Collectors.toList()));
    }

    @Operation(summary = "Buscar atendimento por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Atendimento encontrado"),
            @ApiResponse(responseCode = "404", description = "Não encontrado")
    })
    @Override
    @GetMapping(value = { "/{id}" })
    public ResponseEntity<AppointmentDTO> findById(@PathVariable Long id) {
        Appointment appointment = appointmentService.findById(id);
        return ResponseEntity.ok().body(modelMapper.map(appointment, AppointmentDTO.class));
    }

    @Operation(summary = "Atualizar atendimento")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Atualizado"),
            @ApiResponse(responseCode = "409", description = "Regra de negócio violada")
    })
    @Override
    @PutMapping(value = { "/{id}" })
    public ResponseEntity<AppointmentDTO> update(@PathVariable Long id, @RequestBody AppointmentDTO newStatus) {
        Appointment appointment = modelMapper.map(newStatus, Appointment.class);
        appointment.setId(id);
        Appointment updatedAppointment = appointmentService.update(appointment);
        return ResponseEntity.ok().body(modelMapper.map(updatedAppointment, AppointmentDTO.class));
    }

    @Operation(summary = "Atualizar status do atendimento")
    @Override
    @PutMapping(value = { "/{id}/status" })
    public ResponseEntity<AppointmentDTO> updateStatus(@PathVariable Long id,
            @RequestBody AppointmentStatusDTO statusDTO) {
        Appointment updated = appointmentService.updateStatus(id, statusDTO.getStatus());
        return ResponseEntity.ok().body(modelMapper.map(updated, AppointmentDTO.class));
    }

    @Operation(summary = "Remover atendimento")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Removido"),
            @ApiResponse(responseCode = "404", description = "Não encontrado")
    })
    @Override
    @DeleteMapping(value = { "/{id}" })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        appointmentService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
