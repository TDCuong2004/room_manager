package com.example.server.controllers;

import com.example.server.dto.PostDTO;
import com.example.server.entity.PostEntity;
import com.example.server.services.PostService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin
public class PostController {

    @Autowired
    private PostService postService;
    @GetMapping
    public ResponseEntity<?> getAllPosts() {
        try {
            return ResponseEntity.ok(postService.getAll());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    // ================= CREATE POST =================
    @PostMapping
    public ResponseEntity<?> createPost(
            @RequestBody PostDTO dto,
            Principal principal
    ) {
        try {

            System.out.println("Principal = " + principal);

            if (principal == null) {
                return ResponseEntity.status(401).body("Chưa đăng nhập");
            }

            String username = principal.getName();

            System.out.println("DTO = " + dto);

            PostEntity post = postService.create(dto, username);

            return ResponseEntity.ok(post);

        } catch (Exception e) {
            e.printStackTrace(); // 🔥 QUAN TRỌNG
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}