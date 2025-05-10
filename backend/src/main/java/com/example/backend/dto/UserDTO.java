package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
private int id;
    private String email;
    private String firstName;
    private String lastName;
    private String metierSugg;
    private Boolean testDone;
}