package com.niqdev.authserver.bootstrap;

import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.niqdev.authserver.entity.Authority;
import com.niqdev.authserver.entity.Role;
import com.niqdev.authserver.entity.User;
import com.niqdev.authserver.repository.AuthorityRepository;
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
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public void run(String... args) {
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
