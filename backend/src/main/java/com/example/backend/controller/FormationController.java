package com.example.backend.controller;

import com.example.backend.entities.Formation;
import com.example.backend.entities.User;
import com.example.backend.service.FormationService;
import com.example.backend.service.serviceImp.UserServiceImp;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api")
public class FormationController {

    @Value("${gemini.api.key}")  // Clé API de Gemini
    private String apiKey;

    @Autowired
    private FormationService formationService;

    @Autowired
    private UserServiceImp userService;

    @GetMapping("/getUserFormations")
    public ResponseEntity<Object> getUserFormations(@RequestHeader("userId") int userId) {
    try {
        // Récupérer l'utilisateur par son ID
        User user = userService.getUserById(userId);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utilisateur non trouvé.");
        }

        // Récupérer les formations associées à cet utilisateur
        List<Formation> formations = formationService.getFormationsByUserId(userId);

        if (formations.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Aucune formation trouvée pour cet utilisateur.");
        }

        return ResponseEntity.ok(formations);
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la récupération des formations : " + e.getMessage());
    }
}

}
