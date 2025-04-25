package com.niqdev.authserver.bootstrap;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.niqdev.authserver.entity.Authority;
import com.niqdev.authserver.entity.Client;
import com.niqdev.authserver.entity.Role;
import com.niqdev.authserver.entity.User;
import com.niqdev.authserver.repository.AuthorityRepository;
import com.niqdev.authserver.repository.ClientRepository;
import com.niqdev.authserver.repository.RoleRepository;
import com.niqdev.authserver.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthorityRepository authorityRepository;
    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public void run(String... args) {
    	
    	Client client = Client.builder()
    			.id(UUID.randomUUID().toString())
    			.clientId("my-client")
    			.clientSecret(passwordEncoder.encode("my-secret"))
    			.authenticationMethods(
    					List.of(
    							ClientAuthenticationMethod.CLIENT_SECRET_BASIC
							)
    					.stream()
    					.map(ClientAuthenticationMethod::getValue)
    					.collect(Collectors.joining(",")))
    			.authorizationGrantTypes(
    					List.of(
    							AuthorizationGrantType.AUTHORIZATION_CODE, 
    							AuthorizationGrantType.REFRESH_TOKEN
							)
    					.stream()
    					.map(AuthorizationGrantType::getValue)
    					.collect(Collectors.joining(",")))
				.redirectUris(
						List.of(
								"http://localhost:8080/login/oauth2/code/my-client"
							)
						.stream()
    					.collect(Collectors.joining(",")))
				.scopes(
						List.of(
								"openid", 
								"read"
							)
						.stream()
						.collect(Collectors.joining(",")))
    			.build();
    	
    	clientRepository.save(client);
    	
        // 1. 建立權限
        Authority authorityRead = saveAuthorityIfNotExists("READ");
        Authority authorityWrite = saveAuthorityIfNotExists("WRITE");
        Authority authorityDelete = saveAuthorityIfNotExists("DELETE");

        // 2. 建立角色並關聯權限
        Role roleUser = saveRoleIfNotExists("USER", Set.of(authorityRead));
        Role roleAdmin = saveRoleIfNotExists("ADMIN", Set.of(authorityRead, authorityWrite, authorityDelete));

        // 3. 建立 admin 使用者
        String adminUsername = "admin";
        if (userRepository.findByUsername(adminUsername).isEmpty()) {
            User admin = User.builder()
                    .username(adminUsername)
                    .password(passwordEncoder.encode("admin"))
                    .roles(Set.of(roleAdmin))
                    .build();
            userRepository.save(admin);
            log.info("建立 admin 使用者（帳號：{}）", adminUsername);
        } else {
            log.info("admin 使用者已存在，略過建立");
        }

        // 4. 建立一般使用者
        String userUsername = "user";
        if (userRepository.findByUsername(userUsername).isEmpty()) {
            User user = User.builder()
                    .username(userUsername)
                    .password(passwordEncoder.encode("user"))
                    .roles(Set.of(roleUser))
                    .build();
            userRepository.save(user);
            log.info("建立一般使用者（帳號：{}）", userUsername);
        } else {
            log.info("一般使用者已存在，略過建立");
        }
    }

    @Transactional(readOnly = true)
    private Authority saveAuthorityIfNotExists(String name) {
        return authorityRepository.findByName(name)
                .orElseGet(() -> authorityRepository.save(new Authority(null, name)));
    }

    @Transactional(readOnly = true)
    private Role saveRoleIfNotExists(String name, Set<Authority> authorities) {
        return roleRepository.findByName(name)
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName(name);
                    role.setAuthorities(authorities);
                    return roleRepository.save(role);
                });
    }
}
