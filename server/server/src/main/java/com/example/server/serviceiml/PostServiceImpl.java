package com.example.server.serviceiml;

import com.example.server.dto.PostDTO;
import com.example.server.entity.PostEntity;
import com.example.server.entity.PostImageEntity;
import com.example.server.entity.User;
import com.example.server.enums.PostStatus;
import com.example.server.repository.PostImageRepository;
import com.example.server.repository.PostRepository;
import com.example.server.repository.RoomRepository;
import com.example.server.repository.UserRepository;
import com.example.server.services.PostService;
import com.example.server.dto.PostResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    @Override
    public List<PostResponse> getAll() {

        return postRepository.findAll().stream().map(p -> {

            PostResponse dto = new PostResponse();
            dto.setId(p.getId());
            dto.setTitle(p.getTitle());
            dto.setAddress(p.getAddress());
            dto.setPrice(p.getPrice());

            List<String> images = postImageRepository.findByPostId(p.getId())
                    .stream()
                    .map(img -> img.getImageUrl())
                    .toList();

            dto.setImages(images);

            return dto;

        }).toList();
    }
    @Override
    public PostEntity create(PostDTO dto, String username) {

        // tìm user từ token
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // tạo post
        PostEntity post = new PostEntity();
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setPrice(dto.getPrice());
        post.setArea(dto.getArea());
        post.setAddress(dto.getAddress());
        post.setStatus(PostStatus.PUBLISHED);
        post.setCreatedAt(LocalDateTime.now());
        post.setUser(user);

        // set room
        post.setRoom(
                roomRepository.findById(dto.getRoomId())
                        .orElseThrow(() -> new RuntimeException("Room not found"))
        );

        // lưu post trước
        postRepository.save(post);

        // lưu ảnh
        if (dto.getImages() != null) {
            for (String url : dto.getImages()) {
                PostImageEntity img = new PostImageEntity();
                img.setImageUrl(url);
                img.setPost(post);
                postImageRepository.save(img);
            }
        }

        return post;
    }
}