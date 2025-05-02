package com.niqdev.authserver.config;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.niqdev.authserver.converter.UserToUserInfoDtoConverter;
import com.niqdev.authserver.dto.UserInfoDto;
import com.niqdev.authserver.entity.User;
import com.niqdev.authserver.security.CustomUserDetails;
import com.niqdev.authserver.util.KeyGeneratorUtils;

@Configuration
@EnableWebSecurity
public class AuthorizationServerConfig {
	
	@Autowired
	private UserToUserInfoDtoConverter userConverter;
	
	@Bean
	OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer() {
	    return context -> {
	        Authentication principal = context.getPrincipal();
	        if (principal.getPrincipal() instanceof CustomUserDetails userDetails) {
	            List<String> roles = userDetails.getAuthorities().stream()
	                .map(GrantedAuthority::getAuthority)
	                .toList();

	            User user = userDetails.getUser();
	            UserInfoDto userInfo = userConverter.convert(user);

	            if (context.getTokenType().getValue().equals("id_token")) {
	                context.getClaims().claim("roles", roles);
	                context.getClaims().claim("user", userInfo.toClaims()); // toClaims() 將 DTO 轉為 Map，保證能被 JSON 正確序列化進 token
	            }

	            if (context.getTokenType().getValue().equals("access_token")) {
	            	context.getClaims().claim("authorities", roles);
	                context.getClaims().claim("roles", roles);
	                context.getClaims().claim("user", userInfo.toClaims()); // toClaims() 將 DTO 轉為 Map，保證能被 JSON 正確序列化進 token
	            }
	        }
	    };
	}

	@Bean
	SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
		var authorizationServerConfigurer = new OAuth2AuthorizationServerConfigurer();

        authorizationServerConfigurer.oidc(Customizer.withDefaults());
//		authorizationServerConfigurer.authorizationEndpoint(endpoint -> endpoint
//        	.consentPage("/oauth2/consent")
//        ); // 自訂授權同意頁面

		http
			.securityMatcher(authorizationServerConfigurer.getEndpointsMatcher())
			.authorizeHttpRequests(authorize -> authorize
					.anyRequest().authenticated()
				)
			.exceptionHandling(exceptions -> exceptions
					.authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"))
				)
			.csrf(csrf -> csrf
					.ignoringRequestMatchers(authorizationServerConfigurer.getEndpointsMatcher())
				)
			.with(authorizationServerConfigurer, (config) -> {});

		return http.build();
	}

	@Bean
	AuthorizationServerSettings authorizationServerSettings() {
		return AuthorizationServerSettings.builder().build();
	}
	
    @Bean
    JWKSource<SecurityContext> jwkSource() {
        RSAKey rsaKey = generateRsa();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
    }

    private RSAKey generateRsa() {
        KeyPair keyPair = KeyGeneratorUtils.generateRsaKey();
        return new RSAKey.Builder((RSAPublicKey) keyPair.getPublic())
                .privateKey((RSAPrivateKey) keyPair.getPrivate())
                .keyID(UUID.randomUUID().toString())
                .build();
    }
}
