package com.anztokenization.tokenizationservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.anztokenization.tokenizationservice.service.TokenService;

import java.util.Collections;
import java.util.List;

@RestController
public class TokenController {

    private final TokenService tokenService;

    public TokenController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @PostMapping("/tokenize")
    public List<String> generateTokens(@RequestBody List<String> accountNumbers) {
        // if accountNumbers is null or empty, return empty list
        if (accountNumbers == null || accountNumbers.isEmpty()) {
            return Collections.emptyList();
        }
        // generate tokens for each account number
        return tokenService.generateToken(accountNumbers);
    }

    @PostMapping("/detokenize")
    public List<String> detokenizeTokens(@RequestBody List<String> tokens) {
        // if tokens is null or empty, return empty list
        if (tokens == null || tokens.isEmpty()) {
            return Collections.emptyList();
        }
        // detokenize tokens for each token
        return tokenService.detokenize(tokens);
    }

    // Exception handler for this controller
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An error occurred: " + ex.getMessage());
    }

}
