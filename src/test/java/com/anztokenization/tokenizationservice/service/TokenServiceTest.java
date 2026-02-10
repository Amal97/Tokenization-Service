package com.anztokenization.tokenizationservice.service;

import com.anztokenization.tokenizationservice.model.Token;
import com.anztokenization.tokenizationservice.repository.TokenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @Mock
    private TokenRepository tokenRepository;

    @InjectMocks
    private TokenService tokenService;

    private String accountNumber = "4111-1111-1111-1111";
    private String tokenValue = "fvMymE7X0Je1IzMDgWooV5iGBPw0yoFy";

    @Test
    void testGenerateToken_Success() {
        // Arrange
        when(tokenRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.empty());
        when(tokenRepository.save(any(Token.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        List<String> result = tokenService.generateToken(List.of(accountNumber));

        // Assert
        assertEquals(1, result.size());
        assertNotNull(result.get(0));
        verify(tokenRepository, times(1)).save(any(Token.class));
    }

    @Test
    void testGenerateToken_ExistingToken() {
        // Arrange
        Token existingToken = new Token(accountNumber, tokenValue);
        when(tokenRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(existingToken));

        // Act
        List<String> result = tokenService.generateToken(List.of(accountNumber));

        // Assert
        assertEquals(1, result.size());
        assertEquals(tokenValue, result.get(0));
        verify(tokenRepository, never()).save(any(Token.class));
    }

    @Test
    void testGenerateToken_HandleCollision() {
        // Arrange
        when(tokenRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.empty());

        // Throw collision exception once, then succeed
        when(tokenRepository.save(any(Token.class)))
                .thenThrow(new DataIntegrityViolationException("Collision"))
                .thenAnswer(i -> i.getArguments()[0]);

        // Act
        List<String> result = tokenService.generateToken(List.of(accountNumber));

        // Assert
        assertEquals(1, result.size());
        verify(tokenRepository, times(2)).save(any(Token.class));
    }

    @Test
    void testGenerateToken_MaxRetriesReached() {
        // Arrange
        when(tokenRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.empty());
        when(tokenRepository.save(any(Token.class))).thenThrow(new DataIntegrityViolationException("Collision"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            tokenService.generateToken(List.of(accountNumber));
        });

        assertTrue(exception.getMessage().contains("Failed to generate unique token"));
        verify(tokenRepository, times(5)).save(any(Token.class));
    }

    @Test
    void testDetokenize_Success() {
        // Arrange
        Token token = new Token(accountNumber, tokenValue);
        when(tokenRepository.findByToken(tokenValue)).thenReturn(Optional.of(token));

        // Act
        List<String> result = tokenService.detokenize(List.of(tokenValue));

        // Assert
        assertEquals(1, result.size());
        assertEquals(accountNumber, result.get(0));
    }

    @Test
    void testDetokenize_NotFound() {
        // Arrange
        when(tokenRepository.findByToken("unknown")).thenReturn(Optional.empty());

        // Act
        List<String> result = tokenService.detokenize(List.of("unknown"));

        // Assert
        assertEquals(1, result.size());
        assertNull(result.get(0));
    }
}
