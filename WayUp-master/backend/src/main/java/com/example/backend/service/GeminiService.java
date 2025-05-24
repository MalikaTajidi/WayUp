package com.example.backend.service;

import com.example.backend.entities.Internship;
import com.example.backend.service.serviceImp.InternshipSuggestionServiceImp;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class GeminiService {

    private final InternshipSuggestionServiceImp internshipSuggestionServiceImp;

    public GeminiService(InternshipSuggestionServiceImp internshipSuggestionServiceImp) {
        this.internshipSuggestionServiceImp = internshipSuggestionServiceImp;
    }

    public List<Internship> getInternshipsByJobTitle(String jobTitle) {
        // Appeler la méthode de InternshipSuggestionServiceImp pour récupérer les stages
        String internshipsJson = internshipSuggestionServiceImp.fetchInternships(jobTitle);
        
        // Convertir le JSON en objets Internship (Tu peux utiliser une librairie comme Jackson ou Gson pour cela)
        List<Internship> internships = convertJsonToInternships(internshipsJson);

        return internships;
    }

    private List<Internship> convertJsonToInternships(String internshipsJson) {
        // Utiliser une bibliothèque pour transformer le JSON en objets Internship
        // Par exemple avec Jackson : 
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(internshipsJson, new TypeReference<List<Internship>>() {});
        } catch (IOException e) {
            e.printStackTrace();
            return List.of(); // Retourner une liste vide en cas d'erreur
        }
    }
}
