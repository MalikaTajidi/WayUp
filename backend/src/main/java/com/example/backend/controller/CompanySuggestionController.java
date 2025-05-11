package com.example.backend.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.dto.CompanySuggestionDTO;
import com.example.backend.entities.User;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.servicesInterfaces.CompanySuggestionService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/")
public class CompanySuggestionController {
    
    private final UserRepository userRepository;
    private final CompanySuggestionService geminiService;
    private final ObjectMapper objectMapper;
    
    @Autowired
    private ApplicationContext context;
    
    public CompanySuggestionController(UserRepository userRepository, 
                                      CompanySuggestionService geminiService, 
                                      ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.geminiService = geminiService;
        this.objectMapper = objectMapper;
    }
    
    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/{id}/suggested-companies")
    public ResponseEntity<?> getSuggestedCompanies(@PathVariable int id) {
        Optional<User> optionalUser = userRepository.findById(id);
        
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        
        User user = optionalUser.get();
        String jobTitle = user.getMetierSugg(); // get job from DB
        
        if (jobTitle == null || jobTitle.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("User does not have a job title specified");
        }
        
        try {
            // Get raw JSON response from service
            String rawJson = geminiService.fetchCompanies(jobTitle);
            
            // Parse the response as a list of CompanySuggestionDTO objects
            List<CompanySuggestionDTO> companies = objectMapper.readValue(
                rawJson, 
                new TypeReference<List<CompanySuggestionDTO>>() {}
            );
            
            // Check if the first company has an error
            if (companies.size() > 0 && companies.get(0).getName().contains("Error")) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(companies.get(0));
            }
            
            return ResponseEntity.ok(companies);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to process API response: " + e.getMessage());
        }
    }
    
    // Method to test the API directly for debugging purposes
    @GetMapping("/test-companies-api/{jobTitle}")
    public ResponseEntity<?> testCompaniesApi(@PathVariable String jobTitle) {
        try {
            String rawJson = geminiService.fetchCompanies(jobTitle);
            return ResponseEntity.ok(rawJson);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("API test failed: " + e.getMessage());
        }
    }

    @GetMapping("/debug/list-models")
    public ResponseEntity<String> listModels() {
        String result = geminiService.listAvailableModels();
        return ResponseEntity.ok(result);
    }
}