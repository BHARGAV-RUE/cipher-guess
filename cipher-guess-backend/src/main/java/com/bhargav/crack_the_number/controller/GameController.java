package com.bhargav.crack_the_number.controller;

import com.bhargav.crack_the_number.dto.StatsResponse;
import com.bhargav.crack_the_number.model.Difficulty;
import com.bhargav.crack_the_number.model.User;
import com.bhargav.crack_the_number.service.GameService;
import com.bhargav.crack_the_number.service.RateLimiterService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/game")
public class GameController {

    @Autowired
    private GameService gameService;

    @Autowired
    private RateLimiterService rateLimiterService;

    private String getClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isEmpty()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    @PostMapping("/start")
    public ResponseEntity<String> start(HttpServletRequest request,
                                        @RequestParam Difficulty difficulty) {
        if (!rateLimiterService.allowGame(getClientIp(request))) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Slow down!");
        }
        User user = (User) request.getAttribute("user");
        return ResponseEntity.ok(gameService.startGame(user, difficulty));
    }

    @PostMapping("/guess")
    public ResponseEntity<String> guess(HttpServletRequest request,
                                        @RequestParam int guess) {
        if (!rateLimiterService.allowGame(getClientIp(request))) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Slow down!");
        }
        User user = (User) request.getAttribute("user");
        return ResponseEntity.ok(gameService.checkGuess(user, guess));
    }

    @PostMapping("/forfeit")
    public ResponseEntity<String> forfeit(HttpServletRequest request) {
        if (!rateLimiterService.allowGame(getClientIp(request))) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Slow down!");
        }
        User user = (User) request.getAttribute("user");
        return ResponseEntity.ok(gameService.forfeit(user));
    }

    @GetMapping("/stats")
    public ResponseEntity<?> stats(HttpServletRequest request) {
        if (!rateLimiterService.allowGame(getClientIp(request))) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Slow down!");
        }
        User user = (User) request.getAttribute("user");
        return ResponseEntity.ok(gameService.getStats(user));
    }
}