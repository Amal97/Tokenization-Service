package com.anztokenization.tokenizationservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.anztokenization.tokenizationservice.model.Token;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByAccountNumber(String accountNumber);

    Optional<Token> findByToken(String token);

}
