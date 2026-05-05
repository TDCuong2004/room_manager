package com.example.server.dto;

import com.example.server.enums.UserStatus;
import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String fullName;
    private String phone;
    private String email;
    private String avatar;

    private String bankName;
    private String bankCode;
    private String bankAccount;
    private UserStatus status;
}