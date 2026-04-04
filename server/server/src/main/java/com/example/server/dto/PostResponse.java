package com.example.server.dto;

import lombok.Data;
import java.util.List;

@Data
public class PostResponse {
    private Long id;
    private String title;
    private String address;
    private Double price;
    private List<String> images;
}