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
import java.util.UUID;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthTokenRepository authTokenRepository;

    @Value("${app.frontend.prod}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        try {
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

            String email = oAuth2User.getAttribute("email");
            String name  = oAuth2User.getAttribute("name");

            // ── FIXED: null-safe username derivation ──────────────────────────
            // Google can return null for "name" when the account has no display
            // name set, or when the granted scope doesn't include profile.
            // Fall back to the portion before "@" in the email, then to a random
            // suffix so we always have a valid, non-blank username.
            String username;
            if (name != null && !name.isBlank()) {
                username = name.replaceAll("\\s+", "").toLowerCase();
            } else if (email != null && email.contains("@")) {
                username = email.split("@")[0].toLowerCase().replaceAll("[^a-z0-9]", "");
            } else {
                username = "user" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
            }

            // ── FIXED: null-safe email guard ──────────────────────────────────
            if (email == null || email.isBlank()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Google did not return an email address. "
                        + "Make sure your Google account has a verified email.");
                return;
            }

            // ── Upsert user ───────────────────────────────────────────────────
            User user = userRepository.findByEmail(email);
            if (user == null) {
                user = new User();
                user.setEmail(email);
                user.setUsername(username);
                user.setPasswordHash("GOOGLE_AUTH");
                userRepository.save(user);
            }

            // ── Issue token ───────────────────────────────────────────────────
            AuthToken token = new AuthToken();
            token.setUser(user);
            token.setExpiresAt(LocalDateTime.now().plusHours(24));
            authTokenRepository.save(token);

            // SameSite=None is required for cross-origin cookies (Vercel → Render)
            String cookieValue = "auth_token=" + token.getToken()
                    + "; HttpOnly"
                    + "; Secure"
                    + "; SameSite=None"
                    + "; Path=/"
                    + "; Max-Age=86400";
            response.setHeader("Set-Cookie", cookieValue);

            String encodedUsername = URLEncoder.encode(user.getUsername(), StandardCharsets.UTF_8);
            response.sendRedirect(frontendUrl + "/dashboard?username=" + encodedUsername);

        } catch (Exception ex) {
            // Log the real cause so you can see it in Render's logs
            System.err.println("[OAuth2SuccessHandler] Error during Google sign-in: "
                    + ex.getClass().getSimpleName() + " — " + ex.getMessage());
            ex.printStackTrace();

            // Redirect to frontend with a user-visible error instead of a blank 500
            response.sendRedirect(frontendUrl + "/login?error=oauth_failed");
        }
    }
}