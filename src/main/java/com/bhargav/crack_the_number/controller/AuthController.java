package com.bhargav.crack_the_number.controller;
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

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request){
        return authService.register(request);
    }
    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request){
        return  authService.login((request));
    }

    @PostMapping("/logout")
    public String logout(@RequestParam String token){
        return authService.logout(token);
    }

}
