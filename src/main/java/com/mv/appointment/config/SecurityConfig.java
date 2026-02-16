package com.mv.appointment.config;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.converter.RsaKeyConverters;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.io.IOException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.List;

@Configuration
@EnableAsync
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    /**
     * Reference to the public key path in the application.properties file
     */
    @Value("${jwt.public.key}")
    private Resource publicKeyResource;

    /**
     * Referencies to the private key path in the application.properties file
     */
    @Value("${jwt.private.key}")
    private Resource privateKeyResource;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity.authorizeHttpRequests(authorize -> authorize
                // Define access rules for the login endpoint, allowing anyone to access it without authentication
                .requestMatchers(HttpMethod.POST, "/token/login").permitAll()
                // Define access rules for appointment endpoints based on user roles
                .requestMatchers(HttpMethod.GET, "/appointment/**").hasAnyAuthority("ROLE_ADMIN","ROLE_RECEPTIONIST","ROLE_DOCTOR")
                .requestMatchers(HttpMethod.POST, "/appointment/**").hasAnyAuthority("ROLE_ADMIN","ROLE_RECEPTIONIST")
                .requestMatchers(HttpMethod.PUT, "/appointment/*/status").hasAnyAuthority("ROLE_ADMIN","ROLE_DOCTOR")
                .requestMatchers(HttpMethod.PUT, "/appointment/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_RECEPTIONIST")
                .requestMatchers(HttpMethod.DELETE, "/appointment/**").hasAuthority("ROLE_ADMIN")
                .anyRequest().authenticated())
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return httpSecurity.build();
    }

    /**
     * Creates a JWT decoder bean that decodes JWT tokens using the public key.
     * Decodes the JWT token using the public key and returns the decoded JWT.
     * 
     * @return jwt decoding bean
     */
    @Bean
    JwtDecoder jwtDecoder() throws IOException {
        RSAPublicKey publicKey = (RSAPublicKey) RsaKeyConverters.x509().convert(publicKeyResource.getInputStream());
        return NimbusJwtDecoder.withPublicKey(publicKey).build();
    }

    /**
     * Creates a JWT encoder bean that encodes JWT tokens using the private key.
     * Encodes the JWT token with the public and private keys.
     * 
     * @return jwt encoded bean
     */
    @Bean
    JwtEncoder jwtEncoder() throws IOException {
        RSAPublicKey publicKey = (RSAPublicKey) RsaKeyConverters.x509().convert(publicKeyResource.getInputStream());
        RSAPrivateKey privateKey = (RSAPrivateKey) RsaKeyConverters.pkcs8().convert(privateKeyResource.getInputStream());
        JWK jwk = new RSAKey.Builder(publicKey).privateKey(privateKey).build();
        var jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwks);
    }

    /**
     * Bean for encoding passwords using BCrypt
     * Used to compare stored passwords with those provided by the user
     * 
     * @return object for encoding password
     */
    @Bean
    BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        var converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            List<String> roles = jwt.getClaimAsStringList("roles");
            if (roles == null) roles = jwt.getClaimAsStringList("scope");
            if (roles == null) return List.of();
            return roles.stream()
                        .map(r -> (org.springframework.security.core.GrantedAuthority)
                                   new SimpleGrantedAuthority(r.startsWith("ROLE_") ? r : "ROLE_" + r))
                        .collect(java.util.stream.Collectors.toList());
        });
        return converter;
    }
}