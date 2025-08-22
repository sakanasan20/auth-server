package com.niqdev.authserver.config;

import com.niqdev.authserver.entity.Authority;
import com.niqdev.authserver.entity.Role;
import com.niqdev.authserver.entity.User;
import com.niqdev.authserver.repository.AuthorityRepository;
import com.niqdev.authserver.repository.RoleRepository;
import com.niqdev.authserver.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;

@Configuration
public class DataInitializer {
    
    @Bean
    CommandLineRunner initData(
            UserRepository userRepository, 
            RoleRepository roleRepository,
            AuthorityRepository authorityRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            // 初始化權限
            Authority readAuthority = createAuthorityIfNotExists(authorityRepository, "READ");
            Authority writeAuthority = createAuthorityIfNotExists(authorityRepository, "WRITE");
            Authority deleteAuthority = createAuthorityIfNotExists(authorityRepository, "DELETE");
            Authority adminAuthority = createAuthorityIfNotExists(authorityRepository, "ADMIN");
            
            // 初始化角色
            Role userRole = createRoleIfNotExists(roleRepository, "USER", Arrays.asList(readAuthority));
            Role adminRole = createRoleIfNotExists(roleRepository, "ADMIN", 
                    Arrays.asList(readAuthority, writeAuthority, deleteAuthority, adminAuthority));
            
            // 檢查是否已有使用者，如果沒有則創建測試使用者
            if (userRepository.count() == 0) {
                // 創建一般使用者
                User user = new User(
                    "user",
                    passwordEncoder.encode("password"),
                    Arrays.asList(userRole)
                );
                userRepository.save(user);
                
                // 創建管理員使用者
                User admin = new User(
                    "admin",
                    passwordEncoder.encode("admin"),
                    Arrays.asList(adminRole)
                );
                userRepository.save(admin);
                
                System.out.println("測試使用者已創建:");
                System.out.println("- username=user, password=password (USER role)");
                System.out.println("- username=admin, password=admin (ADMIN role)");
            }
        };
    }
    
    private Authority createAuthorityIfNotExists(AuthorityRepository repository, String name) {
        return repository.findByName(name)
                .orElseGet(() -> repository.save(new Authority(name)));
    }
    
    private Role createRoleIfNotExists(RoleRepository repository, String name, List<Authority> authorities) {
        return repository.findByName(name)
                .orElseGet(() -> repository.save(new Role(name, authorities)));
    }
}
