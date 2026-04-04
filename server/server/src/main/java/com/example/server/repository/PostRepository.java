package com.example.server.repository;

import com.example.server.entity.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface PostRepository extends JpaRepository<PostEntity, Long> {
}
