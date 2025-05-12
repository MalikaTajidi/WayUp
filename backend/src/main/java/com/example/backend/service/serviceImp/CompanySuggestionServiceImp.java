package com.example.backend.service.serviceImp;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import okhttp3.RequestBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import com.example.backend.service.servicesInterfaces.CompanySuggestionService;

@Service
public class CompanySuggestionServiceImp implements CompanySuggestionService {

    private static final Logger LOGGER = Logger.getLogger(CompanySuggestionServiceImp.class.getName());
    private static final MediaType JSON = MediaType.parse("application/json");

    private final String apiKey = "AIzaSyBJxLlT3i-ZMe8BlhaDtVqnyQSX2zYz6ec";

    private final OkHttpClient client = new OkHttpClient.Builder()
        .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .build();

    public String fetchCompanies(String jobTitle) {
        LOGGER.info("Fetching companies for job title: " + jobTitle);

        try {
            String prompt = String.format("""
                List 10 companies hiring for the role of %s in Morocco.
                Return ONLY a JSON array in this exact format:
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
                  ...
                ]
                Do not return markdown or a single object. Return ONLY a pure JSON array.
                """, jobTitle);

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

            if (apiKey == null || apiKey.trim().isEmpty()) {
                LOGGER.severe("API key is missing! Check application properties configuration.");
                return createErrorResponse("Configuration Error", 
                    "API key is missing. Contact system administrator.");
            }

            Request request = new Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + apiKey)
                .post(RequestBody.create(JSON, requestBodyString))
                .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    String errorBody = response.body() != null ? response.body().string() : "No response body";
                    LOGGER.severe("API error: " + response.code() + " - " + errorBody);
                    return createErrorResponse("API Error", "Status code: " + response.code() + ". " + errorBody);
                }

                String responseBody = response.body() != null ? response.body().string() : "";
                LOGGER.fine("Raw API response: " + responseBody);

                if (responseBody.isEmpty()) {
                    return createErrorResponse("Empty Response", "The API returned an empty response");
                }

                JSONObject responseJson = new JSONObject(responseBody);

                if (!responseJson.has("candidates") || responseJson.getJSONArray("candidates").length() == 0) {
                    return createErrorResponse("Invalid Response Format", "Missing 'candidates' data");
                }

                JSONObject candidateObj = responseJson.getJSONArray("candidates").getJSONObject(0);

                if (!candidateObj.has("content") ||
                    !candidateObj.getJSONObject("content").has("parts") ||
                    candidateObj.getJSONObject("content").getJSONArray("parts").length() == 0) {

                    return createErrorResponse("Invalid Content Format", "Missing expected content structure");
                }

                String textContent = candidateObj.getJSONObject("content")
                                               .getJSONArray("parts")
                                               .getJSONObject(0)
                                               .getString("text")
                                               .trim();

                // Clean Markdown markers or unexpected tags
                textContent = textContent.replaceAll("(?i)```json|```", "").trim();

                // If it's not a JSON array, but a single object, wrap it
                if (textContent.startsWith("{") && textContent.endsWith("}")) {
                    LOGGER.warning("The content is a single JSON object instead of an array. Wrapping it into an array.");
                    textContent = "[" + textContent + "]";
                }

                // Parse to JSONArray
                JSONArray jsonArray = new JSONArray(textContent);
                JSONArray cleanedArray = new JSONArray();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject companyObj = jsonArray.getJSONObject(i);
                    JSONObject cleanedObj = new JSONObject();

                    cleanedObj.put("id", companyObj.optInt("id", i + 1));

                    String[] fields = {"name", "description", "industry", "location", "size"};
                    for (String field : fields) {
                        String value = companyObj.optString(field, "N/A")
                            .replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "")
                            .replace("\n", " ")
                            .replace("\r", "")
                            .replace("\t", " ")
                            .trim();
                        cleanedObj.put(field, value);
                    }

                    int foundedYear;
                    try {
                        foundedYear = companyObj.getInt("foundedYear");
                    } catch (Exception e) {
                        foundedYear = 2020;
                    }
                    cleanedObj.put("foundedYear", foundedYear);

                    cleanedArray.put(cleanedObj);
                }

                return cleanedArray.toString();

            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "IO Exception", e);
            return createErrorResponse("API Connection Error", e.getMessage());
        } catch (JSONException e) {
            LOGGER.log(Level.SEVERE, "JSON Exception", e);
            return createErrorResponse("JSON Processing Error", e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error", e);
            return createErrorResponse("Unexpected Error", e.getMessage());
        }
    }

    private String createErrorResponse(String errorType, String errorMessage) {
        try {
            JSONArray errorArray = new JSONArray();
            JSONObject errorObj = new JSONObject();

            errorObj.put("id", 1);
            errorObj.put("name", errorType);
            errorObj.put("description", errorMessage != null ? errorMessage.replace("\"", "'") : "Unknown error");
            errorObj.put("industry", "Technology");
            errorObj.put("location", "Casablanca, Morocco");
            errorObj.put("size", "N/A");
            errorObj.put("foundedYear", 2020);

            errorArray.put(errorObj);
            return errorArray.toString();
        } catch (JSONException e) {
            LOGGER.log(Level.SEVERE, "Error creating error response", e);
            return "[{\"id\":1,\"name\":\"Critical Error\",\"description\":\"Error creating error response\",\"industry\":\"Technology\",\"location\":\"Casablanca, Morocco\",\"size\":\"N/A\",\"foundedYear\":2020}]";
        }
    }

    public String listAvailableModels() {
        LOGGER.info("Listing available Gemini models");
        try {
            if (apiKey == null || apiKey.trim().isEmpty()) {
                return createErrorResponse("Configuration Error", "API key is missing.");
            }

            Request request = new Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1/models?key=" + apiKey)
                .get()
                .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    String errorBody = response.body() != null ? response.body().string() : "No response body";
                    return createErrorResponse("Error Listing Models", "Status code: " + response.code() + ". " + errorBody);
                }

                return response.body().string();
            }
        } catch (IOException e) {
            return createErrorResponse("Error Listing Models", e.getMessage());
        } catch (Exception e) {
            return createErrorResponse("Unexpected Error", e.getMessage());
        }
    }
}
