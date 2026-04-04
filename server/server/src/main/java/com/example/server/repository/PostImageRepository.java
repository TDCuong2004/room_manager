package com.example.server.repository;

import com.example.server.entity.PostImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostImageRepository extends JpaRepository<PostImageEntity, Long> {
    List<PostImageEntity> findByPostId(Long postId);
}
