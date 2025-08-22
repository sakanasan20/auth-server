package com.niqdev.authserver.config;

import com.niqdev.authserver.entity.User;
import com.niqdev.authserver.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

public class JwtTokenCustomizer implements OAuth2TokenCustomizer<JwtEncodingContext> {

    private final UserRepository userRepository;

    public JwtTokenCustomizer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void customize(JwtEncodingContext context) {
        if (OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())) {
            JwtClaimsSet.Builder claims = context.getClaims();
            
            // 取得使用者資訊
            String username = context.getPrincipal().getName();
            User user = userRepository.findByUsername(username).orElse(null);
            
            if (user != null) {
                // 添加角色資訊
                List<String> roles = user.getRoles().stream()
                        .map(role -> role.getName())
                        .collect(Collectors.toList());
                claims.claim("roles", roles);
                
                // 添加權限資訊
                List<String> authorities = user.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList());
                claims.claim("authorities", authorities);
                
                // 添加使用者 ID
                claims.claim("user_id", user.getId());
                
                // 添加自定義 scope
                claims.claim("scope", "openid profile email roles authorities");
            }
        }
    }
}
