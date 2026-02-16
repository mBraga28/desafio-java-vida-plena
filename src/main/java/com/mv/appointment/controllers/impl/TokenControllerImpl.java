package com.mv.appointment.controllers.impl;


import java.time.Instant;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mv.appointment.controllers.TokenController;
import com.mv.appointment.dtos.LoginRequest;
import com.mv.appointment.dtos.LoginResponse;
import com.mv.appointment.exceptions.CustomAuthenticationException;
import com.mv.appointment.repositories.UserRepository;


@RestController
@RequestMapping("/token")
public class TokenControllerImpl implements TokenController {
	private final JwtEncoder jwtEncoder;

	private final UserRepository userRepository;
	private final BCryptPasswordEncoder passwordEncoder;

	public TokenControllerImpl(JwtEncoder jwtEncoder, UserRepository userRepository,
			BCryptPasswordEncoder passwordEncoder) {
		this.jwtEncoder = jwtEncoder;
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
		var user = userRepository.findByUsername(loginRequest.username());

		/*
		 * If the user is not found or the provided password does not match the one
		 * stored in the database, throws a custom exception for incorrect authentication.
		 */
		if (user.isEmpty() || !user.get().isLoginCorrect(loginRequest, passwordEncoder)) {
			throw new CustomAuthenticationException(
					"Usuário ou senha incorreta. Por favor, verifique suas credenciais e tente novamente.");
		}

		var now = Instant.now();
		var expiresIn = 900L; // Time of token expiration in seconds (15 minutes)

		/*
		 * Builds the set of claims for the JWT, including the issuer, subject, issued at
		 * time, expiration time, and custom claims for user roles and scopes.
		 */
		var claims = JwtClaimsSet.builder()
									.issuer("mybackend")
									.subject(user.get().getUsername())
									.issuedAt(now)
									.expiresAt(now.plusSeconds(expiresIn))
									.claim("scope", 
										user.get().getRoles().stream()
											.map(Enum::name)
											.toList())
									.claim("roles",
										user.get().getRoles().stream()
											.map(Enum::name)
											.toList())
									.build();

		/*
		 * Codifies the set of claims into a JWT token and returns the token value in the response.
		 */
		var jwtValue = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
		return ResponseEntity.ok(new LoginResponse(jwtValue, expiresIn, "Welcome!"));
	}
}
