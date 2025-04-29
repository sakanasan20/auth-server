package com.niqdev.authserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
	
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
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