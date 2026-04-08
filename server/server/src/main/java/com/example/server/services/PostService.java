package com.example.server.services;

import com.example.server.dto.PostDTO;
import com.example.server.dto.PostResponse;
import com.example.server.entity.PostEntity;
import com.example.server.entity.PostImageEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostService {

    List<PostResponse> getAll();

    PostEntity create(PostDTO dto, String username, List<MultipartFile> files);
}