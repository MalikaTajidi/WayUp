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
import java.util.Optional;

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
    
    

@PostMapping("/getFormations/{userId}")
public ResponseEntity<Object> getFormations(@PathVariable int userId, @RequestBody String metier) {
    try {
        // Construire la question dynamique
        String question = String.format(
            "Quelles sont les technologies et compétences nécessaires pour devenir %s  au niveau d'etude ? Merci de répondre sous forme de JSON avec les champs 'name' pour le nom de la technologie/compétence et 'url' pour le lien vers la ressource d'apprentissage.",
            metier
        );

        // Utiliser ObjectMapper pour créer le corps de la requête
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

        // Préparer la requête avec RestTemplate
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        // Effectuer la requête
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        if (response.getStatusCode() != HttpStatus.OK) {
            return ResponseEntity.status(response.getStatusCode()).body(createJsonResponse("error", "Erreur lors de l'appel à l'API Gemini."));
        }

        // Analyser la réponse JSON
        JsonNode rootNode = objectMapper.readTree(response.getBody());
        JsonNode candidatesNode = rootNode.path("candidates");

        if (candidatesNode.isArray() && candidatesNode.size() > 0) {
            JsonNode contentNodeResponse = candidatesNode.get(0).path("content").path("parts").get(0).path("text");
            String jsonText = contentNodeResponse.asText().replace("```json", "").replace("```", "").trim();

            JsonNode formationsNode;
            try {
                formationsNode = objectMapper.readTree(jsonText);
            } catch (Exception ex) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createJsonResponse("error", "Format de réponse inattendu."));
            }

            // Récupérer l'utilisateur depuis la base de données
            Optional<User> userOptional = Optional.ofNullable(userService.getUserById(userId));
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(createJsonResponse("error", "Utilisateur non trouvé."));
            }
            User user = userOptional.get();

            // Enregistrer les formations
            if (formationsNode.isArray()) {
                for (JsonNode formation : formationsNode) {
                    String formationName = formation.path("name").asText();
                    String urlFormation = formation.path("url").asText();

                    Formation newFormation = new Formation();
                    newFormation.setFormationName(formationName);
                    newFormation.setUrl(urlFormation);
                    newFormation.setUser(user);

                    formationService.saveFormation(newFormation);
                }
                return ResponseEntity.ok(createJsonResponse("success", "Formations enregistrées avec succès."));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createJsonResponse("error", "Les données retournées ne sont pas au format attendu."));
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(createJsonResponse("error", "Aucune formation trouvée."));
        }

    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createJsonResponse("error", "Erreur : " + e.getMessage()));
    }
}

private String createJsonResponse(String status, String message) {
    try {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode responseNode = objectMapper.createObjectNode();
        responseNode.put("status", status);
        responseNode.put("message", message);
        return objectMapper.writeValueAsString(responseNode);
    } catch (Exception e) {
        return "{\"status\": \"error\", \"message\": \"Erreur de création de réponse JSON\"}";
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