package com.example.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.backend.entities.Question;
import com.example.backend.repository.QuestionRepository;
@Service
public class TestService {
   @Autowired
    private QuestionRepository questionRepository;

    // Méthode pour récupérer toutes les questions
    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }
}
