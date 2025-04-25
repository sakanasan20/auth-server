package com.niqdev.authserver.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "clients")
public class Client {

	@Id
	private String id;
    private String clientId;
    private String clientSecret;
    private String authenticationMethods;
    private String authorizationGrantTypes;
    private String redirectUris;
    private String scopes;
	
}
