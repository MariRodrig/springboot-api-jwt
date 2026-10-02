package com.mariana.springboot_api.security;

import com.mariana.springboot_api.service.UserDetailsService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.io.IOException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JwtAuthFilterTest {

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthFilter jwtAuthFilter;

    @Test
    void shouldContinueFilterWhenAuthorizationHeaderIsMissing() throws Exception{

        when(request.getHeader("Authorization")).thenReturn(null);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(userDetailsService);
    }

    @Test
    void shouldContinueFilterWhenAuthorizationHeaderIsNotBearer() throws Exception{

        when(request.getHeader("Authorization")).thenReturn("Basic 123");

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(userDetailsService);
    }

    @AfterEach
    void clearContext(){
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldAuthenticateUserWhenTokenIsValid() throws ServletException, IOException {

        String username = "alfredo";

        String token = JwtUtil.generateToken(username);
        UserDetails userDetails = User.withUsername(username).password("123456").authorities("USER")
                .build();

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(userDetailsService).loadUserByUsername(username);
        verify(filterChain).doFilter(request, response);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(username, SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @Test
    void shouldNotAuthenticateAgainWhenUserIsAlreadyAuthenticated() throws Exception{

        String username = "alfredo";
        String token = JwtUtil.generateToken(username);

        UsernamePasswordAuthenticationToken existingAuthentication = new UsernamePasswordAuthenticationToken(
                username, null, Collections.emptyList()
        );

        SecurityContextHolder.getContext().setAuthentication(existingAuthentication);

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verifyNoInteractions(userDetailsService);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldThrowExceptionWhenTokenIsInvalid(){

        when(request.getHeader("Authorization")).thenReturn("Bearer token-invalido");

        assertThrows(JwtException.class, () -> jwtAuthFilter.doFilterInternal(request, response, filterChain));

        verifyNoInteractions(userDetailsService);
    }
}
