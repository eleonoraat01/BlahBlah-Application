package com.example.blahblahapp.dtos.channel;

import com.example.blahblahapp.dtos.user.PartialUserDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChannelMemberDTO {
    private Long id;
    private PartialUserDTO user;
    private String role;
    private long joinedAt;

    @JsonProperty("isActive")
    @Column(name = "is_active")
    private boolean isActive;
}
