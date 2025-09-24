package com.example.appbackend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.nio.charset.StandardCharsets;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter) throws Exception {
		http
				.csrf(csrf -> csrf.disable())
				.cors(cors -> {})
				.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.exceptionHandling(ex -> ex
						.authenticationEntryPoint((request, response, authException) -> {
							response.setStatus(401);
							response.setContentType(MediaType.APPLICATION_JSON_VALUE);
							response.setCharacterEncoding(StandardCharsets.UTF_8.name());
							response.getWriter().write("{\"error\":\"unauthorized\"}");
						})
						.accessDeniedHandler((request, response, accessDeniedException) -> {
							response.setStatus(403);
							response.setContentType(MediaType.APPLICATION_JSON_VALUE);
							response.setCharacterEncoding(StandardCharsets.UTF_8.name());
							response.getWriter().write("{\"error\":\"forbidden\"}");
						})
				)
				.authorizeHttpRequests(auth -> auth
						.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
						.requestMatchers(HttpMethod.POST, "/api/auth/login", "/api/auth/register", "/api/auth/logout").permitAll()
						.requestMatchers(HttpMethod.GET, "/actuator/health").permitAll()
						.anyRequest().authenticated()
				)
				.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(userDetailsService);
		provider.setPasswordEncoder(passwordEncoder);
		return provider;
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}
}

