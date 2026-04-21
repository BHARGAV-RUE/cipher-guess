package com.bhargav.crack_the_number.repository;

import com.bhargav.crack_the_number.model.GameSession;
import com.bhargav.crack_the_number.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
// Fetches all the game sessions for given user
public interface GameSessionRepository extends JpaRepository<GameSession,Integer> {
    List<GameSession> findByUser(User user);
}
