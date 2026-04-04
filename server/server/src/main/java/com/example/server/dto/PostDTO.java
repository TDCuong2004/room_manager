package com.example.server.dto;

import lombok.Data;

import java.util.List;
@Data
public class PostDTO {
    public String title;
    public String content;
    public Double price;
    public Double area;
    public String address;
    public Long roomId;
    public List<String> images;
}