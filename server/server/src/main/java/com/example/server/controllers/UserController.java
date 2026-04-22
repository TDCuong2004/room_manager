package com.example.server.controllers;

import com.example.server.dto.UpdateProfileRequest;
import com.example.server.dto.UserProfileDTO;
import com.example.server.entity.User;
import com.example.server.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // ================= GET PROFILE =================
    @GetMapping("/profile")
    public UserProfileDTO getProfile(Principal principal) {

        if (principal == null) {
            throw new RuntimeException("Chưa đăng nhập");
        }

        User user = userRepository
                .findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        return new UserProfileDTO(user); // ✅ TRẢ DTO
    }

    // ================= UPDATE PROFILE =================
    @PutMapping("/profile")
    public UserProfileDTO updateProfile(
            @RequestBody UpdateProfileRequest req,
            Principal principal) {

        if (principal == null) {
            throw new RuntimeException("Chưa đăng nhập");
        }

        User user = userRepository
                .findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        user.setFullName(req.getFullName());
        user.setPhone(req.getPhone());
        user.setEmail(req.getEmail());
        user.setAvatar(req.getAvatar());
        user.setBankName(req.getBankName());
        user.setBankCode(req.getBankCode());
        user.setBankAccount(req.getBankAccount());
        userRepository.save(user);

        return new UserProfileDTO(user);
    }
}