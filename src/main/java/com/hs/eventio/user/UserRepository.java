package com.hs.eventio.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findUserByEmail(String email);
    default User findUserById(UUID userId){
        return findById(userId).orElseThrow();
    }
}