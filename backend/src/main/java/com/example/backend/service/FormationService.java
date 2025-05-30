package com.example.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.backend.entities.Formation;
import com.example.backend.repository.FormationRepository;

@Service
public class FormationService {

    @Autowired
    private FormationRepository formationRepository;

    public void saveFormation(Formation formation) {
        formationRepository.save(formation);
    }

    public List<Formation> getFormationsByUserId(int userId) {
        return formationRepository.findByUserId(userId);
    }
}
