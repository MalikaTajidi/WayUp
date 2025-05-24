package com.example.backend.service.serviceImp;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.example.backend.service.servicesInterfaces.InternshipSuggestionService;

import okhttp3.*;

@Service
public class InternshipSuggestionServiceImp implements InternshipSuggestionService {
    private static final Logger LOGGER = Logger.getLogger(InternshipSuggestionServiceImp.class.getName());
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final OkHttpClient client = new OkHttpClient();

    @Override
    public String fetchInternships(String jobTitle) {
        LOGGER.info("🔍 Requête de stages pour : " + jobTitle);

        try {
            // Préparer le prompt
            String prompt = """
                Liste 10 stages réels pour %s au Maroc.
                Entreprises: OCP, INWI, Deloitte, CGI.
                Format JSON strict. Exemple:
                [ {
                  "id": 1,
                  "title": "Stage en %s",
                  "company": "OCP",
                  "location": "Casablanca",
                  "duration": "3 mois",
                  "description": "...",
                  "requirements": "..."
                } ]
                """.formatted(jobTitle, jobTitle);

            // Créer le corps de la requête
            JSONObject requestJson = new JSONObject()
                .put("contents", new JSONArray().put(
                    new JSONObject()
                        .put("role", "user")
                        .put("parts", new JSONArray().put(
                            new JSONObject().put("text", prompt)
                        ))
                ))
                .put("generationConfig", new JSONObject()
                    .put("temperature", 0.3)
                    .put("maxOutputTokens", 1024)
                );

            Request request = new Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=AIzaSyCcZ-f_UcPEJ_X6jxwAeVZSvFDQsW4xJ6g")
                .post(RequestBody.create(requestJson.toString(), JSON))
                .build();

            try (Response response = client.newCall(request).execute()) {
                String rawResponse = response.body().string();

                LOGGER.info("✅ Réponse brute de Gemini : \n" + rawResponse);

                if (!response.isSuccessful()) {
                    LOGGER.severe("❌ Erreur Gemini : " + response.code());
                    return "[]";
                }

                JSONObject json = new JSONObject(rawResponse);
                JSONArray candidates = json.optJSONArray("candidates");

                if (candidates == null || candidates.length() == 0) {
                    LOGGER.warning("⚠️ Aucun candidat généré.");
                    return "[]";
                }

                String text = candidates
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text");

                LOGGER.info("🧪 Texte reçu de Gemini :\n" + text);

                // 🔧 Extraire uniquement le tableau JSON
                int startIndex = text.indexOf('[');
                int endIndex = text.lastIndexOf(']') + 1;

                if (startIndex == -1 || endIndex == -1 || endIndex <= startIndex) {
                    LOGGER.warning("⚠️ Impossible d'extraire le tableau JSON.");
                    return "[]";
                }

                String jsonArrayString = text.substring(startIndex, endIndex);

                JSONArray result = new JSONArray(jsonArrayString);
                return result.toString(2); // formaté pour lisibilité
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "❌ Exception lors de l'appel à Gemini", e);
            return "[]";
        }
    }
}
