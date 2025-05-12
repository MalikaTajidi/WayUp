package com.example.backend.controller;

import com.example.backend.entities.Formation;
import com.example.backend.entities.User;
import com.example.backend.service.FormationService;
import com.example.backend.service.serviceImp.UserServiceImp;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

@PostMapping("/getFormations")
public ResponseEntity<Object> getFormations(@RequestBody String metier, @RequestHeader("userId") int userId) {
    try {
        // Récupérer l'utilisateur par son ID unique
        User user = userService.getUserById(userId);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utilisateur non trouvé.");
        }

        // Construire la question dynamique pour l'API Gemini
        String question = String.format(
            "Quelles sont les formations nécessaires pour devenir %s ? Merci de répondre sous forme de JSON avec les champs 'name' pour le nom de la formation et 'url' pour le lien vers la formation.",
            metier
        );

        // Créer le corps de la requête pour Gemini
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode textNode = objectMapper.createObjectNode();
        textNode.put("text", question);

        ArrayNode partsArray = objectMapper.createArrayNode();
        partsArray.add(textNode);

        ObjectNode contentNode = objectMapper.createObjectNode();
        contentNode.set("parts", partsArray);

        ArrayNode contentsArray = objectMapper.createArrayNode();
        contentsArray.add(contentNode);

        ObjectNode requestBodyNode = objectMapper.createObjectNode();
        requestBodyNode.set("contents", contentsArray);

        String requestBody = objectMapper.writeValueAsString(requestBodyNode);

        // URL de l'API Gemini
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + apiKey;

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        // Effectuer la requête
        ResponseEntity<String> response = restTemplate.exchange(
            url, HttpMethod.POST, entity, String.class
        );

        // Vérifier la réponse
        if (response.getStatusCode() != HttpStatus.OK) {
            return ResponseEntity.status(response.getStatusCode()).body("Erreur lors de l'appel à l'API Gemini.");
        }

        // Analyser la réponse de l'API Gemini
        JsonNode rootNode = objectMapper.readTree(response.getBody());
        JsonNode candidatesNode = rootNode.path("candidates");

        if (candidatesNode.isArray() && candidatesNode.size() > 0) {
            JsonNode contentNodeResponse = candidatesNode.get(0).path("content").path("parts").get(0).path("text");
            String jsonText = contentNodeResponse.asText().replace("json", "").replace("", "").trim();

            // Vérifier et enregistrer les formations
            JsonNode formationsNode;
            try {
                formationsNode = objectMapper.readTree(jsonText);
            } catch (Exception ex) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Format de réponse inattendu.");
            }

            if (formationsNode.isArray()) {
                for (JsonNode formationJson : formationsNode) {
                    String formationName = formationJson.path("name").asText();
                    String urlFormation = formationJson.path("url").asText();

                    // Créer l'objet Formation et associer l'utilisateur
                    Formation formation = new Formation();
                    formation.setFormationName(formationName);
                    formation.setUrl(urlFormation);
                    formation.setUser(user);  // Associer l'utilisateur authentifié

                    // Enregistrer la formation dans la base de données
                    formationService.saveFormation(formation);
                }

                // Retourner la réponse sous forme de JSON avec un message
                Map<String, String> responseMap = new HashMap<>();
                responseMap.put("message", "Formations enregistrées avec succès.");
                return ResponseEntity.ok(responseMap);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("La réponse de l'API n'est pas sous forme de liste.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Aucune formation trouvée.");
        }

    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur de communication avec l'API Gemini : " + e.getMessage());
    }
}

@GetMapping("/userFormations/{userId}")
public ResponseEntity<Object> getUserFormations(@PathVariable("userId") int userId) {
    try {
        System.err.println("User récupéré depuis l'URL: " + userId);

        // Récupérer l'utilisateur par son ID
        User user = userService.getUserById(userId);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utilisateur non trouvé.");
        }

        // Récupérer les formations associées à cet utilisateur
        List<Formation> formations = formationService.getFormationsByUserId(userId);

        // Si la liste est vide, retourner une liste vide avec un code 200 OK
        if (formations.isEmpty()) {
            System.out.println("Aucune formation trouvée pour cet utilisateur.");
            return ResponseEntity.ok(Collections.emptyList());
        }

        // Retourner les formations avec le code 200 OK
        return ResponseEntity.ok(formations);
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la récupération des formations : " + e.getMessage());
    }
}



}
