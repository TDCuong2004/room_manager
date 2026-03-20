package com.example.server.controllers;

import com.example.server.entity.User;
import com.example.server.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Lấy profile
    @GetMapping("/profile")
    public User getProfile(Principal principal) {

        return userRepository
                .findByUsername(principal.getName())
                .orElseThrow();
    }

    @PutMapping("/profile")
    public User updateProfile(
            @RequestBody User updatedUser,
            Principal principal) {

        User user = userRepository
                .findByUsername(principal.getName())
                .orElseThrow();

        user.setFullName(updatedUser.getFullName());
        user.setPhone(updatedUser.getPhone());
        user.setEmail(updatedUser.getEmail());
        user.setAvatar(updatedUser.getAvatar()); // thêm dòng này

        return userRepository.save(user);
    }
}
