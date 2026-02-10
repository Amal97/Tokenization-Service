package com.anztokenization.tokenizationservice.service;

import com.anztokenization.tokenizationservice.model.Token;
import com.anztokenization.tokenizationservice.repository.TokenRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.*;
import java.util.Base64;

@Service
public class TokenService {

    private final TokenRepository tokenRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    public TokenService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public List<String> generateToken(List<String> accountNumbers) {
        List<String> tokens = new ArrayList<>();

        for (String accountNumber : accountNumbers) {
            // Check if account is already tokenized
            Optional<Token> existing = tokenRepository.findByAccountNumber(accountNumber);
            if (existing.isPresent()) {
                tokens.add(existing.get().getToken());
                continue;
            }

            // Generate token and handle collision
            String token = null;
            boolean saved = false;
            int attempts = 0;
            while (!saved && attempts < 5) { // retry max 5 times
                token = generateRandomToken();
                try {
                    tokenRepository.save(new Token(accountNumber, token));
                    saved = true;
                } catch (DataIntegrityViolationException e) {
                    // token collision, retry
                    attempts++;
                }
            }

            if (!saved) {
                throw new RuntimeException("Failed to generate unique token after 5 attempts");
            }

            tokens.add(token);
        }

        return tokens;
    }

    public List<String> detokenize(List<String> tokens) {
        List<String> accountNumbers = new ArrayList<>();
        for (String token : tokens) {
            Optional<Token> existing = tokenRepository.findByToken(token);
            // if token is found, add account number to list
            if (existing.isPresent()) {
                accountNumbers.add(existing.get().getAccountNumber());
            } else {
                accountNumbers.add(null);
            }
        }
        return accountNumbers;
    }

    private String generateRandomToken() {
        byte[] bytes = new byte[24];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
