package com.bhargav.crack_the_number.service;

import com.bhargav.crack_the_number.dto.StatsResponse;
import com.bhargav.crack_the_number.model.*;
import com.bhargav.crack_the_number.repository.GameSessionRepository;
import com.bhargav.crack_the_number.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class GameService {

    @Autowired
    private GameSessionRepository gameSessionRepository;

    @Autowired
    private UserRepository userRepository;

    public String startGame(User user, Difficulty difficulty) {
        // If user already has an active game, forfeit it automatically
        Optional<GameSession> existing =
                gameSessionRepository.findByUserAndStatus(user, SessionStatus.ACTIVE);
        if (existing.isPresent()) {
            GameSession old = existing.get();
            old.setStatus(SessionStatus.FORFEITED);
            old.setWon(false);
            gameSessionRepository.save(old);
        }

        int max = switch (difficulty) {
            case EASY   -> 10;
            case MEDIUM -> 50;
            case HARD   -> 100;
            case GOD    -> 1000;
        };

        int target = new Random().nextInt(max) + 1;

        GameSession session = new GameSession();
        session.setUser(user);
        session.setDifficulty(difficulty);
        session.setTarget(target);
        session.setGuessesTaken(0);
        session.setWon(false);
        session.setStatus(SessionStatus.ACTIVE);
        gameSessionRepository.save(session);

        return "Game started! Guess a number between 1 and " + max;
    }

    public String checkGuess(User user, int guess) {
        // FIX: no more NullPointerException — fetch from DB
        Optional<GameSession> opt =
                gameSessionRepository.findByUserAndStatus(user, SessionStatus.ACTIVE);

        if (opt.isEmpty()) {
            return "No active game. Please start a game first.";
        }

        GameSession session = opt.get();
        int target     = session.getTarget();
        int guessCount = session.getGuessesTaken() + 1;
        session.setGuessesTaken(guessCount);

        if (guess > target) {
            gameSessionRepository.save(session);
            return "TOO HIGH";
        }
        if (guess < target) {
            gameSessionRepository.save(session);
            return "TOO LOW";
        }

        // Correct guess
        session.setWon(true);
        session.setStatus(SessionStatus.WON);
        gameSessionRepository.save(session);

        return "CORRECT! You got it in " + guessCount + " guesses!";
    }

    public String forfeit(User user) {
        Optional<GameSession> opt =
                gameSessionRepository.findByUserAndStatus(user, SessionStatus.ACTIVE);

        if (opt.isEmpty()) {
            return "No active game to forfeit.";
        }

        GameSession session = opt.get();
        int target = session.getTarget();

        session.setStatus(SessionStatus.FORFEITED);
        session.setWon(false);
        gameSessionRepository.save(session);

        user.setTotalGamesPlayed(user.getTotalGamesPlayed() + 1);
        userRepository.save(user);

        return "You forfeited. The number was " + target;
    }

    public StatsResponse getStats(User user) {
        List<GameSession> sessions = gameSessionRepository.findByUser(user);

        int totalGames = sessions.size();
        int totalWins  = 0;
        int bestScore  = Integer.MAX_VALUE;

        for (GameSession session : sessions) {
            if (session.getWon()) {
                totalWins++;
                if (session.getGuessesTaken() < bestScore) {
                    bestScore = session.getGuessesTaken();
                }
            }
        }

        int totalLosses = totalGames - totalWins;
        double winRate  = totalGames > 0 ? (totalWins * 100.0) / totalGames : 0;
        if (bestScore == Integer.MAX_VALUE) bestScore = 0;

        StatsResponse stats = new StatsResponse();
        stats.setTotalGames(totalGames);
        stats.setTotalWins(totalWins);
        stats.setTotalLosses(totalLosses);
        stats.setWinRate(winRate);
        stats.setBestScore(bestScore);

        return stats;
    }
}