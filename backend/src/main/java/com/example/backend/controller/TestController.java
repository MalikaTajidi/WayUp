package com.example.backend.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.aspectj.weaver.ast.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.entities.Question;
import com.example.backend.service.TestService;

@RestController
@RequestMapping("/api")
public class TestController {
    @Autowired
    private TestService testService;

    // Endpoint pour récupérer toutes les questions
    @GetMapping("/questions")
    public ResponseEntity<List<String>> getAllQuestions() {
        // Récupérer toutes les questions depuis le service
        List<Question> questions = testService.getAllQuestions();

        // Extraire les textes des questions pour les renvoyer
        List<String> questionTexts = questions.stream()
                .map(Question::getText) // Supposons que `getText()` renvoie le texte de la question
                .collect(Collectors.toList());

        return ResponseEntity.ok(questionTexts);
    }
}
