package com.example.backend.dto;

import com.example.backend.entities.User;

public class AuthResponse {
    private String message;
    private String token;
    private UserDTO user;

    public AuthResponse(String message) {
        this.message = message;
    }

    public AuthResponse(String message, String token) {
        this.message = message;
        this.token = token;
    }
     public AuthResponse(String message, String token,UserDTO user) {
        this.message = message;
        this.token = token;
        this.user = user;

    }

    public String getMessage() {
        return message;
    }

    public String getToken() {
        return token;
    }
    public UserDTO getUser() {
        return user;
    }
}
