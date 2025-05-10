package com.example.backend.service.servicesInterfaces;

import java.io.IOException;

public interface CompanySuggestionService {

    String fetchCompanies(String jobTitle) throws IOException;

    String listAvailableModels();
    
}
