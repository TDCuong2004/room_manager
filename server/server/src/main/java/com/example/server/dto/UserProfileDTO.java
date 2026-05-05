package com.example.server.dto;

import com.example.server.entity.User;
import lombok.Data;

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
    }
}