package com.example.backend.service.serviceImp;

import java.io.IOException;

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
    
    private final String apiKey = "AIzaSyD88dOHHEcCSMLSqRBBhGHjaO4gJdwRG2g";
    private final OkHttpClient client = new OkHttpClient.Builder().build();

    public String fetchCompanies(String jobTitle) {
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

            // Use the correct endpoint for Gemini Pro
            final Request request = new Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=" + apiKey)
                .post(RequestBody.create(MediaType.parse("application/json"), requestBodyString))
                .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    String errorBody = response.body() != null ? response.body().string() : "No response body";
                    throw new IOException("Unexpected response: " + response.code() + " - " + errorBody);
                }
                
                String responseBody = response.body().string();
                JSONObject responseJson = new JSONObject(responseBody);
                
                // Parse based on Gemini Pro response format
                if (responseJson.has("candidates") && responseJson.getJSONArray("candidates").length() > 0) {
                    JSONObject candidateObj = responseJson.getJSONArray("candidates").getJSONObject(0);
                    
                    if (candidateObj.has("content") && 
                        candidateObj.getJSONObject("content").has("parts") && 
                        candidateObj.getJSONObject("content").getJSONArray("parts").length() > 0) {
                        
                        String textContent = candidateObj.getJSONObject("content")
                                                       .getJSONArray("parts")
                                                       .getJSONObject(0)
                                                       .getString("text");
                        
                        // Clean up potential markdown formatting
                        if (textContent.contains("```json")) {
                            textContent = textContent.replaceAll("```json", "");
                        }
                        if (textContent.contains("```")) {
                            textContent = textContent.replaceAll("```", "");
                        }
                        
                        // Trim any whitespace
                        textContent = textContent.trim();
                        
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
                            je.printStackTrace();
                            return "[{\"id\":1,\"name\":\"Error parsing AI response\",\"description\":\"The AI returned an improperly formatted response. Please try again.\",\"industry\":\"Technology\",\"location\":\"Casablanca, Morocco\",\"size\":\"N/A\",\"foundedYear\":2020}]";
                        }
                    }
                }
                
                return "[{\"id\":1,\"name\":\"Error processing API response\",\"description\":\"Could not extract company data from API response.\",\"industry\":\"Technology\",\"location\":\"Casablanca, Morocco\",\"size\":\"N/A\",\"foundedYear\":2020}]";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "[{\"id\":1,\"name\":\"API Connection Error\",\"description\":\"" + e.getMessage().replace("\"", "'") + "\",\"industry\":\"Technology\",\"location\":\"Casablanca, Morocco\",\"size\":\"N/A\",\"foundedYear\":2020}]";
        } catch (JSONException e) {
            e.printStackTrace();
            return "[{\"id\":1,\"name\":\"JSON Parsing Error\",\"description\":\"" + e.getMessage().replace("\"", "'") + "\",\"industry\":\"Technology\",\"location\":\"Casablanca, Morocco\",\"size\":\"N/A\",\"foundedYear\":2020}]";
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
            return "[{\"id\":1,\"name\":\"Error Listing Models\",\"description\":\"" + e.getMessage().replace("\"", "'") + "\",\"industry\":\"Technology\",\"location\":\"Casablanca, Morocco\",\"size\":\"N/A\",\"foundedYear\":2020}]";
        }
    }
}