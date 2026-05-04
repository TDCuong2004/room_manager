package com.example.server.controllers;

import com.example.server.dto.RegisterRequest;
import com.example.server.enums.UserRole;
import com.example.server.security.jwt.JwtUtils;
import com.example.server.entity.User;
import com.example.server.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public AuthController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> request) {

        User user = userRepository
                .findByUsername(request.get("username"))
                .orElseThrow();

        if (!passwordEncoder.matches(
                request.get("password"), user.getPassword())) {
            throw new RuntimeException("Sai mật khẩu");
        }

        String token = jwtUtils.generateToken(user.getUsername());

        return Map.of(
                "token", token,
                "role", user.getRole().name()
        );
    }
    //register
    @PostMapping("/register")
    public Map<String, String> register(@RequestBody RegisterRequest request) {

        if (request.getPhone() == null || request.getPassword() == null) {
            throw new RuntimeException("Thiếu thông tin");
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Mật khẩu không khớp");
        }

        // 🔥 check trùng SĐT
        if (userRepository.findByUsername(request.getPhone()).isPresent()) {
            throw new RuntimeException("Số điện thoại đã tồn tại");
        }

        User user = new User();
        user.setUsername(request.getPhone()); // 👉 username = phone
        user.setPhone(request.getPhone());
        user.setFullName(request.getFullName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.USER);

        userRepository.save(user);

        String token = jwtUtils.generateToken(user.getUsername());

        return Map.of(
                "message", "Đăng ký thành công",
                "token", token,
                "role", user.getRole().name()
        );
    }
}