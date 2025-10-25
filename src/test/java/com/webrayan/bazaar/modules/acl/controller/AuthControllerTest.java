package com.webrayan.bazaar.modules.acl.controller;

import com.webrayan.bazaar.modules.acl.dto.LoginUserDto;
import com.webrayan.bazaar.modules.acl.dto.SignupUserDto;
import com.webrayan.bazaar.modules.acl.entity.User;
import com.webrayan.bazaar.modules.acl.repository.UserRepository;
import com.webrayan.bazaar.core.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class AuthControllerTest {
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private UserDetailsService userDetailsService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoginSuccess() {
        String username = "testuser";
        String password = "testpass";
        UserDetails userDetails = mock(UserDetails.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(jwtTokenProvider.generateToken(userDetails)).thenReturn("mocked-jwt-token");

        LoginUserDto dto = new LoginUserDto();
        dto.setUsername(username);
        dto.setPassword(password);
        ResponseEntity<?> response = authController.login(dto);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        @SuppressWarnings("unchecked")
        java.util.Map<String, Object> responseBody = (java.util.Map<String, Object>) response.getBody();
        assertTrue(responseBody.containsKey("token"));
        assertTrue((Boolean) responseBody.get("success"));
    }

    @Test
    void testLoginFailure() {
        String username = "testuser";
        String password = "wrongpass";
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new org.springframework.security.core.AuthenticationException("Bad credentials"){});

        LoginUserDto dto = new LoginUserDto();
        dto.setUsername(username);
        dto.setPassword(password);
        ResponseEntity<?> response = authController.login(dto);
        assertEquals(401, response.getStatusCode().value());
        assertNotNull(response.getBody());
        @SuppressWarnings("unchecked")
        java.util.Map<String, Object> responseBody = (java.util.Map<String, Object>) response.getBody();
        assertEquals("Invalid username or password", responseBody.get("message"));
        assertFalse((Boolean) responseBody.get("success"));
    }

   // TODO: check this test
   //  @Test
    void testSignupSuccess() {
        String email = "test@example.com";
        String password = "password";
        String name = "Test User";
        
        when(userRepository.findByEmail(email)).thenReturn(java.util.Optional.empty());
        when(passwordEncoder.encode(password)).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        SignupUserDto dto = new SignupUserDto();
        dto.setEmail(email);
        dto.setPassword(password);
        dto.setUsername(name);
        
        ResponseEntity<?> response = authController.signup(dto);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        @SuppressWarnings("unchecked")
        java.util.Map<String, Object> responseBody = (java.util.Map<String, Object>) response.getBody();
        assertEquals("User registered successfully", responseBody.get("message"));
        assertTrue((Boolean) responseBody.get("success"));
    }

    @Test
    void testSignupDuplicateEmail() {
        String email = "test@example.com";
        String password = "password";
        String name = "Test User";
        
        when(userRepository.findByEmail(email)).thenReturn(java.util.Optional.of(new User()));

        SignupUserDto dto = new SignupUserDto();
        dto.setEmail(email);
        dto.setPassword(password);
        dto.setUsername(name);
        
        ResponseEntity<?> response = authController.signup(dto);
        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());
        @SuppressWarnings("unchecked")
        java.util.Map<String, Object> responseBody = (java.util.Map<String, Object>) response.getBody();
        assertEquals("Email already registered", responseBody.get("message"));
        assertFalse((Boolean) responseBody.get("success"));
    }

    @Test
    void testForgotPassword() {
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);
        
        when(userRepository.findByEmail(email)).thenReturn(java.util.Optional.of(user));

        ResponseEntity<?> response = authController.forgotPassword(email);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        String responseBody = (String) response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.contains("Reset token:"));
    }

    @Test
    void testForgotPasswordUserNotFound() {
        String email = "nonexistent@example.com";
        
        when(userRepository.findByEmail(email)).thenReturn(java.util.Optional.empty());

        ResponseEntity<?> response = authController.forgotPassword(email);
        assertEquals(400, response.getStatusCode().value());
        assertEquals("User not found", response.getBody());
    }
}
