package com.bhargav.crack_the_number.model;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;

@Entity
@Table(name = "auth_token")
public class AuthToken {
    //token ID creation
    @Id
    @UuidGenerator
    private String token;
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }

    //user_id as foreign key
    @ManyToOne
    @JoinColumn(name = "user_id", unique = false, nullable = false)
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    //Expiry of token
    @Column(name = "expires_at", unique = false, nullable = false)
    private LocalDateTime expiresAt;
    public void onExpired(){
        this.expiresAt = LocalDateTime.now();
    }
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    //token creation
    @Column(name = "created_at", nullable = false, unique = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
