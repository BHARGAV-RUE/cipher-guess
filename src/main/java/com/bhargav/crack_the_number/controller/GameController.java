package com.bhargav.crack_the_number.controller;

import com.bhargav.crack_the_number.dto.StatsResponse;
import com.bhargav.crack_the_number.model.AuthToken;
import com.bhargav.crack_the_number.model.Difficulty;
import com.bhargav.crack_the_number.model.User;
import com.bhargav.crack_the_number.repository.AuthTokenRepository;
import com.bhargav.crack_the_number.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/game")
public class GameController {
    @Autowired
    private GameService gameService;

    @Autowired
    private AuthTokenRepository authTokenRepository;

    @PostMapping("/start")
    public String start(@RequestParam String token, @RequestParam Difficulty difficulty) {
        AuthToken authToken = authTokenRepository.findByToken(token);
        User user = authToken.getUser();
        String result = gameService.startGame(user, difficulty);
        System.out.println("Result:" + result);
        return result;
    }

    @PostMapping("/guess")
    public String guess(@RequestParam String token, @RequestParam int guess){
        AuthToken authToken = authTokenRepository.findByToken(token);
        User user = authToken.getUser();
        return gameService.checkGuess(user, guess);
    }

    @GetMapping("/stats")
    public StatsResponse stats(@RequestParam String token){
        AuthToken authToken = authTokenRepository.findByToken(token);
        User user = authToken.getUser();
        return gameService.getStats(user);
    }
}
