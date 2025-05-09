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

    private final OkHttpClient client = new OkHttpClient.Builder()
        .build();

    public String fetchCompanies(String jobTitle){
        try{
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

        String jsonBody = "{ \"contents\": [{ \"parts\": [{ \"text\": " + org.json.JSONObject.quote(prompt) + " }] }] }";

               JSONObject requestJson = new JSONObject();
            JSONObject contentPart = new JSONObject();
            JSONObject part = new JSONObject();
            
            part.put("text", prompt);
            
            JSONObject[] partsArray = new JSONObject[1];
            partsArray[0] = part;
            
            contentPart.put("parts", partsArray);
            
            JSONObject[] contentsArray = new JSONObject[1];
            contentsArray[0] = contentPart;
            
            requestJson.put("contents", contentsArray);
            
            String requestBodyString = requestJson.toString();

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
                
                if (responseJson.has("candidates") && responseJson.getJSONArray("candidates").length() > 0) {
                    JSONObject candidate = responseJson.getJSONArray("candidates").getJSONObject(0);
                    if (candidate.has("content") && candidate.getJSONObject("content").has("parts") && 
                        candidate.getJSONObject("content").getJSONArray("parts").length() > 0) {
                        
                        String textContent = candidate.getJSONObject("content")
                                                     .getJSONArray("parts")
                                                     .getJSONObject(0)
                                                     .getString("text");
                        
                        // Check if the response starts with markdown code block indicators
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
                }
                
                return "Failed to extract companies data from response";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Error connecting to Gemini API: " + e.getMessage();
        } catch (JSONException e) {
            e.printStackTrace();
            return "Error parsing JSON: " + e.getMessage();
        }
    }
}

