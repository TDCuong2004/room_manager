package com.example.server.entity;

import com.example.server.enums.PostStatus;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "posts")
public class PostEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private Double price;
    private Double area;
    private String address;

    @Enumerated(EnumType.STRING)
    private PostStatus status;

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Rooms room;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostImageEntity> images;
}
