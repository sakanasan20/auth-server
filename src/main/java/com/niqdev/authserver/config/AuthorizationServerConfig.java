package com.niqdev.authserver.config;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.niqdev.authserver.util.KeyGeneratorUtils;

/**
 * Spring Authorization Server 的核心設定類別，它負責啟用 OAuth2 授權伺服器功能，並註冊相關端點、客戶端資料與授權設定。
 */
@Configuration
@EnableWebSecurity // 啟用 Spring Security
public class AuthorizationServerConfig {

	/**
	 * 設定授權伺服器專屬的 SecurityFilterChain
	 * @param http
	 * @return
	 * @throws Exception
	 */
	@Bean
	SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
		var authorizationServerConfigurer = new OAuth2AuthorizationServerConfigurer();

		// 配置 OpenID Connect metadata
        authorizationServerConfigurer.oidc(Customizer.withDefaults());
        
		authorizationServerConfigurer.authorizationEndpoint(endpoint -> endpoint
				.consentPage("/oauth2/consent")); // 自訂授權同意頁面

		http
			.securityMatcher(authorizationServerConfigurer.getEndpointsMatcher()) // 只攔截授權伺服器相關路徑 : 只對授權伺服器的端點（如 /oauth2/token 等）套用這組 filter chain。
			.authorizeHttpRequests(authorize -> authorize
					.anyRequest().authenticated()
				)
			.exceptionHandling(exceptions -> exceptions
			        .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"))
			    )
			.csrf(csrf -> csrf
					.ignoringRequestMatchers(authorizationServerConfigurer.getEndpointsMatcher())
				)
			.with(authorizationServerConfigurer, (config) -> {}); // 套用設定器 : 把 authorizationServerConfigurer 加入到 Security 配置中

		return http.build();
	}

	/**
	 * 註冊 OAuth2 客戶端資訊
	 * @return
	 */
	@Bean
	RegisteredClientRepository registeredClientRepository() {
		// 這裡設定了一個硬編碼（in-memory）的 Client
		RegisteredClient registeredClient = RegisteredClient
				.withId(UUID.randomUUID().toString())
				.clientId("my-client") // 客戶端認證所需資訊，ID
				.clientSecret("{noop}my-secret") // 客戶端認證所需資訊，Password
				.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC) // 使用 client_secret_basic 認證
				.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE) // 支援的授權類型，授權碼
				.authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN) // 支援的授權類型，刷新 token
				.redirectUri("http://localhost:8080/callback") // 完成授權後重導的 URI
				.scope(OidcScopes.OPENID) // 支援的權限範圍（scope），openid
				.scope("read") // 支援的權限範圍（scope），read
				.build();
		
		return new InMemoryRegisteredClientRepository(registeredClient); // 若要接資料庫，也可改成 JdbcRegisteredClientRepository
	}

	/**
	 * 設定 OAuth2 授權伺服器本身的資訊
	 *  - 發佈端點的位置（預設 /oauth2/token, /oauth2/authorize, /oauth2/jwks 等）
	 *  - issuer URL（你可以用 .issuer("http://your-auth-server") 自定義）
	 * @return
	 */
	@Bean
	AuthorizationServerSettings authorizationServerSettings() {
		return AuthorizationServerSettings.builder().build();
	}
	
    /**
     * JWK 配置
     * @return
     */
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
