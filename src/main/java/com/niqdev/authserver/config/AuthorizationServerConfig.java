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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
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
import com.niqdev.authserver.util.KeyGeneratorUtils;

@Configuration
@EnableWebSecurity
public class AuthorizationServerConfig {
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
    @Bean
    OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer() {
        return context -> {
            Authentication principal = context.getPrincipal();
            if (principal.getPrincipal() instanceof UserDetails userDetails) {
                List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();

                if (context.getTokenType().getValue().equals("id_token")) {
                    context.getClaims().claim("roles", roles); // 加到 ID Token
                }

                if (context.getTokenType().getValue().equals("access_token")) {
                    context.getClaims().claim("roles", roles); // 加到 Access Token
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
	RegisteredClientRepository registeredClientRepository() {
		RegisteredClient registeredClient = RegisteredClient
				.withId(UUID.randomUUID().toString())
				.clientId("my-client")
				.clientSecret(passwordEncoder.encode("my-secret"))
				.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
				.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
				.authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
				.redirectUri("http://localhost:8080/login/oauth2/code/my-client")
				.scope(OidcScopes.OPENID)
				.scope("read")
				.build();
		
		return new InMemoryRegisteredClientRepository(registeredClient);
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
