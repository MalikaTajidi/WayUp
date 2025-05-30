package com.example.backend.service.serviceImp;



import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.backend.config.JwtProvider;
import com.example.backend.dto.LoginDTO;
import com.example.backend.dto.RegisterDTO;
import com.example.backend.entities.Formation;
import com.example.backend.entities.User;
import com.example.backend.repository.FormationRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.servicesInterfaces.UserService;

@Service
public class UserServiceImp implements UserService {
    private final UserRepository userRepository;
        private final FormationRepository formationRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;


    public UserServiceImp(UserRepository userRepository,FormationRepository formationRepository, PasswordEncoder passwordEncoder, JwtProvider jwtProvider) {
        this.userRepository = userRepository;
        this.formationRepository = formationRepository;
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
    public String login(LoginDTO request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password.");
        }

        return jwtProvider.generateToken(user.getEmail());
    }

public User getUserById(int userId) {
        return userRepository.findById(userId).orElse(null); // ou vous pouvez gérer l'exception selon votre logique
    }
    


    public List<Formation> getFormationsByUserId(int userId) {
    return formationRepository.findByUserId(userId);
}

    }