package com.example.server.dto;

import com.example.server.entity.User;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserProfileDTO {
    private Long id;
    private String username;
    private String fullName;
    private String phone;
    private String email;
    private String avatar;
    private String role;

    private String status;

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

    public UserProfileDTO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.fullName = user.getFullName();
        this.phone = user.getPhone();
        this.email = user.getEmail();
        this.avatar = user.getAvatar();

        this.role = user.getRole().name();
        this.status = user.getStatus().name();

        this.bankName = user.getBankName();
        this.bankCode = user.getBankCode();
        this.bankAccount = user.getBankAccount();
        // CCCD
        this.cccdNumber = user.getCccdNumber();
        this.cccdFrontImage = user.getCccdFrontImage();
        this.cccdBackImage = user.getCccdBackImage();
        this.cccdIssueDate = user.getCccdIssueDate();
        this.cccdIssuePlace = user.getCccdIssuePlace();
        this.dateOfBirth = user.getDateOfBirth();
    }
}