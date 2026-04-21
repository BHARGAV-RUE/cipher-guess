package com.bhargav.crack_the_number.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "game_session")
public class GameSession {
    @Id
    //session Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    //user_id used as foreign key
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, unique = false)
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    //Total number of guesses taken by user
    @Column(name = "guesses_taken", nullable = false, unique = false)
    private int guessesTaken;

    public int getGuessesTaken() {
        return guessesTaken;
    }

    public void setGuessesTaken(int guessesTaken) {
        this.guessesTaken = guessesTaken;
    }

    //Total games won by user
    @Column(name = "won", nullable = false, unique = false)
    private boolean won;

    public boolean getWon() {
        return won;
    }

    public void setWon(boolean won) {
        this.won = won;
    }

    //when user started the gameplay
    @Column(name = "played_at", nullable = false, unique = false)
    private LocalDateTime playedAt;

    @PrePersist
    public void onPlayed() {
        this.playedAt = LocalDateTime.now();
    }

    //used to define the levels in game
    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty", nullable = false)
    private Difficulty difficulty;

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }
}