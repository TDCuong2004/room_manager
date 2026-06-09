package com.example.server.dto;

import com.example.server.enums.UserStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateProfileRequest {

    private String fullName;
    private String phone;
    private String email;
    private String avatar;

    private String bankName;
    private String bankCode;
    private String bankAccount;

    // CCCD
    private String cccdNumber;
    private String cccdFrontImage;
    private String cccdBackImage;
    private LocalDate cccdIssueDate;
    private String cccdIssuePlace;
    private LocalDate dateOfBirth;
    private UserStatus status;
}