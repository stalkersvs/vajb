package com.example.appbackend.auth;

import com.example.appbackend.security.JwtService;
import com.example.appbackend.security.TokenBlacklistService;
import com.example.appbackend.user.User;
import com.example.appbackend.user.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	private static final String COOKIE_NAME = "JWT";
	private final AuthenticationManager authenticationManager;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final TokenBlacklistService tokenBlacklistService;

	public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, TokenBlacklistService tokenBlacklistService) {
		this.authenticationManager = authenticationManager;
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
		this.tokenBlacklistService = tokenBlacklistService;
	}

	@PostMapping("/register")
	public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
		if (userRepository.existsByEmail(request.getEmail())) {
			return ResponseEntity.badRequest().body(Map.of("error", "Email already used"));
		}
		User user = new User();
		user.setEmail(request.getEmail());
		user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
		user.setRoles(userRepository.count() == 0 ? "ADMIN,USER" : "USER");
		userRepository.save(user);
		return ResponseEntity.ok(Map.of("status", "registered"));
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
		Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(auth);
		User user = userRepository.findByEmail(request.getEmail()).orElseThrow();
		Map<String, Object> claims = new HashMap<>();
		claims.put("roles", user.getRoles());
		String token = jwtService.generateToken(request.getEmail(), claims);

		Cookie cookie = new Cookie(COOKIE_NAME, token);
		cookie.setHttpOnly(true);
		cookie.setSecure(false); // set to true in HTTPS environments
		cookie.setPath("/");
		cookie.setMaxAge(60 * 60);
		cookie.setAttribute("SameSite", "Strict");
		response.addCookie(cookie);

		return ResponseEntity.ok(Map.of("status", "ok"));
	}

	@GetMapping("/me")
	public ResponseEntity<?> me(Authentication authentication) {
		if (authentication == null) {
			return ResponseEntity.status(401).body(Map.of("error", "unauthorized"));
		}
		var roles = authentication.getAuthorities().stream().map(a -> a.getAuthority()).toList();
		return ResponseEntity.ok(Map.of(
				"email", authentication.getName(),
				"roles", roles
		));
	}

	@PostMapping("/logout")
	public ResponseEntity<?> logout(@CookieValue(name = COOKIE_NAME, required = false) String token, HttpServletResponse response) {
		if (token != null) {
			try {
				Claims claims = jwtService.parse(token);
				long expiresIn = Math.max(1, (claims.getExpiration().toInstant().getEpochSecond() - Instant.now().getEpochSecond()));
				tokenBlacklistService.blacklist(token, expiresIn);
			} catch (Exception ignored) {}
		}
		Cookie cookie = new Cookie(COOKIE_NAME, "");
		cookie.setHttpOnly(true);
		cookie.setSecure(false); // set to true in HTTPS environments
		cookie.setPath("/");
		cookie.setMaxAge(0);
		cookie.setAttribute("SameSite", "Strict");
		response.addCookie(cookie);
		return ResponseEntity.ok(Map.of("status", "logged_out"));
	}
}

