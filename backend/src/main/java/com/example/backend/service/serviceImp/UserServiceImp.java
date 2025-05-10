package com.example.backend.service.serviceImp;



import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.backend.config.JwtProvider;
import com.example.backend.dto.AuthResponse;
import com.example.backend.dto.LoginDTO;
import com.example.backend.dto.RegisterDTO;
import com.example.backend.entities.User;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.servicesInterfaces.UserService;

@Service
public class UserServiceImp implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public UserServiceImp(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtProvider jwtProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
    }
    @Override
    public String register(RegisterDTO request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already in use.");
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);
        return "User registered successfully: " + user.getEmail();
    }

   @Override
   public AuthResponse login(LoginDTO request) {
    User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));

    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
        throw new IllegalArgumentException("Invalid email or password.");
    }

    String token = jwtProvider.generateToken(user.getEmail());

    return new AuthResponse("Login successful", token, user.getId(), user.getEmail());
}   

public User getUserById(int userId) {
        return userRepository.findById(userId).orElse(null); // ou vous pouvez gérer l'exception selon votre logique
    }
  }
