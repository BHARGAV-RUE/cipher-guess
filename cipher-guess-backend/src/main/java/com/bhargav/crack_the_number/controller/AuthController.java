package com.bhargav.crack_the_number.controller;

import com.bhargav.crack_the_number.service.RateLimiterService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import com.bhargav.crack_the_number.dto.LoginRequest;
import com.bhargav.crack_the_number.dto.RegisterRequest;
import com.bhargav.crack_the_number.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private RateLimiterService rateLimiterService;

    // Helper to get real IP even behind a proxy
    private String getClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isEmpty()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request,
                                           HttpServletRequest httpRequest) {
        String ip = getClientIp(httpRequest);
        if (!rateLimiterService.allowRegister(ip)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("Too many registrations. Try again in an hour.");
        }
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request,
                                        HttpServletRequest httpRequest) {
        String ip = getClientIp(httpRequest);
        if (!rateLimiterService.allowLogin(ip)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("Too many login attempts. Try again in 15 minutes.");
        }
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        String token = authHeader.substring(7);
        return authService.logout(token);
    }
}