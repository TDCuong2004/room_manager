package com.example.server.dto;

import lombok.Data;

import java.util.List;

@Data
public class RoomResponse {

    private Long id;
    private String roomName;
    private String roomCode;
    private Double price;
    private Double area;
    private Integer maxPeople;
    private String status;

    private List<String> images;

}
