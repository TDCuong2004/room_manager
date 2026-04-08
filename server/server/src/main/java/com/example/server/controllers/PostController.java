package com.example.server.controllers;

import com.example.server.dto.PostDTO;
import com.example.server.entity.PostEntity;
import com.example.server.services.PostService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin
public class PostController {

    @Autowired
    private PostService postService;

    // ================= GET ALL =================
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
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam Long roomId,
            @RequestParam(required = false) List<MultipartFile> images,
            Principal principal
    ) {
        try {

            if (principal == null) {
                return ResponseEntity.status(401).body("Chưa đăng nhập");
            }

            String username = principal.getName();

            // 👉 map sang DTO
            PostDTO dto = new PostDTO();
            dto.setTitle(title);
            dto.setContent(content);
            dto.setRoomId(roomId);

            PostEntity post = postService.create(dto, username, images);

            return ResponseEntity.ok(post);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}