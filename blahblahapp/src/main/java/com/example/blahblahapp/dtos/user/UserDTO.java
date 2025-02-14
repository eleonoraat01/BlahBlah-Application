package com.example.blahblahapp.dtos.user;

import com.example.blahblahapp.enums.UserStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long id;
    private String username;
    private String displayname;
    private String email;
    private UserStatus status;
    private String phone;
    private String address;
    private String bio;
    private String profilePictureUrl;
    private String coverPictureUrl;
    private List<InterestDTO> interests;
    private List<SocialLinkDTO> socialLinks;

    @JsonProperty("isActive")
    private boolean isActive;

    private long createdAt;
    private long updatedAt;

    private List<FriendshipDTO> friendships;
}
