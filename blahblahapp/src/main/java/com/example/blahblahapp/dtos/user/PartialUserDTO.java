package com.example.blahblahapp.dtos.user;

import com.example.blahblahapp.enums.UserStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PartialUserDTO {
    private Long id;
    private String username;
    private String displayname;
    private UserStatus status;
    private String profilePictureUrl;

//    @JsonProperty("isActive")
//    private boolean isActive;
//
//    private long createdAt;
//    private long updatedAt;
}
