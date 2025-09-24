package com.example.appbackend.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
	private static final String COOKIE_NAME = "JWT";
	private final JwtService jwtService;
	private final UserDetailsService userDetailsService;
	private final TokenBlacklistService tokenBlacklistService;

	public JwtAuthFilter(JwtService jwtService, UserDetailsService userDetailsService, TokenBlacklistService tokenBlacklistService) {
		this.jwtService = jwtService;
		this.userDetailsService = userDetailsService;
		this.tokenBlacklistService = tokenBlacklistService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		String token = extractToken(request);
		if (token != null && !tokenBlacklistService.isBlacklisted(token)) {
			try {
				Claims claims = jwtService.parse(token);
				String username = claims.getSubject();
				UserDetails userDetails = userDetailsService.loadUserByUsername(username);
				UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
						userDetails, null, userDetails.getAuthorities());
				auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(auth);
			} catch (Exception ignored) {}
		}
		filterChain.doFilter(request, response);
	}

	private String extractToken(HttpServletRequest request) {
		// 1) Cookie
		if (request.getCookies() != null) {
			Cookie cookie = Arrays.stream(request.getCookies())
					.filter(c -> COOKIE_NAME.equals(c.getName()))
					.findFirst().orElse(null);
			if (cookie != null && cookie.getValue() != null && !cookie.getValue().isBlank()) {
				return cookie.getValue();
			}
		}
		// 2) Authorization header fallback
		String header = request.getHeader(HttpHeaders.AUTHORIZATION);
		if (header != null && header.startsWith("Bearer ")) {
			return header.substring(7);
		}
		return null;
	}
}

