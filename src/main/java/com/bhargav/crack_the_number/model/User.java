package com.bhargav.crack_the_number.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {
    @Id
    //User ID creation
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    public int getId(){
        return id;
    }
    public void setId(int id){
        this.id = id;
    }

    //Saving Username
    @Column( name = "username", nullable = false, unique = true)
    private String username;
    public String getUsername(){
        return username;
    }
    public void setUsername(String username){
        this.username = username;
    }

    //Storing hash value of password
    @Column(name = "password_hash", nullable = false, unique = false)
    private String passwordHash;
    public String getPasswordHash(){
        return passwordHash;
    }
    public void setPasswordHash(String passwordHash){
        this.passwordHash = passwordHash;
    }

    //storing when the user registered
    @Column(name = "created_at", nullable = false, unique = false)
    private LocalDateTime createdAt;
    @PrePersist
    public void onCreate(){
        this.createdAt = LocalDateTime.now();
    }

    //storing email or user
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    public String getEmail(){
        return email;
    }
    public void setEmail(String email){
        this.email = email;
    }

    //storing history of games played by user
    @Column(name = "total_games_played", nullable = false, unique = false)
    private int totalGamesPlayed =0;
    public int getTotalGamesPlayed(){
        return totalGamesPlayed;
    }
    public void setTotalGamesPlayed(int totalGamesPlayed){
        this.totalGamesPlayed = totalGamesPlayed;
    }
}
