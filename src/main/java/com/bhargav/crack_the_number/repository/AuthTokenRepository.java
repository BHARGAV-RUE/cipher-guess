package com.bhargav.crack_the_number.repository;

import com.bhargav.crack_the_number.model.AuthToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
// Creates token for user and gives it to user who logged in and every time user requests for anything
// within the application the authintication checks if token exists or not
public interface AuthTokenRepository extends JpaRepository<AuthToken,String> {
        AuthToken findByToken(String token);
}
