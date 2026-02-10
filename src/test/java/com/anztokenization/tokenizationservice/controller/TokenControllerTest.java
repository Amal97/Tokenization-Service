package com.anztokenization.tokenizationservice.controller;

import com.anztokenization.tokenizationservice.service.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TokenControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private TokenController tokenController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(tokenController).build();
    }

    @Test
    void testTokenize_Success() throws Exception {
        when(tokenService.generateToken(anyList())).thenReturn(List.of("fvMymE7X0Je1IzMDgWooV5iGBPw0yoFy"));

        mockMvc.perform(post("/tokenize")
                .contentType(MediaType.APPLICATION_JSON)
                .content("[\"4111-1111-1111-1111\"]"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("fvMymE7X0Je1IzMDgWooV5iGBPw0yoFy"));
    }

    @Test
    void testTokenize_EmptyList() throws Exception {
        mockMvc.perform(post("/tokenize")
                .contentType(MediaType.APPLICATION_JSON)
                .content("[]"))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    @Test
    void testTokenize_InternalError() throws Exception {
        // We throw a RuntimeException which the controller should catch
        when(tokenService.generateToken(anyList())).thenThrow(new RuntimeException("Service failure"));

        mockMvc.perform(post("/tokenize")
                .contentType(MediaType.APPLICATION_JSON)
                .content("[\"4111-1111-1111-1111\"]"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("An error occurred: Service failure"));
    }

    @Test
    void testDetokenize_Success() throws Exception {
        when(tokenService.detokenize(anyList())).thenReturn(List.of("4111-1111-1111-1111"));

        mockMvc.perform(post("/detokenize")
                .contentType(MediaType.APPLICATION_JSON)
                .content("[\"fvMymE7X0Je1IzMDgWooV5iGBPw0yoFy\"]"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("4111-1111-1111-1111"));
    }

    @Test
    void testDetokenize_EmptyList() throws Exception {
        mockMvc.perform(post("/detokenize")
                .contentType(MediaType.APPLICATION_JSON)
                .content("[]"))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    @Test
    void testTokenize_NullList() throws Exception {
        mockMvc.perform(post("/tokenize")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDetokenize_InternalError() throws Exception {
        when(tokenService.detokenize(anyList())).thenThrow(new RuntimeException("Service failure"));

        mockMvc.perform(post("/detokenize")
                .contentType(MediaType.APPLICATION_JSON)
                .content("[\"token123\"]"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("An error occurred: Service failure"));
    }
}
