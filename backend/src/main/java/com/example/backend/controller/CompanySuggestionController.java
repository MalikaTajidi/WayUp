package com.example.backend.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.dto.CompanySuggestionDTO;
import com.example.backend.entities.User;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.servicesInterfaces.CompanySuggestionService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;




@RestController
@RequestMapping("/api/")
public class CompanySuggestionController {

    private final UserRepository userRepository;
    private final CompanySuggestionService geminiService;
    private final ObjectMapper objectMapper;

 public CompanySuggestionController(UserRepository userRepository, CompanySuggestionService geminiService, ObjectMapper objectMapper){
         this.userRepository = userRepository;
        this.geminiService = geminiService;
        this.objectMapper = objectMapper;
 }

 @GetMapping("/{id}/suggested-companies")
    public ResponseEntity<?> getSuggestedCompanies(@PathVariable Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        User user = optionalUser.get();
        String jobTitle = user.getMetierSugg(); // get job from DB
       

        try {
            String rawJson = geminiService.fetchCompanies(jobTitle);
            JsonNode root = objectMapper.readTree(rawJson);
            String jsonArray = root.get("candidates").get(0).get("content").get("parts").get(0).get("text").asText();
            List<CompanySuggestionDTO> companies = objectMapper.readValue(jsonArray, new TypeReference<>() {});
            return ResponseEntity.ok(companies);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to process Gemini response");
        }
    }

    
}
