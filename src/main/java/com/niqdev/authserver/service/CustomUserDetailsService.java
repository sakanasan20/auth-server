package com.niqdev.authserver.service;

import com.niqdev.authserver.entity.User;
import com.niqdev.authserver.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    private final UserRepository userRepository;
    
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        
        // 確保角色和權限被正確載入
        if (user.getRoles() != null) {
            user.getRoles().forEach(role -> {
                if (role.getAuthorityList() != null) {
                    role.getAuthorityList().size(); // 觸發 lazy loading
                }
            });
        }
        
        return user;
    }
}
