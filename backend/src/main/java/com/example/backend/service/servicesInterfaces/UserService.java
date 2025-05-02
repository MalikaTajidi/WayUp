package com.example.backend.service.servicesInterfaces;

import com.example.backend.dto.LoginDTO;
import com.example.backend.dto.RegisterDTO;

public interface UserService {
    String register(RegisterDTO request);
    String login(LoginDTO request);
   
}