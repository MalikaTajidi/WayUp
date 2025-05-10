package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String message;
    private String token;

    private int userId;
    private String email;

     public AuthResponse(String message) {
         this.message = message;
     }

     public AuthResponse(String string, AuthResponse token2, UserDTO userDTO) {
      //TODO Auto-generated constructor stub
     }

    // public AuthResponse(String message, String token) {
    //     this.message = message;
    //     this.token = token;
    // }

    // public String getMessage() {
    //     return message;
    // }

    // public String getToken() {
    //     return token;
    // }
}
