package com.example.server.dto;

import lombok.Data;

@Data
public class PostSearchRequest {
    private String location;
    private Double maxPrice;
    private Double area;
}
