package com.niqdev.authserver.repository;

import java.util.stream.Collectors;

import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Component;

import com.niqdev.authserver.entity.Client;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DynamicRegisteredClientRepository implements RegisteredClientRepository {
	
	private final ClientRepository clientRepository;

	@Override
	public void save(RegisteredClient registeredClient) {
        Client client = buildClient(registeredClient);
        clientRepository.save(client);
	}

	@Override
	public RegisteredClient findById(String id) {
	    return clientRepository.findById(id)
	            .map(this::buildRegisteredClient)
	            .orElse(null);
	}

	@Override
	public RegisteredClient findByClientId(String clientId) {
	    return clientRepository.findByClientId(clientId)
	            .map(this::buildRegisteredClient)
	            .orElse(null);
	}
	
	private RegisteredClient buildRegisteredClient(Client client) {
	    RegisteredClient.Builder builder = RegisteredClient
	        .withId(client.getId())
	        .clientId(client.getClientId())
	        .clientSecret(client.getClientSecret());

	    // 解析 authentication methods
	    if (client.getAuthenticationMethods() != null) {
	        for (String method : client.getAuthenticationMethods().split(",")) {
	            switch (method.trim()) {
	                case "client_secret_basic" -> builder.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC);
	                case "client_secret_post" -> builder.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST);
	                case "none" -> builder.clientAuthenticationMethod(ClientAuthenticationMethod.NONE);
	                // 可以擴充更多
	            }
	        }
	    }

	    // 解析 grant types
	    if (client.getAuthorizationGrantTypes() != null) {
	        for (String type : client.getAuthorizationGrantTypes().split(",")) {
	            switch (type.trim()) {
	                case "authorization_code" -> builder.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE);
	                case "refresh_token" -> builder.authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN);
	                case "client_credentials" -> builder.authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS);
	                case "password" -> builder.authorizationGrantType(new AuthorizationGrantType("password")); // 自定義類型（如果支援）
	            }
	        }
	    }

	    // redirect URIs
	    if (client.getRedirectUris() != null) {
	        for (String uri : client.getRedirectUris().split(",")) {
	            builder.redirectUri(uri.trim());
	        }
	    }

	    // scopes
	    if (client.getScopes() != null) {
	        for (String scope : client.getScopes().split(",")) {
	            builder.scope(scope.trim());
	        }
	    }

	    return builder.build();
	}
	
	private Client buildClient(RegisteredClient registeredClient) {
        Client.ClientBuilder builder  = Client.builder()
        		.id(registeredClient.getId())
        		.clientId(registeredClient.getClientId())
        		.clientSecret(registeredClient.getClientSecret());

        builder.authenticationMethods(registeredClient.getClientAuthenticationMethods().stream()
            .map(ClientAuthenticationMethod::getValue)
            .collect(Collectors.joining(",")));

        builder.authorizationGrantTypes(registeredClient.getAuthorizationGrantTypes().stream()
            .map(AuthorizationGrantType::getValue)
            .collect(Collectors.joining(",")));
        
        builder.redirectUris(String.join(",", registeredClient.getRedirectUris()));
        builder.scopes(String.join(",", registeredClient.getScopes()));
        
        return builder.build();
	}
}
