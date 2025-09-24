package com.example.appbackend.config;

import com.example.appbackend.user.User;
import com.example.appbackend.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {
	@Bean
	CommandLineRunner seedAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			String adminEmail = "admin@example.com";
			if (!userRepository.existsByEmail(adminEmail)) {
				User admin = new User();
				admin.setEmail(adminEmail);
				admin.setPasswordHash(passwordEncoder.encode("admin"));
				admin.setRoles("ADMIN,USER");
				userRepository.save(admin);
			}
		};
	}
}
