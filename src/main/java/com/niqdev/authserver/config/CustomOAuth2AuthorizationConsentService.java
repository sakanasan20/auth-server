package com.niqdev.authserver.config;

import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2AuthorizationConsentService implements OAuth2AuthorizationConsentService {
    
    @Override
    public void save(OAuth2AuthorizationConsent authorizationConsent) {
        // 不儲存 consent，讓每次都需要重新同意
        // 這樣即使客戶端清除了 cookies，服務器端也不會記住
    }
    
    @Override
    public void remove(OAuth2AuthorizationConsent authorizationConsent) {
        // 不需要實作，因為我們不儲存
    }
    
    @Override
    public OAuth2AuthorizationConsent findById(String registeredClientId, String principalName) {
        // 總是返回 null，讓每次都需要重新同意
        return null;
    }
}
