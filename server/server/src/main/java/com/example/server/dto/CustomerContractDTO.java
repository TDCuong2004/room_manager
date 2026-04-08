package com.example.server.dto;

import lombok.Data;

@Data
public class CustomerContractDTO {

    private String fullName;
    private String phone;
    private Integer birthYear;
    private String gender;

    private boolean representative;

    private String cccdFrontImage; // URL ảnh
    private String cccdBackImage;  // URL ảnh
}