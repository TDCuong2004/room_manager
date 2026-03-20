package com.example.server.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "room_images")
@Data
public class RoomImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Rooms room;
}