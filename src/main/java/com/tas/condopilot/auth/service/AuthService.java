package com.tas.condopilot.auth.service;

import com.tas.condopilot.auth.dto.AuthResponse;
import com.tas.condopilot.auth.dto.LoginRequest;
import com.tas.condopilot.auth.dto.RegisterRequest;
import com.tas.condopilot.auth.entity.UserEntity;
import com.tas.condopilot.auth.repository.UserRepository;
import com.tas.condopilot.common.exception.BusinessException;
import com.tas.condopilot.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final AuthenticationManager authenticationManager;

	public void register(RegisterRequest request) {
		if (userRepository.existsByEmail(request.getEmail())) {
			throw new BusinessException("E-mail já cadastrado");
		}

		UserEntity user = UserEntity.builder().email(request.getEmail())
				.password(passwordEncoder.encode(request.getPassword())).role(request.getRole()).build();

		userRepository.save(user);
	}

	public AuthResponse login(LoginRequest request) {
		authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

		String token = jwtService.generateToken(request.getEmail());
		return AuthResponse.builder().token(token).type("Bearer").build();
	}
}