package com.niqdev.authserver.support.factory;

import java.time.LocalDateTime;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.niqdev.authserver.entity.User;

public class UserTestFactory {
	
	private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public static User createDefaultUser() {
        return User.builder()
                .username("defaultUser")
                .password(encoder.encode("defaultPassword"))
                .email("default@example.com")
                .enabled(true)
                .accountNonLocked(true)
                .accountNonExpired(true)
                .credentialsNonExpired(true)
                .createdAt(LocalDateTime.now())
                .createdBy("test")
                .updatedAt(LocalDateTime.now())
                .updatedBy("test")
                .build();
    }

    public static User createUser(String username, String email) {
        return User.builder()
                .username(username)
                .password(encoder.encode("defaultPassword"))
                .email(email)
                .enabled(true)
                .accountNonLocked(true)
                .accountNonExpired(true)
                .credentialsNonExpired(true)
                .createdAt(LocalDateTime.now())
                .createdBy("test")
                .updatedAt(LocalDateTime.now())
                .updatedBy("test")
                .build();
    }

    public static User createDisabledUser() {
        return User.builder()
                .username("disabledUser")
                .password(encoder.encode("defaultPassword"))
                .email("disabled@example.com")
                .enabled(false)
                .accountNonLocked(true)
                .accountNonExpired(true)
                .credentialsNonExpired(true)
                .createdAt(LocalDateTime.now())
                .createdBy("test")
                .updatedAt(LocalDateTime.now())
                .updatedBy("test")
                .build();
    }

    public static User createLockedUser() {
        return User.builder()
                .username("lockedUser")
                .password(encoder.encode("defaultPassword"))
                .email("locked@example.com")
                .enabled(true)
                .accountNonLocked(false)
                .accountNonExpired(true)
                .credentialsNonExpired(true)
                .createdAt(LocalDateTime.now())
                .createdBy("test")
                .updatedAt(LocalDateTime.now())
                .updatedBy("test")
                .build();
    }

    public static User createExpiredUser() {
        return User.builder()
                .username("expiredUser")
                .password(encoder.encode("defaultPassword"))
                .email("expired@example.com")
                .enabled(true)
                .accountNonLocked(true)
                .accountNonExpired(false)
                .credentialsNonExpired(true)
                .createdAt(LocalDateTime.now())
                .createdBy("test")
                .updatedAt(LocalDateTime.now())
                .updatedBy("test")
                .build();
    }

    public static User createCredentialsExpiredUser() {
        return User.builder()
                .username("credsExpiredUser")
                .password(encoder.encode("defaultPassword"))
                .email("credsExpired@example.com")
                .enabled(true)
                .accountNonLocked(true)
                .accountNonExpired(true)
                .credentialsNonExpired(false)
                .createdAt(LocalDateTime.now())
                .createdBy("test")
                .updatedAt(LocalDateTime.now())
                .updatedBy("test")
                .build();
    }
}
