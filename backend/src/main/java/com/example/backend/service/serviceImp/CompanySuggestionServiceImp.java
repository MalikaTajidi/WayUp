package com.example.backend.service.serviceImp;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import okhttp3.RequestBody;

import com.example.backend.service.servicesInterfaces.CompanySuggestionService;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Service
public class CompanySuggestionServiceImp implements CompanySuggestionService {
    
    private static final Logger LOGGER = Logger.getLogger(CompanySuggestionServiceImp.class.getName());
    private static final MediaType JSON = MediaType.parse("application/json");
    
    // API Key - should ideally be in application properties
    private final String apiKey = "AIzaSyAYagjKdvJkclnOSUUCnZplzEk1C6u6R98";
    
    private final OkHttpClient client = new OkHttpClient.Builder()
        .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .build();

    public String fetchCompanies(String jobTitle) {
        LOGGER.info("Fetching companies for job title: " + jobTitle);
        
        try {
            // Using current Gemini Pro model endpoint (as of May 2025)
            String prompt = String.format("""
                List 10 companies hiring for the role of %s in Morocco.
                Return the response in this exact JSON format:
                [
                  {
                    "id": 1,
                    "name": "Company Name",
                    "description": "Brief company description",
                    "industry": "Industry sector",
                    "location": "City, Morocco",
                    "size": "Number of employees range",
                    "foundedYear": 2020
                  },
                  ... (9 more companies)
                ]
                Only return the JSON array without any additional text or explanations.
                """, jobTitle);

            // Create the JSON request for Gemini Pro model
            JSONObject contentsObj = new JSONObject();
            contentsObj.put("role", "user");
            contentsObj.put("parts", new JSONArray().put(new JSONObject().put("text", prompt)));
            
            JSONArray contents = new JSONArray().put(contentsObj);
            
            JSONObject requestJson = new JSONObject();
            requestJson.put("contents", contents);
            requestJson.put("generationConfig", new JSONObject()
                .put("temperature", 0.2)
                .put("maxOutputTokens", 1024));
            
            String requestBodyString = requestJson.toString();
            LOGGER.fine("Request body: " + requestBodyString);

            // Check if API key is properly injected
            if (apiKey == null || apiKey.trim().isEmpty()) {
                LOGGER.severe("API key is missing! Check application properties configuration.");
                return createErrorResponse("Configuration Error", 
                    "API key is missing. Contact system administrator.");
            }

            // Use the correct endpoint for Gemini 1.5 Pro (current as of May 2025)
            final Request request = new Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + apiKey)
                .post(RequestBody.create(JSON, requestBodyString))
                .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    String errorBody = response.body() != null ? response.body().string() : "No response body";
                    LOGGER.severe("API error: " + response.code() + " - " + errorBody);
                    return createErrorResponse("API Error", 
                        "Status code: " + response.code() + ". " + errorBody);
                }
                
                String responseBody = response.body() != null ? response.body().string() : "";
                LOGGER.fine("Raw API response: " + responseBody);
                
                if (responseBody.isEmpty()) {
                    return createErrorResponse("Empty Response", 
                        "The API returned an empty response");
                }
                
                JSONObject responseJson = new JSONObject(responseBody);
                
                // Parse based on Gemini Pro response format
                if (!responseJson.has("candidates") || responseJson.getJSONArray("candidates").length() == 0) {
                    LOGGER.warning("No candidates in response: " + responseBody);
                    return createErrorResponse("Invalid Response Format", 
                        "The API response did not contain expected 'candidates' data");
                }
                
                JSONObject candidateObj = responseJson.getJSONArray("candidates").getJSONObject(0);
                
                if (!candidateObj.has("content") || 
                    !candidateObj.getJSONObject("content").has("parts") || 
                    candidateObj.getJSONObject("content").getJSONArray("parts").length() == 0) {
                    
                    LOGGER.warning("Missing content in response: " + responseBody);
                    return createErrorResponse("Invalid Content Format", 
                        "The API response did not contain expected content data");
                }
                
                String textContent = candidateObj.getJSONObject("content")
                                               .getJSONArray("parts")
                                               .getJSONObject(0)
                                               .getString("text");
                
                // Clean up potential markdown formatting
                if (textContent.contains("json")) {
                    textContent = textContent.replaceAll("json", "");
                }
                if (textContent.contains("")) {
                    textContent = textContent.replaceAll("", "");
                }
                
                // Trim any whitespace and check if it's empty
                textContent = textContent.trim();
                if (textContent.isEmpty()) {
                    return createErrorResponse("Empty Content", 
                        "The API returned empty content after processing");
                }
                
                // Validate and properly format the JSON
                try {
                    // Parse the JSON to ensure it's valid
                    JSONArray jsonArray = new JSONArray(textContent);
                    
                    // Clean and reformat each object to ensure proper escaping
                    JSONArray cleanedArray = new JSONArray();
                    
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject companyObj = jsonArray.getJSONObject(i);
                        JSONObject cleanedObj = new JSONObject();
                        
                        // Process each field with proper escaping
                        cleanedObj.put("id", companyObj.optInt("id", i+1));
                        
                        // Process string fields, ensuring they're properly escaped
                        String[] stringFields = {"name", "description", "industry", "location", "size"};
                        for (String field : stringFields) {
                            String value = companyObj.optString(field, "N/A");
                            // Remove any control characters that could break JSON
                            value = value.replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "")
                                       .replace("\n", " ")
                                       .replace("\r", "")
                                       .replace("\t", " ")
                                       .trim();
                            cleanedObj.put(field, value);
                        }
                        
                        // Process foundedYear ensuring it's an integer
                        int foundedYear;
                        try {
                            foundedYear = companyObj.getInt("foundedYear");
                        } catch (Exception e) {
                            foundedYear = 2020; // Default year if invalid
                        }
                        cleanedObj.put("foundedYear", foundedYear);
                        
                        cleanedArray.put(cleanedObj);
                    }
                    
                    // Convert back to string with proper escaping
                    return cleanedArray.toString();
                } catch (JSONException je) {
                    LOGGER.log(Level.SEVERE, "JSON parsing error", je);
                    return createErrorResponse("JSON Parsing Error", 
                        "The AI returned an improperly formatted response: " + je.getMessage());
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "IO Exception", e);
            return createErrorResponse("API Connection Error", e.getMessage());
        } catch (JSONException e) {
            LOGGER.log(Level.SEVERE, "JSON Exception", e);
            return createErrorResponse("JSON Processing Error", e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error", e);
            return createErrorResponse("Unexpected Error", 
                "An unexpected error occurred: " + e.getMessage());
        }
    }
    
    // Helper method to create error responses
    private String createErrorResponse(String errorType, String errorMessage) {
        try {
            JSONArray errorArray = new JSONArray();
            JSONObject errorObj = new JSONObject();
            
            errorObj.put("id", 1);
            errorObj.put("name", errorType);
            
            // Make sure error message is not null
            String safeErrorMessage = errorMessage != null ? 
                errorMessage.replace("\"", "'") : "Unknown error";
                
            errorObj.put("description", safeErrorMessage);
            errorObj.put("industry", "Technology");
            errorObj.put("location", "Casablanca, Morocco");
            errorObj.put("size", "N/A");
            errorObj.put("foundedYear", 2020);
            
            errorArray.put(errorObj);
            return errorArray.toString();
        } catch (JSONException e) {
            // If even creating the error JSON fails, return a hardcoded error
            LOGGER.log(Level.SEVERE, "Error creating error response", e);
            return "[{\"id\":1,\"name\":\"Critical Error\",\"description\":\"Error creating error response\",\"industry\":\"Technology\",\"location\":\"Casablanca, Morocco\",\"size\":\"N/A\",\"foundedYear\":2020}]";
        }
    }
    
    // Method to list available models for debugging purposes
    public String listAvailableModels() {
        LOGGER.info("Listing available Gemini models");
        try {
            // Check if API key is properly injected
            if (apiKey == null || apiKey.trim().isEmpty()) {
                LOGGER.severe("API key is missing for model listing! Check application properties configuration.");
                return createErrorResponse("Configuration Error", 
                    "API key is missing. Contact system administrator.");
            }
            
            // Updated to use v1 endpoint instead of v1beta
            final Request request = new Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1/models?key=" + apiKey)
                .get()
                .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    String errorBody = response.body() != null ? response.body().string() : "No response body";
                    LOGGER.severe("Error listing models: " + response.code() + " - " + errorBody);
                    return createErrorResponse("Error Listing Models", 
                        "Status code: " + response.code() + ". " + errorBody);
                }
                
                String responseBody = response.body().string();
                return responseBody;
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "IO Exception listing models", e);
            return createErrorResponse("Error Listing Models", e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error listing models", e);
            return createErrorResponse("Unexpected Error", 
                "An unexpected error occurred while listing models: " + e.getMessage());
        }
    }
}