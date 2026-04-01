package com.bhargav.crack_the_number.repository;

import com.bhargav.crack_the_number.model.AuthToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthTokenRepository extends JpaRepository<AuthToken,String> {
        AuthToken findByToken(String token);
}
