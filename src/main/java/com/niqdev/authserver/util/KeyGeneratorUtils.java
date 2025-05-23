package com.niqdev.authserver.util;

import java.security.KeyPair;
import java.security.KeyPairGenerator;

public class KeyGeneratorUtils {
	
    public static KeyPair generateRsaKey() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            return keyPairGenerator.generateKeyPair();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate RSA key pair", e);
        }
    }
}
