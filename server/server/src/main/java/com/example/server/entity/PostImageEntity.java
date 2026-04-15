package com.example.server.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "post_images")
public class PostImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imageUrl;

//    @ManyToOne
//    @JoinColumn(name = "post_id")
//    private PostEntity post;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "post_id")
    private PostEntity post;
}
