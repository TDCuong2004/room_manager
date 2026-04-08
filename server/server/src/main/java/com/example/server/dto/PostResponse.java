package com.example.server.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostResponse {

    private Long id;
    private String title;
    private String content;
    private String address;
    private Double price;
    private LocalDateTime createdAt;

    private String userName;
    private String userAvatar;

    private RoomResponse room;
    private String phone;
    private List<String> images;
}