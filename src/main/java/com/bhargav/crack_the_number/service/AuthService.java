package com.bhargav.crack_the_number.service;

import com.bhargav.crack_the_number.dto.LoginRequest;
import com.bhargav.crack_the_number.dto.RegisterRequest;
import com.bhargav.crack_the_number.model.AuthToken;
import com.bhargav.crack_the_number.model.User;
import com.bhargav.crack_the_number.repository.AuthTokenRepository;
import com.bhargav.crack_the_number.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthTokenRepository  authTokenRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public String register(RegisterRequest request){

        if(userRepository.findUserByUsername(request.getUsername()) != null)
        {
            return "Username already taken";
        }

        if(userRepository.findByEmail(request.getEmail()) != null) {
            return "Email already taken";
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
        return "Registered Successfully!";
    }

    public String login(LoginRequest request){
        User user = userRepository.findUserByUsername(request.getUsername());
        if(user == null){
            return "User not found";
        }
        if(!passwordEncoder.matches(request.getPassword(),user.getPasswordHash())){
            return "Invalid password";
        }
        AuthToken token = new AuthToken();
        token.setUser(user);
        token.setExpiresAt(LocalDateTime.now().plusHours(24));
        authTokenRepository.save(token);

        return token.getToken();
    }

    public String logout(String token){
        AuthToken authToken = authTokenRepository.findByToken(token);
        if(authToken == null){
            return "Invalid token";
        }
        authTokenRepository.delete(authToken);
        return "Logged out successfully";
    }
}
