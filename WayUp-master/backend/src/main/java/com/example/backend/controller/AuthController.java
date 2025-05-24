package com.example.backend.controller;

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
import com.example.backend.service.servicesInterfaces.UserService;

@RestController

public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }
    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/register")
    public ResponseEntity<String> register(@Validated @RequestBody RegisterDTO request) {
        String message = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }
    @CrossOrigin(origins = "http://localhost:4200")
   @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@Validated @RequestBody LoginDTO request) {
    AuthResponse response = userService.login(request);
    return ResponseEntity.ok(response);
}
}
