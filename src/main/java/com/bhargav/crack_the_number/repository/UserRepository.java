package com.bhargav.crack_the_number.repository;

import com.bhargav.crack_the_number.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
// During login it checks the password for the given username
// and during register it checks if the given email already exists in database
public interface UserRepository extends JpaRepository<User,Integer> {
    User findUserByUsername(String username);
    User findByEmail(String email);
}
