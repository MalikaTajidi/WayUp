package com.example.backend.service.serviceImp;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import okhttp3.RequestBody;

import com.example.backend.service.servicesInterfaces.InternshipSuggestionService;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Service
public class InternshipSuggestionServiceImp implements InternshipSuggestionService {

    private static final Logger LOGGER = Logger.getLogger(InternshipSuggestionServiceImp.class.getName());
    private static final MediaType JSON = MediaType.parse("application/json");

    private final String apiKey = "AIzaSyAYagjKdvJkclnOSUUCnZplzEk1C6u6R98"; // Remplacez par votre clé API Gemini

    private final OkHttpClient client = new OkHttpClient.Builder()
        .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .build();

    @Override
    public String fetchInternships(String jobTitle) {
        LOGGER.info("Fetching internships for job title: " + jobTitle);

        try {
            String prompt = String.format("""
                Liste 10 offres de stage disponibles pour le poste de %s au Maroc.
                Retourne la réponse dans ce format JSON exact :
                [
                  {
                    "id": 1,
                    "title": "Intitulé du stage",
                    "company": "Nom de l'entreprise",
                    "description": "Brève description du stage",
                    "location": "Ville, Maroc",
                    "duration": "Durée du stage",
                    "startDate": "Date de début (format YYYY-MM-DD)"
                  },
                  ... (9 autres stages)
                ]
                Retourne uniquement le tableau JSON sans texte supplémentaire.
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

              // Supprime les balises Markdown json ou  et autres débris
textContent = textContent
 .replaceAll("(?i)json", "")
 .replaceAll("", "")
 .trim();

// Supprime tout texte avant le premier [
int startIndex = textContent.indexOf("[");
if (startIndex != 0) {
 textContent = textContent.substring(startIndex);
}

// Supprime tout texte après le dernier ]
int endIndex = textContent.lastIndexOf("]");
if (endIndex != textContent.length() - 1) {
 textContent = textContent.substring(0, endIndex + 1);
}

LOGGER.info("Sanitized textContent:\n" + textContent);

                textContent = textContent.trim();
                if (textContent.isEmpty()) {
                    return createErrorResponse("Empty Content",
                        "The API returned empty content after processing");
                }

                try {
                     LOGGER.info("Text content received from Gemini:\n" + textContent);
                    JSONArray jsonArray = new JSONArray(textContent);
                    JSONArray cleanedArray = new JSONArray();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject internshipObj = jsonArray.getJSONObject(i);
                        JSONObject cleanedObj = new JSONObject();

                        cleanedObj.put("id", internshipObj.optInt("id", i + 1));

                        String[] stringFields = {"title", "company", "description", "location", "duration", "startDate"};
                        for (String field : stringFields) {
                            String value = internshipObj.optString(field, "N/A");
                            value = value.replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "")
                                       .replace("\n", " ")
                                       .replace("\r", "")
                                       .replace("\t", " ")
                                       .trim();
                            cleanedObj.put(field, value);
                        }

                        cleanedArray.put(cleanedObj);
                    }

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

    private String createErrorResponse(String errorType, String errorMessage) {
        try {
            JSONArray errorArray = new JSONArray();
            JSONObject errorObj = new JSONObject();

            errorObj.put("id", 1);
            errorObj.put("title", errorType);

            String safeErrorMessage = errorMessage != null ?
                errorMessage.replace("\"", "'") : "Unknown error";

            errorObj.put("company", "N/A");
            errorObj.put("description", safeErrorMessage);
            errorObj.put("location", "Casablanca, Morocco");
            errorObj.put("duration", "N/A");
            errorObj.put("startDate", "2025-01-01");

            errorArray.put(errorObj);
            return errorArray.toString();
        } catch (JSONException e) {
            LOGGER.log(Level.SEVERE, "Error creating error response", e);
            return "[{\"id\":1,\"title\":\"Critical Error\",\"company\":\"N/A\",\"description\":\"Error creating error response\",\"location\":\"Casablanca, Morocco\",\"duration\":\"N/A\",\"startDate\":\"2025-01-01\"}]";
        }
    }
}