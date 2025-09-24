package com.example.appbackend.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

@Entity
@Table(name = "users")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = false)
	private String passwordHash;

	@Column(nullable = false)
	private String roles; // comma separated, e.g., "USER,ADMIN"

	@Column(nullable = false)
	private OffsetDateTime createdAt = OffsetDateTime.now();

	public User() {}

	public User(Long id, String email, String passwordHash, String roles, OffsetDateTime createdAt) {
		this.id = id;
		this.email = email;
		this.passwordHash = passwordHash;
		this.roles = roles;
		this.createdAt = createdAt == null ? OffsetDateTime.now() : createdAt;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPasswordHash() {
		return passwordHash;
	}
	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}
	public String getRoles() {
		return roles;
	}
	public void setRoles(String roles) {
		this.roles = roles;
	}
	public OffsetDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(OffsetDateTime createdAt) {
		this.createdAt = createdAt;
	}
}

