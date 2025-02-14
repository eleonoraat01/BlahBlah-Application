package com.example.blahblahapp.dtos.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FriendshipDTO {
    private Long id;
    private PartialUserDTO user;
    private PartialUserDTO friend;
    private long createdAt;
    private long updatedAt;
}
