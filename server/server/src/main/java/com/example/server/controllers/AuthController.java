package com.example.server.controllers;

import com.example.server.dto.ChangePasswordRequest;
import com.example.server.dto.RegisterRequest;
import com.example.server.enums.UserRole;
import com.example.server.security.jwt.JwtUtils;
import com.example.server.entity.User;
import com.example.server.repository.UserRepository;
import com.example.server.services.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

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

    @Autowired
    private MailService mailService;
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(
            @RequestParam String email
    ) {

        try {

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() ->
                            new RuntimeException("Email không tồn tại"));

            // tạo mật khẩu mới
            String newPassword = UUID.randomUUID()
                    .toString()
                    .replace("-", "")
                    .substring(0, 8);

            // encode password
            user.setPassword(
                    passwordEncoder.encode(newPassword)
            );

            userRepository.save(user);

            // gửi mail
            mailService.sendNewPassword(email, newPassword);

            return ResponseEntity.ok(
                    "Đã gửi mật khẩu mới qua email"
            );

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }
    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestBody ChangePasswordRequest request,
            Principal principal
    ) {

        try {

            if (principal == null) {
                return ResponseEntity.status(401)
                        .body("Chưa đăng nhập");
            }

            User user = userRepository
                    .findByUsername(principal.getName())
                    .orElseThrow();

            // check password cũ
            if (!passwordEncoder.matches(
                    request.getOldPassword(),
                    user.getPassword()
            )) {

                return ResponseEntity.badRequest()
                        .body("Mật khẩu cũ không đúng");
            }

            // check confirm
            if (!request.getNewPassword()
                    .equals(request.getConfirmPassword())) {

                return ResponseEntity.badRequest()
                        .body("Xác nhận mật khẩu không khớp");
            }

            // update password
            user.setPassword(
                    passwordEncoder.encode(
                            request.getNewPassword()
                    )
            );

            userRepository.save(user);

            return ResponseEntity.ok(
                    "Đổi mật khẩu thành công"
            );

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }
}