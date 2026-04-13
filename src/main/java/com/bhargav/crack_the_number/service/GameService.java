package com.bhargav.crack_the_number.service;
import com.bhargav.crack_the_number.model.GameSession;
import com.bhargav.crack_the_number.dto.StatsResponse;
import java.util.*;


import com.bhargav.crack_the_number.model.Difficulty;
import com.bhargav.crack_the_number.model.User;
import com.bhargav.crack_the_number.repository.GameSessionRepository;
import com.bhargav.crack_the_number.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GameService {
    @Autowired
    private GameSessionRepository gameSessionRepository;

    @Autowired
    private UserRepository userRepository;

    private Map<Integer, Integer> activeTargets = new HashMap<>();
    private Map<Integer, Integer> activeGuessesCount = new HashMap<>();
    private Map<Integer,Difficulty> activeDifficulty = new HashMap<>();

    public String startGame(User user, Difficulty difficulty){
        int max;
        switch(difficulty) {
            case EASY -> max = 10;
            case MEDIUM -> max = 50;
            case HARD -> max = 100;
            case GOD -> max = 1000;
            default ->  max = 10;
        }
        Random rand = new Random();
        int target = rand.nextInt(max) + 1;

        activeTargets.put(user.getId(), target);
        activeGuessesCount.put(user.getId(), 0);
        activeDifficulty.put(user.getId(), difficulty);

        return "Game started! Guess a number between 1 and " + max;
    }

    public String checkGuess(User user, int guess){
        int target = activeTargets.get(user.getId());
        int guessCount = activeGuessesCount.get(user.getId());
        guessCount++;
        activeGuessesCount.put(user.getId(), guessCount);

        if(guess > target){

            return "TOO HIGH";
        }
        if(guess < target){
            return "TOO LOW";
        }

        GameSession session = new GameSession();
        session.setUser(user);
        session.setWon(true);
        session.setGuessesTaken(guessCount);
        session.setDifficulty(activeDifficulty.get(user.getId()));
        gameSessionRepository.save(session);

        activeTargets.remove(user.getId());
        activeGuessesCount.remove(user.getId());
        activeDifficulty.remove(user.getId());

        return "CORRECT! You got it in " + guessCount + "guesses!";
    }

    public  StatsResponse getStats(User user){
        List<GameSession> sessions = gameSessionRepository.findByUser(user);

        int totalGames = sessions.size();
        int totalWins = 0;
        int bestScore = Integer.MAX_VALUE;

        for(GameSession session : sessions){
            if(session.getWon()){
                totalWins++;
                if (session.getGuessesTaken() < bestScore){
                    bestScore = session.getGuessesTaken();
                }
            }
        }
        int totalLosses = totalGames - totalWins;

        double winRate = 0;
        if (totalGames > 0){
            winRate = (totalWins * 100.0) / totalGames;
        }
        if(bestScore == Integer.MAX_VALUE){
            bestScore = 0;
        }
        StatsResponse stats = new StatsResponse();
        stats.setTotalGames(totalGames);
        stats.setTotalWins(totalWins);
        stats.setTotalLosses(totalLosses);
        stats.setWinRate(winRate);
        stats.setBestScore(bestScore);

        return stats;
    }
}
