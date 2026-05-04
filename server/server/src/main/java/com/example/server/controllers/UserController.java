package com.example.server.controllers;

import com.example.server.dto.UpdateProfileRequest;
import com.example.server.dto.UserProfileDTO;
import com.example.server.entity.User;
import com.example.server.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

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

    @GetMapping
    public List<UserProfileDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserProfileDTO::new)
                .toList();
    }
    // ================= UPDATE USER (ADMIN) =================
    @PutMapping("/{id}")
    public UserProfileDTO updateUser(
            @PathVariable Long id,
            @RequestBody UpdateProfileRequest req) {

        User user = userRepository.findById(id)
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
    // ================= DELETE USER =================
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
    }
}