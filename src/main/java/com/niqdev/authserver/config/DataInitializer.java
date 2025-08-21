package com.niqdev.authserver.config;

import com.niqdev.authserver.entity.User;
import com.niqdev.authserver.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;

@Configuration
public class DataInitializer {
    
    @Bean
    CommandLineRunner initData(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // 檢查是否已有使用者，如果沒有則創建測試使用者
            if (userRepository.count() == 0) {
                User user = new User(
                    "user",
                    passwordEncoder.encode("password"),
                    Arrays.asList("USER")
                );
                userRepository.save(user);
                
                System.out.println("測試使用者已創建: username=user, password=password");
            }
        };
    }
}
