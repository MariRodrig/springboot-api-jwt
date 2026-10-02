package com.mariana.springboot_api.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JwtUtilTest {

    @Test
    void shouldGenerateToken(){

        String username = "Alfredo";

        String token = JwtUtil.generateToken(username);

        assertNotNull(token);
        assertFalse(token.isBlank());
        String extractedUsername = JwtUtil.extractUsername(token);
        assertEquals(username, extractedUsername);
    }

    @Test
    void shouldValidateToken(){

        String username = "Alfredo";

        String token = JwtUtil.generateToken(username);
        boolean isValid = JwtUtil.validateToken(token);
        assertTrue(isValid);
    }

    @Test
    void shouldReturnFalseForInvalidToken(){

        String invalidToken = "token-invalido";

        boolean isValid = JwtUtil.validateToken(invalidToken);
        assertFalse(isValid);
    }
}
