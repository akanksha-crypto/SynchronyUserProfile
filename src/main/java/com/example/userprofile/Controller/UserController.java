package com.example.userprofile.Controller;

import com.example.userprofile.Dtos.LoginDTO;
import com.example.userprofile.Dtos.RegisterDTO;
import com.example.userprofile.Models.User;
import com.example.userprofile.Repository.UserRepo;
import com.example.userprofile.Service.ImgurService;
import com.example.userprofile.Service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.method.P;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {
    private UserService userService;
    private ImgurService imgurService;
    private UserRepo userRepo;

    public UserController(UserService userService, ImgurService imgurService, UserRepo userRepo){
        this.userService = userService;
        this.imgurService = imgurService;
        this.userRepo = userRepo;
    }

    @PostMapping("/register")
    public User registerUser(@RequestBody RegisterDTO registerDTO){
        return userService.registerUser(registerDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDTO loginDTO) {
        return userService.login(loginDTO);
    }

    //@PostMapping("/{username}/uploadImage")
    public ResponseEntity<String> uploadImage(@PathVariable String username, @RequestParam("file") MultipartFile file) {
        try {
            String imageUrl = imgurService.uploadImage(file);

            Optional<User> user = userRepo.findByUsername(username);
            if (user != null) {
                user.get().setImageLink(imageUrl);
                userRepo.save(user.get());
                return ResponseEntity.ok("Image uploaded successfully: " + imageUrl);
            }
            return ResponseEntity.status(404).body("User not found.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Image upload failed.");
        }
    }

    // View user profile with image
    @GetMapping("/{username}/profile")
    public ResponseEntity<User> getUserProfile(@PathVariable String username) {
        Optional<User> user = userRepo.findByUsername(username);
        if (user.get() != null) {
            return ResponseEntity.ok(user.get());
        }
        return ResponseEntity.status(404).body(null);
    }

    // Delete user image
    @DeleteMapping("/{username}/deleteImage")
    public ResponseEntity<String> deleteImage(@PathVariable Long userid) {
        Optional<User> user = userRepo.findById(userid);
        if (user.get() != null && !user.get().getImageLink().equals(null)) {
            String imageHash = user.get().getImageLink().split("/")[4]; // Get image hash from URL
            imgurService.deleteImage(imageHash);
            user.get().setImageLink(null);  // Remove image URL from user profile
            userRepo.save(user.get());
            return ResponseEntity.ok("Image deleted successfully.");
        }
        return ResponseEntity.status(404).body("User or image not found.");
    }
}
