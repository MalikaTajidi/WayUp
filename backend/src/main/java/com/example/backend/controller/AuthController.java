package com.example.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.dto.AuthResponse;
import com.example.backend.dto.LoginDTO;
import com.example.backend.dto.RegisterDTO;
import com.example.backend.dto.UserDTO;
import com.example.backend.entities.User;
import com.example.backend.repository.UserRepository;

import com.example.backend.dto.AuthResponse;

import com.example.backend.service.servicesInterfaces.UserService;

@RestController

public class AuthController {

     @Autowired
    private UserRepository userRepository;
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }
    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Validated @RequestBody RegisterDTO request) {
        String message = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new AuthResponse(message));
    }
    @CrossOrigin(origins = "http://localhost:4200")
  @PostMapping("/login")
public ResponseEntity<AuthResponse> login(@Validated @RequestBody LoginDTO request) {
    User user = userRepository.findByEmail(request.getEmail()).orElseThrow();
    AuthResponse token = userService.login(request);
    UserDTO userDTO = new UserDTO(user.getId(), user.getEmail(), user.getFirstName(), user.getLastName(),user.getMetierSugg(),user.isTestDone());

    return ResponseEntity.ok(new AuthResponse("Login successful", token, userDTO));
}


}