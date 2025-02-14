package com.example.blahblahapp.dtos.user;

import com.example.blahblahapp.enums.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserUpdateDTO {
    private String displayname;

    @Email
    private String email;

    private UserStatus status;
    private String phone;
    private String address;

    @Size(max = 500, message = "Bio cannot exceed 500 characters")
    private String bio;

    private String profilePictureUrl;
    private String coverPictureUrl;
    private List<InterestDTO> interests;
    private List<SocialLinkDTO> socialLinks;
}
