package com.example.server.serviceiml;

import com.example.server.dto.PostDTO;
import com.example.server.dto.PostResponse;
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
import java.util.List;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostImageRepository postImageRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    // ================= GET ALL =================
    @Override
    public List<PostResponse> getAll() {

        return postRepository.findAll().stream().map(p -> {

            PostResponse dto = new PostResponse();

            dto.setId(p.getId());
            dto.setTitle(p.getTitle());
            dto.setContent(p.getContent());
            dto.setAddress(p.getAddress());
            dto.setPrice(p.getPrice());
            dto.setCreatedAt(p.getCreatedAt());
            dto.setUserName(p.getUser().getFullName());
            dto.setUserAvatar(p.getUser().getAvatar());
            dto.setPhone(p.getUser().getPhone());
            // ===== ROOM =====
            if (p.getRoom() != null) {
                RoomResponse roomDTO = new RoomResponse();
                roomDTO.setRoomName(p.getRoom().getRoomName());
                roomDTO.setPrice(p.getRoom().getPrice().doubleValue());
                dto.setRoom(roomDTO);
            }

            // ===== IMAGES =====
            List<String> images = postImageRepository.findByPostId(p.getId())
                    .stream()
                    .map(PostImageEntity::getImageUrl)
                    .toList();

            dto.setImages(images);

            return dto;

        }).toList();
    }

    // ================= CREATE =================
    @Override
    public PostEntity create(PostDTO dto, String username, List<MultipartFile> files) {

        // 🔍 tìm user
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 🔍 tìm room
        Rooms room = roomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new RuntimeException("Room not found"));

        // 📝 tạo post
        PostEntity post = new PostEntity();
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setStatus(PostStatus.PUBLISHED);
        post.setCreatedAt(LocalDateTime.now());
        post.setUser(user);
        post.setRoom(room);

        // 👉 lấy info từ room (chuẩn hơn)
        post.setPrice(room.getPrice().doubleValue());
        post.setArea(room.getArea().doubleValue());
        post.setAddress(room.getBuilding().getAddress());

        // 💾 lưu post
        post = postRepository.save(post);

        // 🖼️ lưu ảnh
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {

                String url = saveFile(file);

                PostImageEntity img = new PostImageEntity();
                img.setImageUrl(url);
                img.setPost(post);

                postImageRepository.save(img);
            }
        }

        return post;
    }

    // ================= UPLOAD FILE (DEMO) =================
    private String saveFile(MultipartFile file) {
        try {
            String uploadDir = System.getProperty("user.dir") + "/uploads/";

            // tạo folder
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
}