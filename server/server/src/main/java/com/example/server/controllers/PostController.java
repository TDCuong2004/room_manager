package com.example.server.controllers;

import com.example.server.dto.PostDTO;
import com.example.server.dto.PostSearchRequest;
import com.example.server.entity.PostEntity;
import com.example.server.services.PostService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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
    @PostMapping("/search")
    public ResponseEntity<?> searchPosts(@RequestBody PostSearchRequest request) {
        try {
            return ResponseEntity.ok(postService.search(request));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Lỗi tìm kiếm");
        }
    }
    // ================= CREATE POST =================
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createPost(
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam Long roomId,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            Principal principal
    ) {
        try {

            if (principal == null) {
                return ResponseEntity.status(401).body("Chưa đăng nhập");
            }

            String username = principal.getName();
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
    @GetMapping("/my")
    public ResponseEntity<?> getMyPosts(Principal principal) {
        try {
            if (principal == null) {
                return ResponseEntity.status(401).body("Chưa đăng nhập");
            }

            String username = principal.getName();

            return ResponseEntity.ok(postService.getMyPosts(username));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PutMapping("/{id}/hide")
    public ResponseEntity<?> hidePost(@PathVariable Long id, Principal principal) {
        try {
            if (principal == null) {
                return ResponseEntity.status(401).body("Chưa đăng nhập");
            }

            postService.hidePost(id, principal.getName());

            return ResponseEntity.ok("Đã ẩn bài viết");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}