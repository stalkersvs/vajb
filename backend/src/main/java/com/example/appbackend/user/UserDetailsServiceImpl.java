package com.example.appbackend.user;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	private final UserRepository userRepository;

	public UserDetailsServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByEmail(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));
		Collection<GrantedAuthority> authorities = Arrays.stream(user.getRoles().split(","))
				.map(String::trim)
				.filter(r -> !r.isEmpty())
				.map(r -> new SimpleGrantedAuthority("ROLE_" + r))
				.collect(Collectors.toList());
		return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPasswordHash(), authorities);
	}
}

