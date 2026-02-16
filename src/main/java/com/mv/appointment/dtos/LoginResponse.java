package com.mv.appointment.dtos;


public record LoginResponse(
		String acessTonkenm, 
		Long expiresIn, 
		String mensageSucess) {}
