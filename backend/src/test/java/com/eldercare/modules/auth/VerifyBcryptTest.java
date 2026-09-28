package com.eldercare.modules.auth;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class VerifyBcryptTest {
    @Test
    public void testBcrypt() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String raw = "Nhms@Demo2026";
        String hash = "$2a$12$iea3i2TqNaXHNcsIaPj/8esgGOwTs869f3lJurRNf9khEDsQ8CksC";
        boolean match = encoder.matches(raw, hash);
        System.out.println("====== BCRYPT MATCH IN JAVA: " + match + " ======");
        assertTrue(match);
    }
}
