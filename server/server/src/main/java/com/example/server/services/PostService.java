package com.example.server.services;

import com.example.server.dto.PostDTO;
import com.example.server.dto.PostResponse;
import com.example.server.dto.PostSearchRequest;
import com.example.server.entity.PostEntity;
import com.example.server.entity.PostImageEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostService {
    void hidePost(Long id, String username);
    List<PostResponse> getAll();
    List<PostResponse> search(PostSearchRequest request);
    PostEntity create(PostDTO dto, String username, List<MultipartFile> files);
    List<PostResponse> getMyPosts(String username);

}