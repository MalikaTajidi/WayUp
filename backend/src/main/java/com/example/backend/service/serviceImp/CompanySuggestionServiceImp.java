package com.example.backend.service.serviceImp;

import java.io.IOException;

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
    
    private final String apiKey = "AIzaSyD88dOHHEcCSMLSqRBBhGHjaO4gJdwRG2g";
    private final OkHttpClient client = new OkHttpClient.Builder().build();

    public String fetchCompanies(String jobTitle) {
        try {
            // Based on your model list, we'll use text-bison-001 which supports generateText
            String prompt = String.format("""
                List 10 companies hiring for the role of %s in Morocco.
                Return the response in this exact JSON format:
                [
                  {
                    "id": 1,
                    "name": "Company Name",
                    "description": "Brief description",
                    "industry": "Industry sector",
                    "location": "City, Morocco",
                    "size": "Number of employees range",
                    "foundedYear": "Year founded"
                  },
                  ... (9 more companies)
                ]
                Only return the JSON array without any additional text or explanations.
                """, jobTitle);

            // Create the JSON request for text-bison-001 model
            JSONObject requestJson = new JSONObject();
            requestJson.put("prompt", prompt);
            requestJson.put("temperature", 0.2);  // Lower temperature for more consistent, factual responses
            requestJson.put("maxOutputTokens", 1024);
            
            String requestBodyString = requestJson.toString();

            // Use the correct endpoint for text-bison-001
            final Request request = new Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1/models/text-bison-001:generateText?key=" + apiKey)
                .post(RequestBody.create(MediaType.parse("application/json"), requestBodyString))
                .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    String errorBody = response.body() != null ? response.body().string() : "No response body";
                    throw new IOException("Unexpected response: " + response.code() + " - " + errorBody);
                }
                
                String responseBody = response.body().string();
                JSONObject responseJson = new JSONObject(responseBody);
                
                // Parse based on text-bison-001 response format
                if (responseJson.has("candidates") && responseJson.getJSONArray("candidates").length() > 0) {
                    String textContent = responseJson.getJSONArray("candidates")
                                                 .getJSONObject(0)
                                                 .getString("output");
                    
                    // Clean up potential markdown formatting
                    if (textContent.startsWith("```json")) {
                        textContent = textContent.substring(7); // Remove ```json
                    }
                    if (textContent.endsWith("```")) {
                        textContent = textContent.substring(0, textContent.length() - 3); // Remove ```
                    }
                    
                    // Trim any whitespace
                    textContent = textContent.trim();
                    
                    return textContent;
                }
                
                return "Failed to extract companies data from response";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Error connecting to API: " + e.getMessage();
        } catch (JSONException e) {
            e.printStackTrace();
            return "Error parsing JSON: " + e.getMessage();
        }
    }
    
    // Method to list available models for debugging purposes
    public String listAvailableModels() {
        try {
            final Request request = new Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models?key=" + apiKey)
                .get()
                .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    String errorBody = response.body() != null ? response.body().string() : "No response body";
                    throw new IOException("Unexpected response: " + response.code() + " - " + errorBody);
                }
                
                String responseBody = response.body().string();
                return responseBody;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Error listing models: " + e.getMessage();
        }
    }
}