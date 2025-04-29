package com.niqdev.authserver.service;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class AuthService {

	public Optional<Authentication> getAuthentication() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return Optional.ofNullable(authentication);
	}

	public Optional<Jwt> getJwt() {
		Optional<Authentication> auth = getAuthentication();
		if (auth.isPresent() && auth.get() instanceof JwtAuthenticationToken jwtAuth) {
			return Optional.of(jwtAuth.getToken());
		}
		return Optional.empty();
	}
}
