package com.bhargav.crack_the_number.controller;

import com.bhargav.crack_the_number.dto.StatsResponse;
import com.bhargav.crack_the_number.model.Difficulty;
import com.bhargav.crack_the_number.model.User;
import com.bhargav.crack_the_number.service.GameService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameControllerTest {

    @Mock
    private GameService gameService;

    @InjectMocks
    private GameController gameController;

    private User mockUser;
    private HttpServletRequest httpServletRequest;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setUsername("bhargav");

        httpServletRequest = mock(HttpServletRequest.class);
        when(httpServletRequest.getAttribute("user")).thenReturn(mockUser);
    }

//    game/start
    @Test
    void start_ShouldReturnGameStartedMessage_WhenEasyDifficulty() {
        when(gameService.startGame(mockUser, Difficulty.EASY)).thenReturn(
                "Game started! Guess a number between 1 and 10."
        );

        String result = gameController.start(httpServletRequest, Difficulty.EASY);

        assertEquals("Game started! Guess a number between 1 and 10.", result);
        verify(gameService, times(1)).startGame(mockUser, Difficulty.EASY);
    }


    @Test
    void start_ShouldReturnGameStartedMessage_WhenHardDifficulty() {
        when(gameService.startGame(mockUser, Difficulty.HARD))
                .thenReturn("Game started! Guess a number between 1 and 100.");

        String result = gameController.start(httpServletRequest, Difficulty.HARD);

        assertEquals("Game started! Guess a number between 1 and 100.", result);
        verify(gameService, times(1)).startGame(mockUser, Difficulty.HARD);
    }

    @Test
    void start_ShouldExtractUserFromRequestAttribute() {
        when(gameService.startGame(mockUser, Difficulty.MEDIUM)).thenReturn("Game started!");

        gameController.start(httpServletRequest, Difficulty.MEDIUM);

        // Verifies user is being pulled from request attribute, not hardcoded
        verify(httpServletRequest, times(1)).getAttribute("user");
        verify(gameService).startGame(mockUser, Difficulty.MEDIUM);
    }


//    game/guess
    @Test
    void guess_ShouldReturnCorrectMessage_WhenGuessIsRight() {
        when(gameService.checkGuess(mockUser, 42)).thenReturn("Correct! You guessed it!");

        String result = gameController.guess(httpServletRequest, 42);

        assertEquals("Correct! You guessed it!", result);
        verify(gameService, times(1)).checkGuess(mockUser, 42);
    }

    @Test
    void guess_ShouldReturnTooHighMessage_WhenGuessIsTooHigh() {
        when(gameService.checkGuess(mockUser, 99)).thenReturn("Too high! Try again.");

        String result = gameController.guess(httpServletRequest, 99);

        assertEquals("Too high! Try again.", result);
        verify(gameService, times(1)).checkGuess(mockUser, 99);
    }

    @Test
    void guess_ShouldReturnTooLowMessage_WhenGuessIsTooLow() {
        when(gameService.checkGuess(mockUser, 1)).thenReturn("Too low! Try again.");

        String result = gameController.guess(httpServletRequest, 1);

        assertEquals("Too low! Try again.", result);
        verify(gameService, times(1)).checkGuess(mockUser, 1);
    }

    @Test
    void guess_ShouldExtractUserFromRequestAttribute() {
        when(gameService.checkGuess(mockUser, 5)).thenReturn("Too low! Try again.");

        gameController.guess(httpServletRequest, 5);

        verify(httpServletRequest, times(1)).getAttribute("user");
        verify(gameService).checkGuess(mockUser, 5);
    }

//    game/forfeit

    @Test
    void forfeit_ShouldReturnForfeitMessage_WhenCalled() {
        when(gameService.forfeit(mockUser)).thenReturn("You forfeited. The number was 42.");

        String result = gameController.forfeit(httpServletRequest);

        assertEquals("You forfeited. The number was 42.", result);
        verify(gameService, times(1)).forfeit(mockUser);
    }

    @Test
    void forfeit_ShouldExtractUserFromRequestAttribute() {
        when(gameService.forfeit(mockUser)).thenReturn("You forfeited.");

        gameController.forfeit(httpServletRequest);

        verify(httpServletRequest, times(1)).getAttribute("user");
        verify(gameService).forfeit(mockUser);
    }

//    game/stats
@Test
void stats_ShouldReturnStatsResponse_WhenUserHasPlayedGames() {
    StatsResponse mockStats = new StatsResponse();
    mockStats.setTotalGames(10);
    mockStats.setTotalWins(7);
    mockStats.setTotalLosses(3);

    when(gameService.getStats(mockUser)).thenReturn(mockStats);

    StatsResponse result = gameController.stats(httpServletRequest);

    assertEquals(10, result.getTotalGames());
    assertEquals(7, result.getTotalWins());
    assertEquals(3, result.getTotalLosses());
    verify(gameService, times(1)).getStats(mockUser);
}

    @Test
    void stats_ShouldReturnEmptyStats_WhenUserHasNotPlayedAnyGame() {
        StatsResponse emptyStats = new StatsResponse();
        emptyStats.setTotalGames(0);
        emptyStats.setTotalWins(0);
        emptyStats.setTotalLosses(0);

        when(gameService.getStats(mockUser)).thenReturn(emptyStats);

        StatsResponse result = gameController.stats(httpServletRequest);

        assertEquals(0, result.getTotalGames());
        assertEquals(0, result.getTotalWins());
        assertEquals(0, result.getTotalLosses());
        verify(gameService, times(1)).getStats(mockUser);
    }

    @Test
    void stats_ShouldExtractUserFromRequestAttribute() {
        when(gameService.getStats(mockUser)).thenReturn(new StatsResponse());

        gameController.stats(httpServletRequest);

        verify(httpServletRequest, times(1)).getAttribute("user");
        verify(gameService).getStats(mockUser);
    }
}
