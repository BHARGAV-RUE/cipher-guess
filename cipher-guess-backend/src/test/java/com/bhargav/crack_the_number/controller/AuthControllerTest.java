package com.bhargav.crack_the_number.controller;

import com.bhargav.crack_the_number.dto.LoginRequest;
import com.bhargav.crack_the_number.dto.RegisterRequest;
import com.bhargav.crack_the_number.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setup() {
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("bhargav");
        registerRequest.setPassword("pas@123");

        loginRequest = new LoginRequest();
        loginRequest.setUsername("bhargav");
        loginRequest.setPassword("pas@123");
    }

    @Test
    void register_ShouldReturnSuccessMessage_WhenValidRequest(){
        when(authService.register(registerRequest)).thenReturn("User Registered Successfully");

        String result = authController.register(registerRequest);

        assertEquals("User Registered Successfully", result);
        verify(authService, times(1)).register(registerRequest);
    }

    @Test
    void register_ShouldReturnFailureMessage_WhenUserAlreadyExists() {
        when(authService.register(registerRequest)).thenReturn("Username already taken.");

        String result = authController.register(registerRequest);

        assertEquals("Username already taken.", result);
        verify(authService, times(1)).register(registerRequest);
    }

    @Test
    void login_ShouldReturnToken_WhenCredentialsAreValid() {
        String fakeToken = "Bearer eyJhbGciOiJIUzI1NiJ9.fake.token";
        when(authService.login(loginRequest)).thenReturn(fakeToken);

        String result = authController.login(loginRequest);

        assertEquals(fakeToken, result);
        verify(authService, times(1)).login(loginRequest);
    }

    @Test
    void login_ShouldReturnErrorMessage_WhenCredentialsAreInvalid() {
        when(authService.login(loginRequest)).thenReturn("Invalid Credentials");

        String result = authController.login(loginRequest);

        assertEquals("Invalid Credentials", result);
        verify(authService, times(1)).login(loginRequest);
    }

    @Test
    void logout_ShouldReturnSuccessMessage_WhenTokenIsValid() {
        String token = "some-valid-uuid-token";
        HttpServletRequest httpServletRequestn = mock(HttpServletRequest.class);
        when(httpServletRequestn.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(authService.logout(token)).thenReturn("Logout Successfully");

        String result = authController.logout(httpServletRequestn);

        assertEquals("Logout Successfully", result);
        verify(authService, times(1)).logout(token);
    }

    @Test
    void logout_ShouldReturnFailureMessage_WhenTokenIsInvalid() {
        String token = "invalid-or-expired-token";
        HttpServletRequest httpRequest = mock(HttpServletRequest.class);
        when(httpRequest.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(authService.logout(token)).thenReturn("Token not found or already logged out");

        String result = authController.logout(httpRequest);

        assertEquals("Token not found or already logged out", result);
        verify(authService, times(1)).logout(token);
    }

    @Test
    void logout_ShouldStripBearerPrefix_BeforeCallingService() {
        String rawToken = "abc123token";
        HttpServletRequest httpRequest = mock(HttpServletRequest.class);
        when(httpRequest.getHeader("Authorization")).thenReturn("Bearer " + rawToken);
        when(authService.logout(rawToken)).thenReturn("Logged out successfully");

        authController.logout(httpRequest);

        // Verifies the controller strips "Bearer " and passes only the raw token
        verify(authService).logout(rawToken);
        verify(authService, never()).logout("Bearer " + rawToken);
    }
}
