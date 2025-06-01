package com.example.backend.controller;

import com.example.backend.dto.Internship;
import com.example.backend.service.serviceImp.InternshipSuggestionServiceImp;
import com.example.backend.service.servicesInterfaces.CompanySuggestionService;
import com.example.backend.entities.User;
import com.example.backend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/internships")
public class InternshipSuggestionController {

    private static final Logger LOGGER = Logger.getLogger(InternshipSuggestionController.class.getName());
    
    private final InternshipSuggestionServiceImp geminiService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
  //  private final CompanySuggestionService geminiService;

    // Constructor to inject dependencies
    public InternshipSuggestionController(InternshipSuggestionServiceImp internshipSuggestionService, UserRepository userRepository) {
        this.geminiService = internshipSuggestionService;
        this.userRepository = userRepository;
        this.objectMapper = new ObjectMapper();
    }

    // Endpoint to get internships for the currently authenticated user
    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/me")
    public ResponseEntity<?> getInternshipsForAuthenticatedUser() {
        try {
            // Get the currently authenticated user's email
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName(); // Username is stored as email in UserDetails
            
            LOGGER.info("Fetching internships for user with email: " + email);

            // Load the user from the database
            Optional<User> optionalUser = userRepository.findByEmail(email);
            if (optionalUser.isEmpty()) {
                LOGGER.warning("User not found with email: " + email);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("User not authenticated or not found");
            }

            User user = optionalUser.get();
            String jobTitle = user.getMetierSugg(); // Job title stored in the DB for the user
            
            if (jobTitle == null || jobTitle.trim().isEmpty()) {
                LOGGER.warning("Job title (metierSugg) is empty for user: " + email);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("User's job preference is not set. Please update your profile.");
            }
            
            LOGGER.info("Fetching internships for job title: " + jobTitle);

            // Call the service to get the internships based on the job title
            String internshipsJsonResponse = geminiService.fetchInternships(jobTitle);
            LOGGER.info("Received JSON response: " + internshipsJsonResponse);

            // Parse the JSON response into a List of Internship objects
            try {
                List<Internship> internships = objectMapper.readValue(
                    internshipsJsonResponse, 
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Internship.class)
                );
                
                if (internships.isEmpty()) {
                    LOGGER.warning("No internships found for job title: " + jobTitle);
                    return ResponseEntity.ok().body(List.of());
                }
                
                return ResponseEntity.ok(internships);
            } catch (IOException e) {
                LOGGER.severe("Error parsing JSON response: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error parsing internship data: " + e.getMessage());
            }
        } catch (Exception e) {
            LOGGER.severe("Unexpected error in getInternshipsForAuthenticatedUser: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected error: " + e.getMessage());
        }
    }
    
    // Endpoint to manually check internships for a specific job title (for testing)
  @CrossOrigin(origins = "http://localhost:4200")
  @GetMapping("/test-intership-api/{jobTitle}")
    public ResponseEntity<?> testIntershipApi(@PathVariable String jobTitle) {
        try {
            String rawJson = geminiService.fetchInternships(jobTitle);
            return ResponseEntity.ok(rawJson);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("API test failed: " + e.getMessage());
        }
    }
}