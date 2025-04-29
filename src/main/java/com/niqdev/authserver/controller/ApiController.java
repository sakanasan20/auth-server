package com.niqdev.authserver.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.niqdev.authserver.service.AuthService;

@RestController
@RequestMapping("/api")
public class ApiController {
	
	private final AuthService authService;
	
    public ApiController(AuthService authService) {
		this.authService = authService;
	}

	@GetMapping("/jwt")
    public String getJwt() {
        return authService.getJwt()
                .map(jwt -> "JWT Token Value: " + jwt.getTokenValue())
                .orElse("Not authenticated");
    }
	
	@GetMapping("/user")
    public String getUser() {
        return authService.getAuthentication()
                .map(auth -> "User: " + auth.getName())
                .orElse("Not authenticated");
    }
	

    @GetMapping("/roles")
    public List<String> getRoles() {
        return authService.getJwt()
        		.map(jwt -> jwt.getClaimAsStringList("roles"))
        		.orElse(Collections.emptyList());
    }
}
