package com.bhargav.crack_the_number.filter;

import com.bhargav.crack_the_number.model.AuthToken;
import com.bhargav.crack_the_number.repository.AuthTokenRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
// This verifies the token generated during login or registeration to let the user enter or not
public class AuthFilter extends OncePerRequestFilter {
    @Autowired
    private AuthTokenRepository authTokenRepository;

    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        if(path.contains("/auth/login") || path.contains("/auth/register")){
            filterChain.doFilter(request, response);
            return;
        }
        String token = request.getHeader("Authorization");
        if(token != null && token.startsWith("Bearer ")){
            token = token.substring(7);
        }
        else {
            token = request.getParameter("token");
        }
        if(token == null){
            response.setStatus(401);
            response.getWriter().write("Missing token");
            return;
        }

        AuthToken authToken = authTokenRepository.findByToken(token);
        if(authToken == null){
            response.setStatus(401);
            response.getWriter().write("Invalid Token");
            return;
        }
        if(authToken.getExpiresAt().isBefore(LocalDateTime.now())){
            response.setStatus(401);
            response.getWriter().write("Token expired");
            return;
        }

        request.setAttribute("user", authToken.getUser());
        filterChain.doFilter(request, response);
    }
}
