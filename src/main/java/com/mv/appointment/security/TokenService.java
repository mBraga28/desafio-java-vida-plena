package com.mv.appointment.security;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.mv.appointment.exceptions.TokenDeniedException;


@Service
public class TokenService {

	@Value("${jwt.expiration}")
	private long expiration;
	
	@Value("${jwt.issuer.name}")
	private String issuer;
	
	private final JwtEncoder jwtEncoder;
	
	private final JwtDecoder jwtDecoder;
	
	public TokenService(JwtEncoder jwtEncoder, JwtDecoder jwtDecoder) {
		this.jwtEncoder = jwtEncoder;
		this.jwtDecoder = jwtDecoder;
	}
	
	/*
	 * Codified the set of claims into a JWT token and returns the token 
	 * value in the response.
	 * 
	 * @return token generated 
	 */
	public String generateToken(String username) {
		var now = Instant.now();
		var expiresIn = expiration * 60; // Tempo de expiração do token em segundos

		/*
		 * Builds the set of claims for the JWT, including the issuer, 
		 * subject, issued at, and expiration time.
		 */
		var claims = JwtClaimsSet.builder()
								.issuer(issuer)
								.subject(username)
								.issuedAt(now)
								.expiresAt(now.plusSeconds(expiresIn))
								.build();


		return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
	}
	
	public String regenerateToken(String oldToken) throws TokenDeniedException {
		String username = getUsename(tokenHeaderVerify(oldToken));
		return generateToken(username);
	}
	
	/**
	 * Extracts the username from the token
	 * @param token
	 * @return username of the user
	 */
	public String getUsename(String token) {		
		return jwtDecoder.decode(token).getSubject();
	}
	
	/**
	 * Obtains the token sent without the prefix
	 * @param authHeader value of the Authorization attribute of the Header
	 * @return a token's string
	 * @throws TokenDeniedException
	 */
	public String tokenHeaderVerify(String authHeader) throws TokenDeniedException {
		// verify if the token is present
		if (!authHeader.startsWith("Bearer")) {
			throw new TokenDeniedException("Token not provided");
		}
		// verify if the token has the correct length
		if (authHeader.length() < 7) {
			throw new TokenDeniedException("Invalid token");
		}
		
		return authHeader.substring(7);
	}
	
	/**
	 * generate the header with the token in Authorization
	 * @param username
	 * @return response headers
	 */
	public HttpHeaders getAuthHeader(String username) {
		HttpHeaders responseHeaders = new HttpHeaders();
	    
		String token = this.generateToken(username);
		responseHeaders.set("Authorization", token);
		
		return responseHeaders;
	}
	
	/**
	 * generate the header with the token in Authorization
	 * @param oldToken
	 * @return response headers
	 * @throws TokenDeniedException 
	 */
	public HttpHeaders getAuthHeaderToken(String oldToken) throws TokenDeniedException {
		
		HttpHeaders responseHeaders = new HttpHeaders();
	    
		responseHeaders.set("Authorization", this.regenerateToken(oldToken));
		
		return responseHeaders;
	}
}