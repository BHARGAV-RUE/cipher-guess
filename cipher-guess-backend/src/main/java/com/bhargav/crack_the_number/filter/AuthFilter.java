package com.bhargav.crack_the_number.filter;

import com.bhargav.crack_the_number.model.AuthToken;
import com.bhargav.crack_the_number.repository.AuthTokenRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;          // ADD
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class AuthFilter extends OncePerRequestFilter {

    @Autowired
    private AuthTokenRepository authTokenRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        if (path.contains("/auth/login")    ||
                path.contains("/auth/register") ||
                path.contains("/oauth2")        ||
                path.contains("/login/oauth2")  ||
                path.equals("/")                ||
                path.contains("/actuator")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = null;

        // 1. Try Authorization header first (regular login)
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }

        // 2. Fall back to HttpOnly cookie (OAuth2 login)  ← ADD
        if (token == null && request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("auth_token".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        // 3. Fall back to query param (keep for backward compat, can remove later)
        if (token == null) {
            token = request.getParameter("token");
        }

        if (token == null) {
            response.setStatus(401);
            response.getWriter().write("Missing token");
            return;
        }

        AuthToken authToken = authTokenRepository.findByToken(token);
        if (authToken == null) {
            response.setStatus(401);
            response.getWriter().write("Invalid Token");
            return;
        }
        if (authToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            response.setStatus(401);
            response.getWriter().write("Token expired");
            return;
        }

        request.setAttribute("user", authToken.getUser());
        filterChain.doFilter(request, response);
    }
}