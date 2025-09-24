package com.example.appbackend.security;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class TokenBlacklistService {
	private final StringRedisTemplate redisTemplate;

	public TokenBlacklistService(StringRedisTemplate redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public void blacklist(String token, long expiresInSeconds) {
		redisTemplate.opsForValue().set(redisKey(token), "1", Duration.ofSeconds(expiresInSeconds));
	}

	public boolean isBlacklisted(String token) {
		return Boolean.TRUE.equals(redisTemplate.hasKey(redisKey(token)));
	}

	private String redisKey(String token) {
		return "blacklist:" + token;
	}
}

