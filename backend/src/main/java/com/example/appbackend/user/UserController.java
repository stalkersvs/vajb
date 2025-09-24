package com.example.appbackend.user;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {
	@GetMapping("/profile")
	public Map<String, Object> profile(Authentication authentication) {
		return Map.of(
				"name", authentication.getName(),
				"authorities", authentication.getAuthorities()
		);
	}
}

