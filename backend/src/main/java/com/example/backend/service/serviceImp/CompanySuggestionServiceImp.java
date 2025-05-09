package com.example.backend.service.serviceImp;

import java.io.IOException;

import okhttp3.RequestBody;

import com.example.backend.service.servicesInterfaces.CompanySuggestionService;


import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CompanySuggestionServiceImp implements CompanySuggestionService {
    
    
    private final String apiKey = "AIzaSyD88dOHHEcCSMLSqRBBhGHjaO4gJdwRG2g";

    private final OkHttpClient client = new OkHttpClient();

    public String fetchCompanies(String jobTitle) throws IOException {
        String prompt = String.format("""
            List 10 companies hiring for the role of in Morocco
            Return the result as a JSON array.
            Each object should include:
            - id (an integer)
            - name
            - description
            - industry
            - location
            - size
            - foundedYear
            """, jobTitle);

        String jsonBody = "{ \"contents\": [{ \"parts\": [{ \"text\": " + org.json.JSONObject.quote(prompt) + " }] }] }";

    final Request request = new Request.Builder()
    .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=" + apiKey)
    .post(RequestBody.create(MediaType.get("application/json"), jsonBody))
    .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected response " + response);
            return response.body().string();
        }
    }
}
