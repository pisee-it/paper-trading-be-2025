package com.trade.PiSeeTrading.repository;

import com.trade.PiSeeTrading.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    // For Login
    Optional<User> findByUsername(String username);

    // For Register
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
}
