package com.example.backend.dto;

<<<<<<< HEAD
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
=======
import com.example.backend.entities.User;

>>>>>>> origin/safae
public class AuthResponse {
    private String message;
    private String token;
    private UserDTO user;

    private int userId;
    private String email;

<<<<<<< HEAD
     public AuthResponse(String message) {
         this.message = message;
     }
=======
    public AuthResponse(String message, String token) {
        this.message = message;
        this.token = token;
    }
     public AuthResponse(String message, String token,UserDTO user) {
        this.message = message;
        this.token = token;
        this.user = user;

    }
>>>>>>> origin/safae

     public AuthResponse(String string, AuthResponse token2, UserDTO userDTO) {
      //TODO Auto-generated constructor stub
     }

<<<<<<< HEAD
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
=======
    public String getToken() {
        return token;
    }
    public UserDTO getUser() {
        return user;
    }
>>>>>>> origin/safae
}
