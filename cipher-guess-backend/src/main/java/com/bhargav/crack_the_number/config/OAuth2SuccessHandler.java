package com.bhargav.crack_the_number.config;

import com.bhargav.crack_the_number.model.AuthToken;
import com.bhargav.crack_the_number.model.User;
import com.bhargav.crack_the_number.repository.AuthTokenRepository;
import com.bhargav.crack_the_number.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthTokenRepository authTokenRepository;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email    = oAuth2User.getAttribute("email");
        String name     = oAuth2User.getAttribute("name");
        String username = name.replaceAll("\\s+", "").toLowerCase();

        User user = userRepository.findByEmail(email);
        if (user == null) {
            user = new User();
            user.setEmail(email);
            user.setUsername(username);
            user.setPasswordHash("GOOGLE_AUTH");
            userRepository.save(user);
        }

        AuthToken token = new AuthToken();
        token.setUser(user);
        token.setExpiresAt(LocalDateTime.now().plusHours(24));
        authTokenRepository.save(token);

        // FIX: Use raw Set-Cookie header instead of Java's Cookie API
        // because Jakarta Servlet's Cookie class has no SameSite support.
        // SameSite=None is required for cross-origin cookies (Vercel → Render).
        // Without it, Chrome and Safari silently block the cookie entirely.
        String cookieValue = "auth_token=" + token.getToken()
                + "; HttpOnly"
                + "; Secure"
                + "; SameSite=None"
                + "; Path=/"
                + "; Max-Age=86400";
        response.setHeader("Set-Cookie", cookieValue);

        String encodedUsername = URLEncoder.encode(user.getUsername(), StandardCharsets.UTF_8);
        response.sendRedirect(frontendUrl + "/dashboard?username=" + encodedUsername);
    }
}