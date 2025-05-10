package com.example.backend.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.example.backend.dto.TestRequest;
import com.example.backend.entities.User;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.MetierService;
import com.fasterxml.jackson.databind.JsonNode;

import io.jsonwebtoken.io.IOException;

@RestController
@RequestMapping("/api")
public class MetierController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MetierService metierService;

    @PostMapping("/resultat/{userId}")
    public ResponseEntity<String> generateCareer(
            @PathVariable int userId,
            @RequestBody TestRequest testRequest) {

        String prompt = buildPrompt(testRequest.getQuestions(), testRequest.getAnswers());
        System.out.println("Prompt envoyé :\n" + prompt);

        JsonNode resultJson = metierService.getGeminiResult(prompt);

        String metierSugg = extractMetierSugg(resultJson);
        System.out.println("metierSugg :\n" + metierSugg);

        Optional<User> user = userRepository.findById(userId);
        user.ifPresent(u -> {
            u.setMetierSugg(metierSugg);
            userRepository.save(u);
        });

        return ResponseEntity.ok(metierSugg);
    }

    private String extractMetierSugg(JsonNode resultJson) {
        JsonNode candidates = resultJson.path("candidates");
        if (candidates.isArray() && candidates.size() > 0) {
            JsonNode content = candidates.get(0).path("content").path("parts").get(0).path("text");
            String text = content.asText();

            // Recherche du premier mot entre ** (Markdown bold)
            Pattern pattern = Pattern.compile("\\*\\*([^*]+)\\*\\*");
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                return matcher.group(1).trim(); // Retourne "Counselor", etc.
            }

            // Sinon, essayer d'extraire le premier mot "simple"
            String[] lines = text.split("\n");
            if (lines.length > 0) {
                return lines[0].split(" ")[0];
            }
        }

        return "Aucune suggestion de métier trouvée.";
    }

    private String buildPrompt(List<String> questions, List<String> answers) {
        StringBuilder prompt = new StringBuilder("Based on the following answers to a career aptitude test, suggest only one profession for the user. Return only the name of the profession clearly marked in **bold** at the start:\n");

        for (int i = 0; i < questions.size(); i++) {
            prompt.append(questions.get(i)).append(" => ").append(answers.get(i)).append("\n");
        }

        return prompt.toString();
    }
}
