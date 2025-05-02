package com.niqdev.authserver.config;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
	
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	JwtAuthenticationConverter jwtAuthenticationConverter() {
	    JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
	    converter.setJwtGrantedAuthoritiesConverter(jwt -> {
	        List<String> authorities = jwt.getClaimAsStringList("authorities");
	        if (authorities == null) {
	            return Collections.emptyList();
	        }

	        // 將所有權限字串轉為 SimpleGrantedAuthority
	        return authorities.stream()
	            .map(auth -> {
	                // 你可以在這裡加上 prefix 判斷與邏輯
	                if (auth.startsWith("ROLE_")) {
	                    return new SimpleGrantedAuthority(auth); // ROLE
	                } else if (auth.startsWith("SCOPE_")) {
	                    return new SimpleGrantedAuthority(auth); // SCOPE
	                } else {
	                    return new SimpleGrantedAuthority(auth); // Authority
	                }
	            })
	            .collect(Collectors.toList());
	    });
	    return converter;
	}
	
    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http
	        .csrf(csrf -> csrf
					.ignoringRequestMatchers("/h2-console/**")
				)
	        .headers(headers -> headers
					.frameOptions(frameOptions -> frameOptions.sameOrigin())
				)
            .authorizeHttpRequests(auth -> auth
            		.requestMatchers("/h2-console/**").permitAll()
            		.requestMatchers("/oidc/logout").permitAll()
            		.anyRequest().authenticated()
            	)
            .oauth2ResourceServer(oauth2 -> oauth2
            		.jwt(Customizer.withDefaults())
                )
            .formLogin(Customizer.withDefaults());
        return http.build();
    }
}