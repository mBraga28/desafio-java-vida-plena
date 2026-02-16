package com.mv.appointment.exceptions;

public class TokenDeniedException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public TokenDeniedException(String msg) {
		super(msg);
	}

}
