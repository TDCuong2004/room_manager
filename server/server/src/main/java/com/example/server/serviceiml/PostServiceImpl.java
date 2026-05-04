package com.example.server.serviceiml;

import com.example.server.dto.PostDTO;
import com.example.server.dto.PostResponse;
import com.example.server.dto.PostSearchRequest;
import com.example.server.dto.RoomResponse;
import com.example.server.entity.PostEntity;
import com.example.server.entity.PostImageEntity;
import com.example.server.entity.Rooms;
import com.example.server.entity.User;
import com.example.server.enums.PostStatus;
import com.example.server.repository.PostImageRepository;
import com.example.server.repository.PostRepository;
import com.example.server.repository.RoomRepository;
import com.example.server.repository.UserRepository;
import com.example.server.services.PostService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PostServiceImpl implements PostService {
//    @Autowired
//    private AIService aiService;
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    // ================= GET ALL =================
    @Override
    public List<PostResponse> getAll() {
        return postRepository.findAllWithImages() // ✅ FIX Ở ĐÂY
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ================= SEARCH =================
    @Override
    public List<PostResponse> search(PostSearchRequest request) {

        List<PostEntity> posts = postRepository.searchPosts(
                emptyToNull(request.getLocation()),
                request.getMaxPrice(),
                request.getArea()
        );

        return posts.stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ================= CREATE =================
    @Override
    public PostEntity create(PostDTO dto, String username, List<MultipartFile> files) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Rooms room = roomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new RuntimeException("Room not found"));

        PostEntity post = new PostEntity();
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setStatus(PostStatus.PUBLISHED);
        post.setCreatedAt(LocalDateTime.now());
        post.setUser(user);
        post.setRoom(room);

        // lấy từ room
        post.setPrice(room.getPrice().doubleValue());
        post.setArea(room.getArea().doubleValue());
        post.setAddress(room.getBuilding().getAddress());

        post = postRepository.save(post);

        // ===== SAVE IMAGES =====
        if (files != null && !files.isEmpty()) {
            List<PostImageEntity> images = new ArrayList<>();

            for (MultipartFile file : files) {

                String url = saveFile(file);

                PostImageEntity img = new PostImageEntity();
                img.setImageUrl(url);
                img.setPost(post);

                images.add(img);
            }

            post.setImages(images); // gắn vào post (quan trọng)
        }

        return postRepository.save(post);
    }

    // ================= MAPPER =================
    private PostResponse mapToResponse(PostEntity p) {

        PostResponse dto = new PostResponse();

        dto.setId(p.getId());
        dto.setTitle(p.getTitle());
        dto.setContent(p.getContent());
        dto.setAddress(p.getAddress());
        dto.setPrice(p.getPrice());
        dto.setCreatedAt(p.getCreatedAt());

        // USER
        if (p.getUser() != null) {
            dto.setUserName(p.getUser().getFullName());
            dto.setUserAvatar(p.getUser().getAvatar());
            dto.setPhone(p.getUser().getPhone());
        }

        // ROOM
        if (p.getRoom() != null) {
            RoomResponse roomDTO = new RoomResponse();

            roomDTO.setId(p.getRoom().getId());
            roomDTO.setRoomName(p.getRoom().getRoomName());
            roomDTO.setPrice(p.getRoom().getPrice().doubleValue());
            roomDTO.setArea(p.getRoom().getArea().doubleValue());
            roomDTO.setMaxPeople(p.getRoom().getMaxPeople());

            roomDTO.setStatus(p.getRoom().getStatus().name());

            dto.setRoom(roomDTO);
        }

        // IMAGES (KHÔNG query nữa → dùng trực tiếp)
        if (p.getImages() != null) {
            dto.setImages(
                    p.getImages().stream()
                            .map(PostImageEntity::getImageUrl)
                            .toList()
            );
        }

        return dto;
    }

    // ================= HELPER =================
    private String emptyToNull(String s) {
        return (s == null || s.trim().isEmpty()) ? null : s;
    }

    // ================= UPLOAD FILE =================
    private String saveFile(MultipartFile file) {
        try {
            String uploadDir = System.getProperty("user.dir") + "/uploads/";

            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            File dest = new File(uploadDir + fileName);

            file.transferTo(dest);

            return "http://localhost:3000/uploads/" + fileName;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Upload failed: " + e.getMessage());
        }
    }
    @Override
    public List<PostResponse> getMyPosts(String username) {

        List<PostEntity> posts = postRepository.findByUsername(username);

        return posts.stream()
                .map(this::mapToResponse)
                .toList();
    }
    @Override
    public void hidePost(Long id, String username) {

        PostEntity post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post không tồn tại"));

        // kiểm tra quyền
        if (!post.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Bạn không có quyền ẩn bài này");
        }

        // 🔥 SOFT DELETE
        post.setStatus(PostStatus.HIDDEN);

        postRepository.save(post);
    }
}