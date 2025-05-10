package com.example.backend.service;

import java.util.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class MetierService {

    private final RestTemplate restTemplate;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    public MetierService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public JsonNode getGeminiResult(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> part = new HashMap<>();
        part.put("text", prompt);

        Map<String, Object> content = new HashMap<>();
        content.put("parts", Collections.singletonList(part));

        Map<String, Object> body = new HashMap<>();
        body.put("contents", Collections.singletonList(content));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ObjectMapper mapper = new ObjectMapper();

            ResponseEntity<String> response = restTemplate.postForEntity(
                "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + geminiApiKey,
                request,
                String.class
            );

            if (response.getStatusCode() != HttpStatus.OK) {
                throw new RuntimeException("Erreur API Gemini: " + response.getStatusCode());
            }

            return mapper.readTree(response.getBody());

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'appel ou du parsing de l'API Gemini");
        }
    }
}
