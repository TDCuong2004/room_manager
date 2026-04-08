package com.example.server.dto;

import com.example.server.entity.User;
import lombok.Data;
@Data
public class UserProfileDTO {

    private String username;
    private String fullName;
    private String phone;
    private String email;
    private String avatar;

    public UserProfileDTO(User user) {
        this.username = user.getUsername();
        this.fullName = user.getFullName();
        this.phone = user.getPhone();
        this.email = user.getEmail();
        this.avatar = user.getAvatar();
    }
}
