package com.example.userprofile.Service;

import com.example.userprofile.Dtos.LoginDTO;
import com.example.userprofile.Dtos.RegisterDTO;
import com.example.userprofile.Models.User;
import com.example.userprofile.Repository.UserRepo;
import com.example.userprofile.configs.EncodePassword;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private UserRepo userRepo;
    private EncodePassword encodePassword;

    public UserService(UserRepo userRepo, EncodePassword encodePassword) {
        this.userRepo = userRepo;
        this.encodePassword = encodePassword;
    }

    public User registerUser(RegisterDTO registerDTO) {
        if (registerDTO.getUsername() == null || registerDTO.getPassword() == null) {
            throw new IllegalArgumentException("Username or Password cannot be null");
        }
        User newuser = new User();
        newuser.setUsername(registerDTO.getUsername());
        newuser.setPassword(encodePassword.passwordEncoder().encode(registerDTO.getPassword()));
        return userRepo.save(newuser);
    }

    public ResponseEntity<String> login(LoginDTO loginDTO) {
        Optional<User> user1 = userRepo.findByUsername(loginDTO.getUsername());
        if ((user1.get() != null) && (encodePassword.passwordEncoder().matches(user1.get().getPassword(),loginDTO.getPassword()))){
                return ResponseEntity.ok("Login successful");
        }
        return ResponseEntity.status(401).body("Invalid username or password");
    }
}
