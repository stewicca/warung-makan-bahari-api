package com.enigma.wmb_api.repository;

import com.enigma.wmb_api.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, String> {
    Optional<UserAccount> findByUsername(String username);
    boolean existsByUsername(String username);
}
