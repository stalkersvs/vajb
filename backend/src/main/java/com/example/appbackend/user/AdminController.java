package com.example.appbackend.user;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

record AdminUserDto(Long id, String email, String roles) {}

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
	private final UserRepository userRepository;

	public AdminController(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@GetMapping
	public List<AdminUserDto> list() {
		return userRepository.findAll().stream()
				.map(u -> new AdminUserDto(u.getId(), u.getEmail(), u.getRoles()))
				.toList();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		if (!userRepository.existsById(id)) {
			return ResponseEntity.notFound().build();
		}
		userRepository.deleteById(id);
		return ResponseEntity.noContent().build();
	}
}
