package com.mv.appointment.controllers;

import org.springframework.http.ResponseEntity;

import com.mv.appointment.dtos.LoginRequest;
import com.mv.appointment.dtos.LoginResponse;
public interface TokenController {
	
	ResponseEntity<LoginResponse> login(LoginRequest loginRequest);
}
