package com.example.backend.controller;

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

import jakarta.transaction.Transactional;

import java.util.*;


import com.example.backend.entities.Skill;
import com.example.backend.repository.SkillRepository;


@RestController
@RequestMapping("/api")
public class MetierController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private MetierService metierService;

    @Transactional
    @PostMapping("/resultat/{userId}")
    public ResponseEntity<String> generateCareer(
            @PathVariable int userId,
            @RequestBody TestRequest testRequest) {
    
        String prompt = buildPrompt(testRequest.getQuestions(), testRequest.getAnswers());
        System.out.println("Prompt envoyé :\n" + prompt);
    
        JsonNode resultJson = metierService.getGeminiResult(prompt);
    
        String metierSugg = extractMetierSugg(resultJson);
        System.out.println("metierSugg :\n" + metierSugg);
    
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Utilisateur introuvable.");
        }
    
        User user = userOpt.get();
    
        skillRepository.deleteByUser(user);
    
        user.setMetierSugg(metierSugg);
        userRepository.save(user);
    
        String skillsPrompt = "Liste les 10 compétences principales nécessaires pour devenir un " + metierSugg + ". Réponds uniquement avec une liste simple, une compétence par ligne.";
        JsonNode skillsJson = metierService.getGeminiResult(skillsPrompt);
    
        List<Skill> skills = extractSkillsFromJson(skillsJson, user);
        skillRepository.saveAll(skills);
        user.setTestDone(true);
        return ResponseEntity.ok("Nouveau métier : " + metierSugg + " avec compétences mises à jour.");
    }
    


    private String extractMetierSugg(JsonNode resultJson) {
        JsonNode content = resultJson.path("candidates").get(0).path("content").path("parts").get(0).path("text");
        String text = content.asText();

        Pattern pattern = Pattern.compile("\\*\\*([^*]+)\\*\\*");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return text.split("\n")[0];
    }

   

private List<Skill> extractSkillsFromJson(JsonNode json, User user) {
    List<Skill> skills = new ArrayList<>();
    String text = json.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();

    for (String line : text.split("\n")) {
        String cleaned = line.replaceAll("^[0-9\\-\\*\\.\\s]+", "").trim();
        if (!cleaned.isEmpty()) {
            Skill skill = new Skill(null, cleaned, false, user);
            skills.add(skill);
        }
    }

    return skills;
}


    private String buildPrompt(List<String> questions, List<String> answers) {
        StringBuilder prompt = new StringBuilder("Based on the following answers to a career aptitude test, suggest only one profession for the user. Return only the name of the profession clearly marked in **bold**:\n");
        for (int i = 0; i < questions.size(); i++) {
            prompt.append(questions.get(i)).append(" => ").append(answers.get(i)).append("\n");
        }
        return prompt.toString();
    }
}
