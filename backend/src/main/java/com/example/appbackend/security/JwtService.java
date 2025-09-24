package com.example.appbackend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {
	private final Key signingKey;
	private final long expirationSeconds;

	public JwtService(
			@Value("${jwt.secret}") String secret,
			@Value("${jwt.expiration}") long expirationSeconds
	) {
		byte[] keyBytes = deriveKeyBytes(secret);
		this.signingKey = Keys.hmacShaKeyFor(keyBytes);
		this.expirationSeconds = expirationSeconds;
	}

	public String generateToken(String subject, Map<String, Object> claims) {
		Instant now = Instant.now();
		return Jwts.builder()
				.setClaims(claims)
				.setSubject(subject)
				.setIssuedAt(Date.from(now))
				.setExpiration(Date.from(now.plusSeconds(expirationSeconds)))
				.signWith(signingKey, SignatureAlgorithm.HS256)
				.compact();
	}

	public Claims parse(String token) {
		return Jwts.parserBuilder()
				.setSigningKey(signingKey)
				.build()
				.parseClaimsJws(token)
				.getBody();
	}

	private static byte[] deriveKeyBytes(String secret) {
		byte[] raw;
		try {
			// If secret looks like base64, decode it
			raw = Decoders.BASE64.decode(secret);
		} catch (Exception e) {
			// Use raw bytes of the provided secret
			raw = secret.getBytes(java.nio.charset.StandardCharsets.UTF_8);
		}
		// Ensure at least 256 bits as required by HS256
		if (raw.length < 32) {
			raw = sha256(raw);
		}
		return raw;
	}

	private static byte[] sha256(byte[] input) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			return md.digest(input);
		} catch (Exception ex) {
			throw new IllegalStateException("SHA-256 not available", ex);
		}
	}
}

